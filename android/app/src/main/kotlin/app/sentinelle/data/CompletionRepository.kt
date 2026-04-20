package app.sentinelle.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.completionStore by preferencesDataStore(name = "sentinelle.completion")

class CompletionRepository(private val context: Context) {

    private fun key(profileId: String): Preferences.Key<Set<String>> =
        stringSetPreferencesKey("done.$profileId")

    fun completedCheckIds(profileId: String): Flow<Set<String>> =
        context.completionStore.data.map { prefs -> prefs[key(profileId)].orEmpty() }

    suspend fun setCompleted(profileId: String, checkId: String, completed: Boolean) {
        context.completionStore.edit { prefs ->
            val current = prefs[key(profileId)].orEmpty()
            prefs[key(profileId)] = if (completed) current + checkId else current - checkId
        }
    }

    suspend fun reset(profileId: String) {
        context.completionStore.edit { prefs -> prefs.remove(key(profileId)) }
    }
}
