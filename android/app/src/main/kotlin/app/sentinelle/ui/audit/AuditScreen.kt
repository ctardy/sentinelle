package app.sentinelle.ui.audit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.sentinelle.R
import app.sentinelle.data.CompletionRepository
import app.sentinelle.data.KnowledgeBaseRepository
import app.sentinelle.data.UserPreferencesRepository
import app.sentinelle.data.UserSelection
import app.sentinelle.domain.Audience
import app.sentinelle.domain.Check
import app.sentinelle.domain.Pace
import app.sentinelle.domain.Profile
import app.sentinelle.domain.adaptProfile
import app.sentinelle.domain.computeProgress
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun AuditScreen(
    profileId: String,
    country: String,
    onCheckClick: (categoryId: String, checkId: String) -> Unit,
    onRestartWizard: () -> Unit,
) {
    val context = LocalContext.current
    val kb = remember { KnowledgeBaseRepository(context) }
    val completion = remember { CompletionRepository(context) }
    val prefs = remember { UserPreferencesRepository(context) }
    val language = remember { Locale.getDefault().language.ifEmpty { "en" } }
    val scope = rememberCoroutineScope()

    val profile by produceState<Profile?>(initialValue = null, key1 = profileId, key2 = language) {
        value = kb.loadProfile(profileId, language) ?: kb.loadProfile(profileId, "en")
    }
    val completed by completion.completedCheckIds(profileId).collectAsState(initial = emptySet())
    val selection by prefs.selection.collectAsState(
        initial = UserSelection(null, null, null, null),
    )

    val loaded = profile ?: return LoadingScaffold()
    val audience = selection.audience ?: Audience.Self
    val pace = selection.pace ?: Pace.Full
    val adapted = adaptProfile(loaded, audience, pace)
    val progress = computeProgress(adapted, completed)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(adapted.label, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = stringResource(R.string.audit_progress, progress.completedCount, progress.totalCount),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.audit_score, progress.score),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp),
            )

            adapted.categories.forEach { category ->
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
                category.description?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                }
                category.checks.forEach { check ->
                    CheckRow(
                        check = check,
                        done = check.checkId in completed,
                        onToggleDone = { newValue ->
                            scope.launch { completion.setCompleted(profileId, check.checkId, newValue) }
                        },
                        onClick = { onCheckClick(category.categoryId, check.checkId) },
                    )
                    HorizontalDivider()
                }
            }

            TextButton(
                onClick = onRestartWizard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
            ) {
                Text(stringResource(R.string.audit_restart_wizard))
            }
        }
    }
}

@Composable
private fun CheckRow(
    check: Check,
    done: Boolean,
    onToggleDone: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(checked = done, onCheckedChange = onToggleDone)
            Column(modifier = Modifier.weight(1f)) {
                Text(check.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    check.risk,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            SeverityBadge(check.severity)
        }
    }
}

@Composable
private fun LoadingScaffold() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
        }
    }
}
