package com.example.tourtest.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_session")


class UserSession(
    private val context: Context
) {
    private val USER_ID_KEY = stringPreferencesKey(name = "user_id")

    val userId: Flow<String?> = context.dataStore.data.map { preferences -> preferences[USER_ID_KEY] }

    suspend fun saveSession(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }
}