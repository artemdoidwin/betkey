package com.betkey.data

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class LocaleManager(
    private val prefManager: PreferencesManager,
    private val betKeyDataManager: BetKeyDataManager
) {

    val dictionary = MutableLiveData<Map<String?, String?>>()
    val languages =  MutableLiveData<Set<String>>()
    val compositeDisposable = CompositeDisposable()

    private var isFirstEvent = true

    companion object {
        const val DEFAULT_LANGUAGE = "en"
    }

    fun setLocale(context: Context) {
        update(context, prefManager.getLanguage())
    }

    fun setNewLocale(/*context: Context,*/ language: String) {
        prefManager.persistLanguage(language)
        loadTranslation(language)
//        update(context, language)
    }

    private fun update(context: Context, language: String?) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        res.updateConfiguration(config, res.displayMetrics)
    }

    fun loadTranslation(language: String? = null) {
        compositeDisposable.clear()
        val langs = mutableSetOf<String>()
        compositeDisposable.add(
            betKeyDataManager.loadTranslation(language)
                .map {
                    val prefLanguage = prefManager.getLanguage()
                    var key = prefLanguage
                    it.translations?.keys?.forEach { l ->
                        if(l?.contains(prefLanguage) == true) {
                            key = l
                        }
                        l?.also { langs.add(l) }
                    }
                    it.translations?.get(key)
                }
                .observeOn(Schedulers.io())
                .subscribe { d ->
                    //crunch
                    //lazy to implement SingleLiveData
                    if(isFirstEvent) {
                        languages.postValue(langs)
                        isFirstEvent = false
                    }
                    dictionary.postValue(d)
                }
        )
    }
}

