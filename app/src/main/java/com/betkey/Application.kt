package com.betkey

import android.content.res.Configuration
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.betkey.data.LocaleManager
import com.betkey.di.appModules
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : MultiDexApplication() {

    private val localeManager: LocaleManager by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Android context
            androidContext(this@Application)
            // modules
            modules(appModules)
        }

        RxJavaPlugins.setErrorHandler {
            Log.e("Application","ErrorHandler", it)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
//        localeManager.setLocale(this)
    }
}