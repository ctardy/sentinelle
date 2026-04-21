package app.sentinelle.ui.audit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.sentinelle.R

@Composable
fun CountryPickerDialog(
    countries: List<String>,
    selectedCountry: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.audit_country_picker_title)) },
        text = {
            Column {
                countries.forEach { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = country == selectedCountry,
                                onClick = { onSelect(country) },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = country == selectedCountry,
                            onClick = null,
                        )
                        Text(
                            text = countryLabel(country),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 12.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.wizard_back)) }
        },
    )
}

fun countryLabel(code: String): String = when (code.uppercase()) {
    "FR" -> "\uD83C\uDDEB\uD83C\uDDF7 FR"
    "DE" -> "\uD83C\uDDE9\uD83C\uDDEA DE"
    "ES" -> "\uD83C\uDDEA\uD83C\uDDF8 ES"
    "GB" -> "\uD83C\uDDEC\uD83C\uDDE7 GB"
    "EU" -> "\uD83C\uDDEA\uD83C\uDDFA EU"
    else -> code
}
