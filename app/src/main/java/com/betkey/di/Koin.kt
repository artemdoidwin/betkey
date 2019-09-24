package com.betkey.di

import android.content.Context
import com.betkey.data.BetKeyDataManager
import com.betkey.data.MarginfoxDataManager
import com.betkey.data.PSPDataManager
import com.betkey.data.PreferencesManager
import com.betkey.network.ApiInterfaceBetkey
import com.betkey.network.ApiInterfaceMarginfox
import com.betkey.network.ApiInterfacePSP
import com.betkey.repository.ModelRepository
import com.betkey.ui.MainViewModel
import com.betkey.utils.*
import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


private val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
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
//                        it.request().newBuilder().header(TOKEN_NAME, "").url(
                            it.request().url().newBuilder().addQueryParameter("apikey", API_KEY_BETKEY).build()
                        ).build()
                    )
                }
                .build())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }
    single(named(MARGINFOX)) {
        Retrofit.Builder()
            .baseUrl(BASE_URSL_MARGINFOX)
            .client(get<OkHttpClient.Builder>()
                .addInterceptor {
                    it.proceed(
                        it.request().newBuilder().url(
                            it.request().url().newBuilder().addQueryParameter("instance", INSTANCE_MARGINFOX).build()
                        ).build()
                    )
                }
                .build())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }
    single(named(PSP)) {
        Retrofit.Builder()
            .baseUrl(BASE_URSL_PSP)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
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
}

private val apiModule = module {
    single { get<Retrofit>(named(BETKEY)).create(ApiInterfaceBetkey::class.java) }
    single { get<Retrofit>(named(MARGINFOX)).create(ApiInterfaceMarginfox::class.java) }
    single { get<Retrofit>(named(PSP)).create(ApiInterfacePSP::class.java) }
}


val appModules = mutableListOf(viewModelModule, networkModule, dataModule, apiModule)