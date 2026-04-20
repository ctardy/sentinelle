package app.sentinelle.data

import android.content.Context
import app.sentinelle.domain.DnsProfile
import app.sentinelle.domain.KnowledgeBaseIndex
import app.sentinelle.domain.Profile
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

class KnowledgeBaseRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun loadIndex(): KnowledgeBaseIndex =
        json.decodeFromString(readAsset("kb/v1/index.json"))

    fun loadProfile(profileId: String, language: String): Profile? =
        tryReadAsset("kb/v1/profiles/$profileId.$language.json")
            ?.let { json.decodeFromString<Profile>(it) }

    fun loadDns(country: String): DnsProfile? =
        tryReadAsset("kb/v1/dns/${country.lowercase()}.json")
            ?.let { json.decodeFromString<DnsProfile>(it) }

    private fun readAsset(path: String): String =
        context.assets.open(path).bufferedReader().use { it.readText() }

    private fun tryReadAsset(path: String): String? =
        try {
            readAsset(path)
        } catch (_: FileNotFoundException) {
            null
        }
}
