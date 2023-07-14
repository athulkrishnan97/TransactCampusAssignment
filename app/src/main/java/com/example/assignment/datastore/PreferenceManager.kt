import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(context: Context) {
    private val dataStore = context.dataStore

    // Define preference key
    private val filterPreferenceKey = stringPreferencesKey("filter_preference")

    // Store preference
    suspend fun setFilterPreference(value: String) {
        dataStore.edit { preferences ->
            preferences[filterPreferenceKey] = value
        }
    }

    // Retrieve preference
    val filterPreferenceFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[filterPreferenceKey] ?: "None"
        }
}
