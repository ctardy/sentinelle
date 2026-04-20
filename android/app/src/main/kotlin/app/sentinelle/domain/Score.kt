package app.sentinelle.domain

private fun Severity.weight(): Int = when (this) {
    Severity.Critical -> 5
    Severity.Important -> 3
    Severity.Recommended -> 1
}

data class AuditProgress(
    val completedCount: Int,
    val totalCount: Int,
    val score: Int,
)

fun computeProgress(profile: Profile, completedIds: Set<String>): AuditProgress {
    val allChecks = profile.categories.flatMap { it.checks }
    val total = allChecks.size
    val completed = allChecks.count { it.checkId in completedIds }
    val maxPoints = allChecks.sumOf { it.severity.weight() }
    val earned = allChecks.filter { it.checkId in completedIds }.sumOf { it.severity.weight() }
    val score = if (maxPoints == 0) 0 else (earned * 100) / maxPoints
    return AuditProgress(completed, total, score)
}
