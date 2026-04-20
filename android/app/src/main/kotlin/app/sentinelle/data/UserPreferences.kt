package app.sentinelle.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sentinelle.preferences")

data class UserSelection(
    val profileId: String?,
    val country: String?,
)

class UserPreferencesRepository(private val context: Context) {

    private val keyProfileId: Preferences.Key<String> = stringPreferencesKey("profileId")
    private val keyCountry: Preferences.Key<String> = stringPreferencesKey("country")

    val selection: Flow<UserSelection> = context.dataStore.data.map { prefs ->
        UserSelection(
            profileId = prefs[keyProfileId],
            country = prefs[keyCountry],
        )
    }

    suspend fun setSelection(profileId: String, country: String) {
        context.dataStore.edit { prefs ->
            prefs[keyProfileId] = profileId
            prefs[keyCountry] = country
        }
    }
}
