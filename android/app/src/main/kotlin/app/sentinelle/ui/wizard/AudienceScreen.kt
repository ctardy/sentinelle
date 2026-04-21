package app.sentinelle.ui.wizard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.sentinelle.R
import app.sentinelle.data.UserPreferencesRepository
import app.sentinelle.domain.Audience
import kotlinx.coroutines.launch

@Composable
fun AudienceScreen(onBack: () -> Unit, onNext: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { UserPreferencesRepository(context) }
    val scope = rememberCoroutineScope()

    WizardStepScaffold(step = 2, onBack = onBack) {
        Text(
            text = stringResource(R.string.audience_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        )
        Audience.entries.forEach { audience ->
            AudienceCard(audience = audience) {
                scope.launch {
                    prefs.setAudience(audience)
                    onNext()
                }
            }
        }
    }
}

@Composable
private fun AudienceCard(audience: Audience, onClick: () -> Unit) {
    val labels = audienceStrings(audience)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(
                text = labels.emoji,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(end = 12.dp),
            )
            Column {
                Text(stringResource(labels.title), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(labels.desc),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

private data class AudienceLabels(val emoji: String, val title: Int, val desc: Int)

private fun audienceStrings(audience: Audience): AudienceLabels = when (audience) {
    Audience.Self -> AudienceLabels("\uD83D\uDC64", R.string.audience_self, R.string.audience_self_desc)
    Audience.Child -> AudienceLabels("\uD83D\uDC76", R.string.audience_child, R.string.audience_child_desc)
    Audience.Senior -> AudienceLabels("\uD83D\uDC75", R.string.audience_senior, R.string.audience_senior_desc)
    Audience.Gift -> AudienceLabels("\uD83C\uDF81", R.string.audience_gift, R.string.audience_gift_desc)
    Audience.Mixed -> AudienceLabels("\uD83D\uDCBC", R.string.audience_mixed, R.string.audience_mixed_desc)
    Audience.SuspectApp -> AudienceLabels("\uD83D\uDD12", R.string.audience_suspect, R.string.audience_suspect_desc)
}
