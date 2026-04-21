package app.sentinelle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.sentinelle.data.DeviceDetector
import app.sentinelle.data.KnowledgeBaseRepository
import app.sentinelle.data.UserPreferencesRepository
import app.sentinelle.data.UserSelection
import app.sentinelle.domain.Audience
import app.sentinelle.domain.Pace
import app.sentinelle.ui.audit.AuditScreen
import app.sentinelle.ui.audit.CheckDetailScreen
import app.sentinelle.ui.theme.SentinelleTheme
import app.sentinelle.ui.wizard.AudienceScreen
import app.sentinelle.ui.wizard.CountryScreen
import app.sentinelle.ui.wizard.DeviceScreen
import app.sentinelle.ui.wizard.PaceScreen
import app.sentinelle.ui.wizard.RecapScreen
import app.sentinelle.ui.wizard.WelcomeScreen
import kotlinx.coroutines.launch

private const val RouteWelcome = "wizard/welcome"
private const val RouteAudience = "wizard/audience"
private const val RouteDevice = "wizard/device"
private const val RouteCountry = "wizard/country"
private const val RoutePace = "wizard/pace"
private const val RouteRecap = "wizard/recap"
private const val RouteAudit = "audit/{profileId}/{country}"
private const val RouteDetail = "detail/{profileId}/{country}/{categoryId}/{checkId}"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SentinelleTheme {
                AppNav()
            }
        }
    }
}

@Composable
private fun AppNav() {
    val context = LocalContext.current
    val prefs = remember { UserPreferencesRepository(context) }
    val kb = remember { KnowledgeBaseRepository(context) }
    val scope = rememberCoroutineScope()

    val selection by prefs.selection.collectAsState(
        initial = UserSelection(null, null, null, null),
    )
    val startRoute = if (selection.isComplete) {
        "audit/${selection.profileId}/${selection.country}"
    } else {
        RouteWelcome
    }

    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = startRoute) {
        composable(RouteWelcome) {
            WelcomeScreen(
                onStart = { nav.navigate(RouteAudience) },
                onSkipIntro = {
                    scope.launch {
                        val device = DeviceDetector.detect()
                        val index = kb.loadIndex()
                        val profileId = device.suggestedProfileId
                        val country = index.defaultDnsCountry
                        prefs.setAll(profileId, country, Audience.Self, Pace.Full)
                        nav.navigate("audit/$profileId/$country") {
                            popUpTo(RouteWelcome) { inclusive = true }
                        }
                    }
                },
            )
        }
        composable(RouteAudience) {
            AudienceScreen(
                onBack = { nav.popBackStack() },
                onNext = { nav.navigate(RouteDevice) },
            )
        }
        composable(RouteDevice) {
            DeviceScreen(
                onBack = { nav.popBackStack() },
                onNext = { nav.navigate(RouteCountry) },
            )
        }
        composable(RouteCountry) {
            CountryScreen(
                onBack = { nav.popBackStack() },
                onNext = { nav.navigate(RoutePace) },
            )
        }
        composable(RoutePace) {
            PaceScreen(
                onBack = { nav.popBackStack() },
                onNext = { nav.navigate(RouteRecap) },
            )
        }
        composable(RouteRecap) {
            RecapScreen(
                onBack = { nav.popBackStack() },
                onLaunch = { profileId, country ->
                    nav.navigate("audit/$profileId/$country") {
                        popUpTo(RouteWelcome) { inclusive = true }
                    }
                },
                onModify = { nav.popBackStack(RouteAudience, inclusive = false) },
            )
        }
        composable(RouteAudit) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId").orEmpty()
            val country = backStackEntry.arguments?.getString("country").orEmpty()
            AuditScreen(
                profileId = profileId,
                country = country,
                onCheckClick = { categoryId, checkId ->
                    nav.navigate("detail/$profileId/$country/$categoryId/$checkId")
                },
                onCountryChange = { newCountry ->
                    nav.navigate("audit/$profileId/$newCountry") {
                        popUpTo(RouteAudit) { inclusive = true }
                    }
                },
                onRestartWizard = {
                    scope.launch {
                        prefs.clear()
                        nav.navigate(RouteWelcome) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
            )
        }
        composable(RouteDetail) { backStackEntry ->
            val args = backStackEntry.arguments
            CheckDetailScreen(
                profileId = args?.getString("profileId").orEmpty(),
                country = args?.getString("country").orEmpty(),
                categoryId = args?.getString("categoryId").orEmpty(),
                checkId = args?.getString("checkId").orEmpty(),
                onDone = { nav.popBackStack() },
            )
        }
    }
}
