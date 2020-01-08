package com.betkey.data

import android.content.SharedPreferences
import com.betkey.utils.*

class PreferencesManager(private val pref: SharedPreferences) {

    fun saveToken(token: String?) {
        pref.edit().putString(TOKEN, token).apply()
    }

    fun getToken() = pref.getString(TOKEN, "")!!

    fun getId() = pref.getInt(AGENT_AGENTID, 0)

    fun saveAgent(id: String, agentId: Int, userName: String) {
        pref.edit().also {
            it.putString(AGENT_ID, id)
            it.putInt(AGENT_AGENTID, agentId)
            it.putString(AGENT_USERNAME, userName)
        }.apply()
    }

    fun getLanguage(): String {
        return pref.getString(LANGUAGE_KEY, LocaleManager.DEFAULT_LANGUAGE) ?: LocaleManager.DEFAULT_LANGUAGE
    }

    fun persistLanguage(language: String) {
        pref.edit().putString(LANGUAGE_KEY, language).apply()
    }
}