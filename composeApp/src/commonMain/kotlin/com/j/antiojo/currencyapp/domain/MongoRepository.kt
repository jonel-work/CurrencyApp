package com.j.antiojo.currencyapp.domain

import com.j.antiojo.currencyapp.domain.model.Currency
import com.j.antiojo.currencyapp.domain.model.RequestState
import kotlinx.coroutines.flow.Flow

interface MongoRepository {
    fun configureTheRealm()
    suspend fun insertCurrencyData(currency: Currency)
    fun readCurrencyDataFromRealm(): Flow<RequestState<List<Currency>>>
    suspend fun cleanUp()
}