package com.j.antiojo.currencyapp.di

import com.j.antiojo.currencyapp.data.local.PreferencesImpl
import com.j.antiojo.currencyapp.data.remote.api.CurrencyApiServiceImpl
import com.j.antiojo.currencyapp.domain.CurrencyApiService
import com.j.antiojo.currencyapp.domain.PreferencesRepository
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferencesRepository = get()) }
}


fun appModule() = listOf(appModule)

fun initializeKoin() {
    startKoin {
        modules(appModule())
    }
}