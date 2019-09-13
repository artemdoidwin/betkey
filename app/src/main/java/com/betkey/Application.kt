package com.betkey

import android.app.Application
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.betkey.di.appModules
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : MultiDexApplication() {
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
}