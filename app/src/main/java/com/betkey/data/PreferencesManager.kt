package com.betkey.data

import android.content.SharedPreferences
import com.betkey.utils.AGENT_AGENTID
import com.betkey.utils.AGENT_ID
import com.betkey.utils.AGENT_USERNAME
import com.betkey.utils.TOKEN

class PreferencesManager(private val pref: SharedPreferences) {

    fun saveToken(token: String?) {
        pref.edit().putString(TOKEN, token).apply()
    }

    fun getToken() = pref.getString(TOKEN, "")!!

    fun saveAgent(id: String, agentId: Int, userName: String) {
        pref.edit().also {
            it.putString(AGENT_ID, id)
            it.putInt(AGENT_AGENTID, agentId)
            it.putString(AGENT_USERNAME, userName)
        }.apply()
    }
}