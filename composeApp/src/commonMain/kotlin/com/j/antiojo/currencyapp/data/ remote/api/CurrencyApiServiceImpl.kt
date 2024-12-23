package com.j.antiojo.currencyapp.data.remote.api

import com.j.antiojo.currencyapp.domain.CurrencyApiService
import com.j.antiojo.currencyapp.domain.PreferencesRepository
import com.j.antiojo.currencyapp.domain.model.ApiResponse
import com.j.antiojo.currencyapp.domain.model.Currency
import com.j.antiojo.currencyapp.domain.model.CurrencyCode
import com.j.antiojo.currencyapp.domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl(private val preferencesRepository: PreferencesRepository) :
    CurrencyApiService {

    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "cur_live_uAVhgOdDx0aJmI5KQK68iklYYfUHyiYYuZA8YXdb"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15000L
            connectTimeoutMillis = 15000L
            socketTimeoutMillis = 15000L
        }

        install(DefaultRequest) {
            headers {
                append("apikey", API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<Currency>> {
        return try {
            val response = httpClient.get(ENDPOINT)
            if (response.status.value in 200..299) {
                val apiResponse = Json.decodeFromString<ApiResponse>(response.body())

                val availableCurrencyCodes = apiResponse.data.keys
                    .filter { currencyCode ->
                        CurrencyCode.entries
                            .map { code -> code.name }
                            .toSet()
                            .contains(currencyCode)
                    }

                val availableCurrencies = apiResponse.data.values
                    .filter { currency ->
                        availableCurrencyCodes.contains(currency.code)
                    }


                //Persist Timestamp
                val lastUpdated = apiResponse.meta.lastUpdateAt
                preferencesRepository.saveLastUpdated(lastUpdated)
                RequestState.Success(availableCurrencies)
            } else {
                RequestState.Error(message = "HTTP Error Code: ${response.status.value}")
            }
        } catch (e: Exception) {
            RequestState.Error(message = e.message ?: "Unknown Error")
        }
    }
}