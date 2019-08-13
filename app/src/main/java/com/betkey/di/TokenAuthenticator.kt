package com.betkey.di

import android.content.Context
import com.betkey.utils.*
import okhttp3.*
import org.json.JSONObject


class TokenAuthenticator(
    private val context: Context,
    private val okHttpClientBuilder: OkHttpClient.Builder
) : Authenticator {

    override fun authenticate(route: Route?, response: Response?): Request? {

        if (response?.code() == 401 && response.message() == "Unauthorized") {

            val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            val id = pref.getString(AGENT_ID, "")
            val agentId = pref.getInt(AGENT_AGENTID, 0)
            val userName = pref.getString(AGENT_USERNAME, "")

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("agent[id]", id!!)
                .addFormDataPart("agent[agent_id]", agentId.toString())
                .addFormDataPart("agent[username]", userName!!)
                .build()

            val request = Request.Builder()
                .url(BASE_URSL_BETKEY + "agents/authenticate/token")
                .post(requestBody)
                .build()

            val okHttpClient = okHttpClientBuilder.build()
            val resp = okHttpClient.newCall(request).execute()
            val body = resp.body()?.string()
            val jsonObj = JSONObject(body)
            val token: String = jsonObj.get("token").toString()

            pref.edit().putString(TOKEN, token).apply()

            return response.request().newBuilder()
                .header(TOKEN_NAME, token)
                .build()
        } else {
            return null
        }
    }
}