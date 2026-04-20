package app.sentinelle.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.sentinelle.R
import app.sentinelle.data.DeviceDetector
import app.sentinelle.data.DeviceInfo
import app.sentinelle.data.KnowledgeBaseRepository
import app.sentinelle.data.UserPreferencesRepository
import app.sentinelle.domain.Profile
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onStart: (profileId: String, country: String) -> Unit) {
    val context = LocalContext.current
    val repo = remember { KnowledgeBaseRepository(context) }
    val prefs = remember { UserPreferencesRepository(context) }
    val scope = rememberCoroutineScope()
    val device = remember { DeviceDetector.detect() }
    val language = remember { Locale.getDefault().language.ifEmpty { "fr" } }

    val state by produceState<OnboardingState?>(initialValue = null, key1 = language) {
        val index = repo.loadIndex()
        val profiles = index.profiles
            .mapNotNull { entry -> repo.loadProfile(entry.profileId, language) ?: repo.loadProfile(entry.profileId, "fr") }
        value = OnboardingState(
            device = device,
            profiles = profiles,
            countries = index.dnsCountries,
            selectedProfileId = device.suggestedProfileId.takeIf { id -> profiles.any { it.profileId == id } }
                ?: profiles.firstOrNull()?.profileId,
            selectedCountry = index.defaultDnsCountry,
        )
    }

    val current = state ?: return LoadingScaffold()
    var selectedProfileId by remember(current) { mutableStateOf(current.selectedProfileId) }
    var selectedCountry by remember(current) { mutableStateOf(current.selectedCountry) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(R.string.home_tagline),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )

            Text(
                text = stringResource(R.string.onboarding_detected_device),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 24.dp),
            )
            Text(
                text = detectedDeviceLabel(current.device),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(R.string.onboarding_choose_profile),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
            current.profiles.forEach { profile ->
                ProfileRow(
                    profile = profile,
                    selected = profile.profileId == selectedProfileId,
                    onSelect = { selectedProfileId = profile.profileId },
                )
            }

            Text(
                text = stringResource(R.string.onboarding_choose_country),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                current.countries.forEach { country ->
                    FilterChip(
                        selected = country == selectedCountry,
                        onClick = { selectedCountry = country },
                        label = { Text(country) },
                        colors = FilterChipDefaults.filterChipColors(),
                    )
                }
            }

            Button(
                onClick = {
                    val profileId = selectedProfileId ?: return@Button
                    val country = selectedCountry ?: return@Button
                    scope.launch {
                        prefs.setSelection(profileId, country)
                        onStart(profileId, country)
                    }
                },
                enabled = selectedProfileId != null && selectedCountry != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            ) {
                Text(stringResource(R.string.onboarding_start_audit))
            }
        }
    }
}

@Composable
private fun ProfileRow(profile: Profile, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = profile.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp),
        )
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

private fun detectedDeviceLabel(device: DeviceInfo): String {
    val parts = listOf(device.manufacturer, device.model, "Android ${device.sdkInt}")
        .filter { it.isNotBlank() }
    return parts.joinToString(" · ")
}

private data class OnboardingState(
    val device: DeviceInfo,
    val profiles: List<Profile>,
    val countries: List<String>,
    val selectedProfileId: String?,
    val selectedCountry: String?,
)

// Notifie Compose que cette composition peut effectuer une suspension via LaunchedEffect
// si besoin futur de rafraîchir l'état après écriture en DataStore.
@Suppress("unused")
@Composable
private fun NoopLaunchedEffect() {
    LaunchedEffect(Unit) { }
}
