package com.betkey.data

import amlib.ccid.c
import android.content.Context
import android.content.res.Configuration
import java.util.*


class LocaleManager(private val prefManager: PreferencesManager) {

    fun setLocale(context: Context) {
        update(context, prefManager.getLanguage())
    }

    fun setNewLocale(context: Context, language: String) {
        prefManager.persistLanguage(language)
        update(context, language)
    }

    private fun update(context: Context, language: String?) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        res.updateConfiguration(config, res.displayMetrics)
    }

    fun getLocale(): String? {
        return prefManager.getLanguage()
    }
}