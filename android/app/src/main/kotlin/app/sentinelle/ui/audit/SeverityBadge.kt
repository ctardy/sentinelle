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

@Composable
fun SeverityBadge(severity: Severity, modifier: Modifier = Modifier) {
    val (label, bg, fg) = when (severity) {
        Severity.Critical -> Triple(stringResource(R.string.severity_critical), Color(0xFFB91C1C), Color.White)
        Severity.Important -> Triple(stringResource(R.string.severity_important), Color(0xFFD97706), Color.White)
        Severity.Recommended -> Triple(stringResource(R.string.severity_recommended), Color(0xFF475569), Color.White)
    }
    Text(
        text = label,
        color = fg,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
