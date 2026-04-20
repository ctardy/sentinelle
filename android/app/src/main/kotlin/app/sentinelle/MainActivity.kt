package app.sentinelle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.sentinelle.data.UserPreferencesRepository
import app.sentinelle.ui.audit.AuditScreen
import app.sentinelle.ui.audit.CheckDetailScreen
import app.sentinelle.ui.onboarding.OnboardingScreen
import app.sentinelle.ui.theme.SentinelleTheme
import kotlinx.coroutines.flow.map

private const val RouteOnboarding = "onboarding"
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
    val startRoute by prefs.selection
        .map { sel ->
            if (sel.profileId != null && sel.country != null) {
                "audit/${sel.profileId}/${sel.country}"
            } else {
                RouteOnboarding
            }
        }
        .collectAsState(initial = RouteOnboarding)

    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = startRoute) {
        composable(RouteOnboarding) {
            OnboardingScreen(onStart = { profileId, country ->
                nav.navigate("audit/$profileId/$country") {
                    popUpTo(RouteOnboarding) { inclusive = true }
                }
            })
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
