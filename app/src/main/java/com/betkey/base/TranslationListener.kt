package com.betkey.base

interface TranslationListener {
    fun onTranslationReceived(dictionary: Map<String?, String?>)
}