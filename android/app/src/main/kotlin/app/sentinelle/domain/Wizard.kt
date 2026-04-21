package app.sentinelle.domain

enum class Audience {
    Self,
    Child,
    Senior,
    Gift,
    Mixed,
    SuspectApp;

    companion object {
        fun fromStorage(value: String?): Audience? = value?.let {
            runCatching { valueOf(it) }.getOrNull()
        }
    }
}

enum class Pace {
    Quick,
    Full;

    companion object {
        fun fromStorage(value: String?): Pace? = value?.let {
            runCatching { valueOf(it) }.getOrNull()
        }
    }
}

fun reorderCategories(categories: List<Category>, audience: Audience): List<Category> {
    if (audience == Audience.SuspectApp) {
        return categories.filter { it.categoryId == "permissions" }
    }
    val priority = when (audience) {
        Audience.Self -> return categories
        Audience.Child -> listOf("permissions", "reseau", "identite", "assistants")
        Audience.Senior -> listOf("permissions", "reseau", "identite", "diagnostics")
        Audience.Gift -> listOf("identite", "samsung-account", "reseau")
        Audience.Mixed -> listOf("permissions", "reseau", "identite", "samsung-account")
        Audience.SuspectApp -> emptyList()
    }
    val prioritySet = priority.toSet()
    val front = priority.mapNotNull { id -> categories.firstOrNull { it.categoryId == id } }
    val rest = categories.filter { it.categoryId !in prioritySet }
    return front + rest
}

fun filterByPace(categories: List<Category>, pace: Pace): List<Category> = when (pace) {
    Pace.Full -> categories
    Pace.Quick -> categories.mapNotNull { cat ->
        val criticals = cat.checks.filter { it.severity == Severity.Critical }
        if (criticals.isEmpty()) null else cat.copy(checks = criticals)
    }
}

fun adaptProfile(profile: Profile, audience: Audience, pace: Pace): Profile {
    val adapted = filterByPace(reorderCategories(profile.categories, audience), pace)
    return profile.copy(categories = adapted)
}

fun countChecks(profile: Profile, audience: Audience, pace: Pace): Int =
    adaptProfile(profile, audience, pace).categories.sumOf { it.checks.size }
