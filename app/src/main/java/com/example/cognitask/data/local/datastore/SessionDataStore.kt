package com.example.cognitask.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val USER_ID      = longPreferencesKey("user_id")
        val ENERGY_LEVEL = intPreferencesKey("energy_level")
    }

    val userId: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_ID] ?: -1L
    }

    val energyLevel: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.ENERGY_LEVEL] ?: 5
    }

    suspend fun saveSession(userId: Long, energyLevel: Int = 5) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID]      = userId
            prefs[Keys.ENERGY_LEVEL] = energyLevel
        }
    }

    suspend fun updateEnergyLevel(level: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ENERGY_LEVEL] = level.coerceIn(1, 10)
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.ENERGY_LEVEL)
        }
    }
}