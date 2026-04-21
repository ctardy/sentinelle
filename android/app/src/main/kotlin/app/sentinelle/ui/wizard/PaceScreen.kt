package app.sentinelle.ui.wizard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.sentinelle.R
import app.sentinelle.data.KnowledgeBaseRepository
import app.sentinelle.data.UserPreferencesRepository
import app.sentinelle.data.UserSelection
import app.sentinelle.domain.Audience
import app.sentinelle.domain.Pace
import app.sentinelle.domain.Profile
import app.sentinelle.domain.countChecks
import androidx.compose.runtime.collectAsState
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun PaceScreen(onBack: () -> Unit, onNext: () -> Unit) {
    val context = LocalContext.current
    val kb = remember { KnowledgeBaseRepository(context) }
    val prefs = remember { UserPreferencesRepository(context) }
    val scope = rememberCoroutineScope()
    val language = remember { Locale.getDefault().language.ifEmpty { "en" } }

    val selection by prefs.selection.collectAsState(initial = UserSelection(null, null, null, null))
    val profileId = selection.profileId
    val audience = selection.audience ?: Audience.Self

    val profile by produceState<Profile?>(initialValue = null, key1 = profileId, key2 = language) {
        val id = profileId ?: return@produceState
        value = kb.loadProfile(id, language) ?: kb.loadProfile(id, "en")
    }

    val loaded = profile
    WizardStepScaffold(step = 5, onBack = onBack) {
        Text(
            text = stringResource(R.string.pace_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        )

        val quickCount = loaded?.let { countChecks(it, audience, Pace.Quick) } ?: 0
        val fullCount = loaded?.let { countChecks(it, audience, Pace.Full) } ?: 0

        PaceCard(
            title = stringResource(R.string.pace_quick),
            description = stringResource(R.string.pace_quick_desc),
            checksCount = quickCount,
            onClick = {
                scope.launch {
                    prefs.setPace(Pace.Quick)
                    onNext()
                }
            },
        )
        PaceCard(
            title = stringResource(R.string.pace_full),
            description = stringResource(R.string.pace_full_desc),
            checksCount = fullCount,
            onClick = {
                scope.launch {
                    prefs.setPace(Pace.Full)
                    onNext()
                }
            },
        )
    }
}

@Composable
private fun PaceCard(title: String, description: String, checksCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            if (checksCount > 0) {
                Text(
                    text = stringResource(R.string.pace_checks_count, checksCount),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
