package app.sentinelle.ui.wizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.sentinelle.R
import app.sentinelle.data.KnowledgeBaseRepository
import app.sentinelle.data.UserPreferencesRepository
import app.sentinelle.domain.KnowledgeBaseIndex
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun CountryScreen(onBack: () -> Unit, onNext: () -> Unit) {
    val context = LocalContext.current
    val kb = remember { KnowledgeBaseRepository(context) }
    val prefs = remember { UserPreferencesRepository(context) }
    val scope = rememberCoroutineScope()

    val index by produceState<KnowledgeBaseIndex?>(initialValue = null) {
        value = kb.loadIndex()
    }
    val suggested = remember {
        val localeCountry = Locale.getDefault().country.uppercase()
        localeCountry.ifEmpty { "EU" }
    }
    var selected by remember(index) {
        mutableStateOf(
            index?.let { idx ->
                idx.dnsCountries.firstOrNull { it == suggested } ?: idx.defaultDnsCountry
            },
        )
    }

    val loadedIndex = index ?: return
    WizardStepScaffold(step = 4, onBack = onBack) {
        Text(
            text = stringResource(R.string.country_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = stringResource(R.string.country_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            loadedIndex.dnsCountries.forEach { country ->
                FilterChip(
                    selected = country == selected,
                    onClick = { selected = country },
                    label = { Text(countryLabel(country)) },
                )
            }
        }
        Button(
            onClick = {
                val value = selected ?: return@Button
                scope.launch {
                    prefs.setCountry(value)
                    onNext()
                }
            },
            enabled = selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
        ) {
            Text(stringResource(R.string.wizard_next))
        }
    }
}

private fun countryLabel(code: String): String = when (code.uppercase()) {
    "FR" -> "🇫🇷 FR"
    "DE" -> "🇩🇪 DE"
    "ES" -> "🇪🇸 ES"
    "GB" -> "🇬🇧 GB"
    "EU" -> "🇪🇺 EU"
    else -> code
}
