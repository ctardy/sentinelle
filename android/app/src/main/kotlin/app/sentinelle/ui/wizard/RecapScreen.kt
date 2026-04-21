package app.sentinelle.ui.wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
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
import java.util.Locale

@Composable
fun RecapScreen(
    onBack: () -> Unit,
    onLaunch: (profileId: String, country: String) -> Unit,
    onModify: () -> Unit,
) {
    val context = LocalContext.current
    val kb = remember { KnowledgeBaseRepository(context) }
    val prefs = remember { UserPreferencesRepository(context) }
    val language = remember { Locale.getDefault().language.ifEmpty { "en" } }

    val selection by prefs.selection.collectAsState(initial = UserSelection(null, null, null, null))
    val profile by produceState<Profile?>(initialValue = null, key1 = selection.profileId, key2 = language) {
        val id = selection.profileId ?: return@produceState
        value = kb.loadProfile(id, language) ?: kb.loadProfile(id, "en")
    }

    val loaded = profile
    val audience = selection.audience ?: Audience.Self
    val pace = selection.pace ?: Pace.Full
    val checksCount = loaded?.let { countChecks(it, audience, pace) } ?: 0

    WizardStepScaffold(step = 6, onBack = onBack) {
        Text(
            text = stringResource(R.string.recap_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                RecapLine(
                    label = stringResource(R.string.recap_profile),
                    value = loaded?.label.orEmpty(),
                )
                RecapLine(
                    label = stringResource(R.string.recap_country),
                    value = selection.country.orEmpty(),
                )
                RecapLine(
                    label = stringResource(R.string.recap_audience),
                    value = audienceLabel(audience),
                )
                RecapLine(
                    label = stringResource(R.string.recap_pace),
                    value = paceLabel(pace),
                )
            }
        }
        Text(
            text = stringResource(R.string.recap_summary, checksCount),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            text = stringResource(R.string.recap_privacy),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = {
                val profileId = selection.profileId ?: return@Button
                val country = selection.country ?: return@Button
                onLaunch(profileId, country)
            },
            enabled = selection.isComplete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
        ) {
            Text(stringResource(R.string.recap_launch))
        }
        TextButton(
            onClick = onModify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp),
        ) {
            Text(stringResource(R.string.recap_modify))
        }
    }
}

@Composable
private fun RecapLine(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun audienceLabel(audience: Audience): String = stringResource(
    when (audience) {
        Audience.Self -> R.string.audience_self
        Audience.Child -> R.string.audience_child
        Audience.Senior -> R.string.audience_senior
        Audience.Gift -> R.string.audience_gift
        Audience.Mixed -> R.string.audience_mixed
        Audience.SuspectApp -> R.string.audience_suspect
    },
)

@Composable
private fun paceLabel(pace: Pace): String = stringResource(
    when (pace) {
        Pace.Quick -> R.string.pace_quick
        Pace.Full -> R.string.pace_full
    },
)
