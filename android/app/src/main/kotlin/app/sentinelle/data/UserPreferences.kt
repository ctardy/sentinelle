package app.sentinelle.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.sentinelle.domain.Audience
import app.sentinelle.domain.Pace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sentinelle.preferences")

data class UserSelection(
    val profileId: String?,
    val country: String?,
    val audience: Audience?,
    val pace: Pace?,
) {
    val isComplete: Boolean
        get() = profileId != null && country != null && audience != null && pace != null
}

class UserPreferencesRepository(private val context: Context) {

    private val keyProfileId: Preferences.Key<String> = stringPreferencesKey("profileId")
    private val keyCountry: Preferences.Key<String> = stringPreferencesKey("country")
    private val keyAudience: Preferences.Key<String> = stringPreferencesKey("audience")
    private val keyPace: Preferences.Key<String> = stringPreferencesKey("pace")

    val selection: Flow<UserSelection> = context.dataStore.data.map { prefs ->
        UserSelection(
            profileId = prefs[keyProfileId],
            country = prefs[keyCountry],
            audience = Audience.fromStorage(prefs[keyAudience]),
            pace = Pace.fromStorage(prefs[keyPace]),
        )
    }

    suspend fun setProfileId(value: String) = context.dataStore.edit { it[keyProfileId] = value }
    suspend fun setCountry(value: String) = context.dataStore.edit { it[keyCountry] = value }
    suspend fun setAudience(value: Audience) = context.dataStore.edit { it[keyAudience] = value.name }
    suspend fun setPace(value: Pace) = context.dataStore.edit { it[keyPace] = value.name }

    suspend fun setAll(profileId: String, country: String, audience: Audience, pace: Pace) {
        context.dataStore.edit { prefs ->
            prefs[keyProfileId] = profileId
            prefs[keyCountry] = country
            prefs[keyAudience] = audience.name
            prefs[keyPace] = pace.name
        }
    }

    suspend fun clear() = context.dataStore.edit { it.clear() }
}
