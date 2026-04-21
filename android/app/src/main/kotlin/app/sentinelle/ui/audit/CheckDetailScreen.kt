package app.sentinelle.ui.audit

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.sentinelle.R
import app.sentinelle.data.CompletionRepository
import app.sentinelle.data.KnowledgeBaseRepository
import app.sentinelle.domain.Check
import app.sentinelle.domain.DnsProfile
import app.sentinelle.domain.IntentDescriptor
import app.sentinelle.domain.Profile
import app.sentinelle.domain.Step
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun CheckDetailScreen(
    profileId: String,
    categoryId: String,
    checkId: String,
    country: String,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val kb = remember { KnowledgeBaseRepository(context) }
    val completion = remember { CompletionRepository(context) }
    val scope = rememberCoroutineScope()
    val language = remember { Locale.getDefault().language.ifEmpty { "en" } }

    val profile by produceState<Profile?>(initialValue = null, key1 = profileId, key2 = language) {
        value = kb.loadProfile(profileId, language) ?: kb.loadProfile(profileId, "en")
    }
    val dns by produceState<DnsProfile?>(initialValue = null, key1 = country) {
        value = kb.loadDns(country)
    }
    val completed by completion.completedCheckIds(profileId).collectAsState(initial = emptySet())

    val loadedProfile = profile ?: return
    val check = findCheck(loadedProfile, categoryId, checkId) ?: return

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    text = check.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f),
                )
                SeverityBadge(check.severity)
            }

            SectionLabel(stringResource(R.string.audit_risk))
            Text(check.risk, style = MaterialTheme.typography.bodyMedium)

            check.whyItMatters?.let { why ->
                SectionLabel(stringResource(R.string.audit_why_it_matters))
                Text(why, style = MaterialTheme.typography.bodyMedium)
            }

            SectionLabel(stringResource(R.string.audit_steps))
            check.steps.forEachIndexed { index, step ->
                StepCard(index = index + 1, step = step) { desc -> openIntent(context, desc) }
            }

            if (check.dnsOptionsCountryFile != null) {
                SectionLabel(stringResource(R.string.audit_dns_recommended, country))
                dns?.servers?.forEach { server ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(server.hostname, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "${server.operator} · ${server.jurisdiction}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            val isDone = check.checkId in completed
            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                onClick = {
                    scope.launch {
                        completion.setCompleted(profileId, check.checkId, !isDone)
                        if (!isDone) onDone()
                    }
                },
            ) {
                Text(
                    if (isDone) stringResource(R.string.audit_marked_done)
                    else stringResource(R.string.audit_mark_done)
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
    )
}

@Composable
private fun StepCard(index: Int, step: Step, onOpenIntent: (IntentDescriptor) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("$index. ${step.label}", style = MaterialTheme.typography.bodyLarge)
            step.fallbackPath?.let {
                Text(
                    text = "${stringResource(R.string.audit_fallback_path)} : $it",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            step.intent?.let { intent ->
                OutlinedButton(onClick = { onOpenIntent(intent) }) {
                    Text(stringResource(R.string.audit_open_settings))
                }
            }
        }
    }
}

private fun findCheck(profile: Profile, categoryId: String, checkId: String): Check? {
    val category = profile.categories.firstOrNull { it.categoryId == categoryId } ?: return null
    return category.checks.firstOrNull { it.checkId == checkId }
}

private fun openIntent(context: Context, descriptor: IntentDescriptor) {
    val intent = Intent(descriptor.action).apply {
        descriptor.data?.let { data = Uri.parse(it) }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.audit_not_available),
            Toast.LENGTH_SHORT,
        ).show()
    }
}
