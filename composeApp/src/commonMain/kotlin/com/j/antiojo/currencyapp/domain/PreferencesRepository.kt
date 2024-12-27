package com.j.antiojo.currencyapp.domain

import com.j.antiojo.currencyapp.domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun saveLastUpdated(lastUpdated: String)
    suspend fun isDataFresh(currentTimestamp: Long): Boolean
    suspend fun saveSourceCurrency(code: String)
    suspend fun saveTargetCurrency(code: String)
    suspend fun readSourceCurrency() : Flow<CurrencyCode>
    suspend fun readTargetCurrency() : Flow<CurrencyCode>
}