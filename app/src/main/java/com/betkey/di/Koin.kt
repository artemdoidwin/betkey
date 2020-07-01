package com.betkey.di

import android.content.Context
import android.util.Log
import com.betkey.BuildConfig
import com.betkey.data.*
import com.betkey.network.ApiInterfaceBetkey
import com.betkey.network.ApiInterfaceMarginfox
import com.betkey.network.ApiInterfacePSP
import com.betkey.repository.ModelRepository
import com.betkey.ui.MainViewModel
import com.betkey.utils.*
import com.google.gson.Gson
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

private val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get(), get()) }
}

private val networkModule = module {
    single(named(BETKEY)) {
        Retrofit.Builder()
            .baseUrl(BASE_URSL_BETKEY)
            .client(get<OkHttpClient.Builder>()
                .authenticator(get<TokenAuthenticator>())
                .addInterceptor {
                    it.proceed(
                        it.request().newBuilder().url(
                            it.request().url().newBuilder().addQueryParameter("apikey", API_KEY_BETKEY).build()
                        ).build()
                    )
                }
                .addInterceptor(get())
                .build())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }
    single(named(MARGINFOX)) {
        Retrofit.Builder()
            .baseUrl(BASE_URSL_MARGINFOX)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(OkHttpClient.Builder()
                .addInterceptor(get())
                .addInterceptor{
                    it.proceed(
                        it.request().newBuilder().url(
                            it.request().url().newBuilder().addQueryParameter("instance", INSTANCE_MARGINFOX).build()
                        ).build()
                    )
                }
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build())
            .build()
    }
    single(named(PSP)) {
        Retrofit.Builder()
            .baseUrl(BASE_URSL_PSP)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(get())
            .build()
    }
    factory<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get())
            .build()
    }
    factory<Interceptor> {
        LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .setLevel(Level.BASIC)
            .log(Platform.INFO)
            .tag("RETROFIT")
            .build()
    }
}

private val dataModule = module {
    single { get<Context>().applicationContext.getSharedPreferences(PREF, Context.MODE_PRIVATE) }
    single { PreferencesManager(get()) }
    single { BetKeyDataManager(get(), get(), get()) }
    single { MarginfoxDataManager(get(), get(), get()) }
    single { PSPDataManager(get(), get(), get()) }
    single { ModelRepository() }
    factory { TokenAuthenticator(get(), get()) }
    single { OkHttpClient.Builder() }
    single { LocaleManager(get()) }
}

private val apiModule = module {
    single { get<Retrofit>(named(BETKEY)).create(ApiInterfaceBetkey::class.java) }
    single { get<Retrofit>(named(MARGINFOX)).create(ApiInterfaceMarginfox::class.java) }
    single { get<Retrofit>(named(PSP)).create(ApiInterfacePSP::class.java) }
}


val appModules = mutableListOf(viewModelModule, networkModule, dataModule, apiModule)