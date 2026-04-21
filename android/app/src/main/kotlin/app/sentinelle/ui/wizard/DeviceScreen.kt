package app.sentinelle.ui.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import app.sentinelle.data.UserSelection
import app.sentinelle.domain.Audience
import app.sentinelle.domain.Profile
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun DeviceScreen(onBack: () -> Unit, onNext: () -> Unit) {
    val context = LocalContext.current
    val kb = remember { KnowledgeBaseRepository(context) }
    val prefs = remember { UserPreferencesRepository(context) }
    val scope = rememberCoroutineScope()
    val device = remember { DeviceDetector.detect() }
    val language = remember { Locale.getDefault().language.ifEmpty { "en" } }

    val selection by prefs.selection.collectAsState(
        initial = UserSelection(null, null, null, null),
    )
    val audience = selection.audience ?: Audience.Self
    val auditingOwnPhone = audience == Audience.Self || audience == Audience.SuspectApp

    val profiles by produceState<List<Profile>>(initialValue = emptyList(), key1 = language) {
        val index = kb.loadIndex()
        value = index.profiles.mapNotNull { entry ->
            kb.loadProfile(entry.profileId, language) ?: kb.loadProfile(entry.profileId, "en")
        }
    }

    // Le picker est fermé par défaut quand l'utilisateur audite son propre téléphone
    // (la détection suffit), ouvert par défaut quand il audite un autre appareil.
    var showPicker by remember(auditingOwnPhone) { mutableStateOf(!auditingOwnPhone) }
    var pickedProfileId by remember(profiles) {
        mutableStateOf(
            device.suggestedProfileId.takeIf { id -> profiles.any { it.profileId == id } }
                ?: profiles.firstOrNull()?.profileId,
        )
    }

    WizardStepScaffold(step = 3, onBack = onBack) {
        Text(
            text = stringResource(
                if (auditingOwnPhone) R.string.device_title
                else R.string.device_title_audit_other,
            ),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        )

        if (auditingOwnPhone) {
            Text(
                text = stringResource(R.string.device_detected),
                style = MaterialTheme.typography.labelLarge,
            )
            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(
                    text = detectedDeviceLabel(device),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp),
                )
            }
            val suggestedProfile = profiles.firstOrNull { it.profileId == device.suggestedProfileId }
                ?: profiles.firstOrNull { it.profileId == "android-generic" }
            if (suggestedProfile != null && !showPicker) {
                Text(
                    text = stringResource(R.string.device_suggested_profile, suggestedProfile.label),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        } else {
            Text(
                text = stringResource(R.string.device_hint_audit_other),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        if (showPicker) {
            if (auditingOwnPhone) {
                Text(
                    text = stringResource(R.string.device_pick_manufacturer),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
            }
            profiles.forEach { profile ->
                ProfileRow(
                    profile = profile,
                    selected = profile.profileId == pickedProfileId,
                    isCurrentDevice = profile.profileId == device.suggestedProfileId,
                    onSelect = { pickedProfileId = profile.profileId },
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (auditingOwnPhone) {
                OutlinedButton(
                    onClick = { showPicker = !showPicker },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        stringResource(
                            if (showPicker) R.string.device_hide_picker
                            else R.string.device_audit_another,
                        ),
                    )
                }
            }
            Button(
                onClick = {
                    val id = pickedProfileId ?: return@Button
                    scope.launch {
                        prefs.setProfileId(id)
                        onNext()
                    }
                },
                enabled = pickedProfileId != null,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.device_confirm))
            }
        }
    }
}

@Composable
private fun ProfileRow(
    profile: Profile,
    selected: Boolean,
    isCurrentDevice: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = profile.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp),
        )
        if (isCurrentDevice) {
            Text(
                text = stringResource(R.string.device_this_phone_badge),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(6.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            )
        }
    }
}

private fun detectedDeviceLabel(device: DeviceInfo): String {
    val parts = listOf(device.manufacturer, device.model, "Android ${device.sdkInt}")
        .filter { it.isNotBlank() }
    return parts.joinToString(" · ")
}
