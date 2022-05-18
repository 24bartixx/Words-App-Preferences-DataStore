package com.example.wordsapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// the name of Preferences Datastore that we instantiate
private const val LAYOUT_PREFERENCES_NAME = "layout_preferences"

// create a DataStore instance using preferencesDataStore delegate, with the Context as retriever
// outside class to access it DataStore though this property throughout the rest of the app
// This makes it easier to keep the DataStore as a singleton
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = LAYOUT_PREFERENCES_NAME)

class SettingsDataStore(context: Context) {

    // "is_linear_layout_manager parameter is the key name"
    private val IS_LINEAR_LAYOUT_MANAGER = booleanPreferencesKey("is_linear_layout_manager")

    // function updating the property with key IS_LINEAR_LAYOUT_MANAGER
    // suspend because edit() is a suspend function
    suspend fun saveLayoutToThePreferences(isLinearLayoutManager: Boolean, context: Context) {
        // edit() suspend function transactionally updates the data
        // all of the code in the transform block block is treated as a single transaction
        // under the hood the transaction work is moved to Dispacter.IO
        context.dataStore.edit { preferences ->
            preferences[IS_LINEAR_LAYOUT_MANAGER] = isLinearLayoutManager
        }
    }

    // function reading form the Preferences DataStore
    // Preferences DataStore exposes data stored in a Flow<Preferences> that emits every time a preference has changed
    // preferenceFlow property returns value of the data
    // function context.dataStore.data.map return value
    val preferenceFlow: Flow<Boolean> = context.dataStore.data
            // as DataStore reads and writes data from the files, IOException may occur
        .catch {
            if(it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[IS_LINEAR_LAYOUT_MANAGER] ?: true }

}