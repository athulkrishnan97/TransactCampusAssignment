import PreferenceManager.Companion.DATA_STORE_NAME
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * This class is responsible for setting up the Datastore. The datastore is used to save the previously
 * selected author for filtering.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATA_STORE_NAME)

class PreferenceManager(context: Context) {
    companion object{
        const val DATA_STORE_NAME = "settings"
        const val AUTHOR_FILTER_PREFERENCE_KEY = "filter_preference"
        const val DEFAULT_AUTHOR_FILTER = "None"
    }
    private val dataStore = context.dataStore

    // Define preference key
    private val filterPreferenceKey = stringPreferencesKey(AUTHOR_FILTER_PREFERENCE_KEY)

    // Store preference
    suspend fun setFilterPreference(value: String) {
        dataStore.edit { preferences ->
            preferences[filterPreferenceKey] = value
        }
    }

    // Retrieve preference
    val filterPreferenceFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[filterPreferenceKey] ?: DEFAULT_AUTHOR_FILTER
        }
}
