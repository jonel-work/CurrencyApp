package com.j.antiojo.currencyapp.di

import com.j.antiojo.currencyapp.data.local.MongoImpl
import com.j.antiojo.currencyapp.data.local.PreferencesImpl
import com.j.antiojo.currencyapp.data.remote.api.CurrencyApiServiceImpl
import com.j.antiojo.currencyapp.domain.CurrencyApiService
import com.j.antiojo.currencyapp.domain.MongoRepository
import com.j.antiojo.currencyapp.domain.PreferencesRepository
import com.j.antiojo.currencyapp.presentation.screen.HomeViewModel
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    single<MongoRepository> { MongoImpl() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferencesRepository = get()) }
    factory {
        HomeViewModel(
            preferencesRepository = get(),
            mongoRepository = get(),
            apiService = get()
        )
    }
}


fun appModule() = listOf(appModule)

fun initializeKoin() {
    startKoin {
        modules(appModule())
    }
}