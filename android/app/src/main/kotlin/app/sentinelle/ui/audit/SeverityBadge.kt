package app.sentinelle.ui.audit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.sentinelle.R
import app.sentinelle.domain.Severity
import app.sentinelle.ui.theme.SeverityCritical
import app.sentinelle.ui.theme.SeverityImportant
import app.sentinelle.ui.theme.SeverityRecommended

@Composable
fun SeverityBadge(severity: Severity, modifier: Modifier = Modifier) {
    val bg = when (severity) {
        Severity.Critical -> SeverityCritical
        Severity.Important -> SeverityImportant
        Severity.Recommended -> SeverityRecommended
    }
    val label = when (severity) {
        Severity.Critical -> stringResource(R.string.severity_critical)
        Severity.Important -> stringResource(R.string.severity_important)
        Severity.Recommended -> stringResource(R.string.severity_recommended)
    }
    Text(
        text = label,
        color = Color(0xFF0F1419),
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
