package app.sentinelle.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Severity {
    @SerialName("critical") Critical,
    @SerialName("important") Important,
    @SerialName("recommended") Recommended,
}

@Serializable
enum class LogsPolicy {
    @SerialName("no-logs") NoLogs,
    @SerialName("minimal") Minimal,
    @SerialName("unknown") Unknown,
}

@Serializable
data class AndroidVersionRange(
    val minSdk: Int,
    val maxSdk: Int,
)

@Serializable
data class IntentDescriptor(
    val action: String,
    val data: String? = null,
)

@Serializable
data class Step(
    val label: String,
    val intent: IntentDescriptor? = null,
    val fallbackPath: String? = null,
)

@Serializable
data class AutoDetect(
    val kind: String,
    val target: String,
)

@Serializable
data class Check(
    val checkId: String,
    val title: String,
    val risk: String,
    val whyItMatters: String? = null,
    val severity: Severity,
    val autoDetect: AutoDetect? = null,
    val steps: List<Step>,
    val dnsOptionsCountryFile: String? = null,
)

@Serializable
data class Category(
    val categoryId: String,
    val label: String,
    val description: String? = null,
    val checks: List<Check>,
)

@Serializable
data class Profile(
    val profileId: String,
    val language: String,
    val label: String,
    val manufacturer: String,
    val androidVersions: List<AndroidVersionRange>,
    val categories: List<Category>,
)

@Serializable
data class DnsServer(
    val hostname: String,
    val operator: String,
    val jurisdiction: String,
    val nonProfit: Boolean = false,
    val filtersMalware: Boolean = false,
    val logsPolicy: LogsPolicy = LogsPolicy.Unknown,
    val notesKey: String? = null,
)

@Serializable
data class DnsProfile(
    val country: String,
    val servers: List<DnsServer>,
)

@Serializable
data class ProfileIndexEntry(
    val profileId: String,
    val manufacturer: String,
    val languages: List<String>,
    val androidVersions: List<AndroidVersionRange>,
)

@Serializable
data class KnowledgeBaseIndex(
    val schemaVersion: String,
    val updatedAt: String,
    val profiles: List<ProfileIndexEntry>,
    val dnsCountries: List<String>,
    val defaultDnsCountry: String,
)
