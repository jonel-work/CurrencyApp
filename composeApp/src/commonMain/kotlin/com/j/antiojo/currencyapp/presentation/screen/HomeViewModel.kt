package com.j.antiojo.currencyapp.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.j.antiojo.currencyapp.domain.CurrencyApiService
import com.j.antiojo.currencyapp.domain.MongoRepository
import com.j.antiojo.currencyapp.domain.PreferencesRepository
import com.j.antiojo.currencyapp.domain.model.Currency
import com.j.antiojo.currencyapp.domain.model.RateStatus
import com.j.antiojo.currencyapp.domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
}

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val mongoRepository: MongoRepository,
    private val apiService: CurrencyApiService
) : ScreenModel {

    private var _rateStatus: MutableState<RateStatus> =
        mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrency: List<Currency> = _allCurrencies

    private var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }
        }
    }

    private fun readSourceCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferencesRepository.readSourceCurrency().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }

                if (selectedCurrency != null) {
                    _sourceCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _sourceCurrency.value =
                        RequestState.Error(message = "Couldn't find the selected country.")

                }

            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferencesRepository.readTargetCurrency().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }

                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _targetCurrency.value =
                        RequestState.Error(message = "Couldn't find the selected country.")

                }

            }
        }
    }

    private suspend fun fetchNewRates() {
        try {
            val localCache = mongoRepository.readCurrencyDataFromRealm().first()

            if (localCache.isSuccessful()) {

                if (localCache.getSuccessData()?.isNotEmpty() == true) {

                    _allCurrencies.addAll(localCache.getSuccessData()!!)

                    if (!preferencesRepository.isDataFresh(
                            Clock.System.now().toEpochMilliseconds()
                        )
                    ) {
                        cacheTheData()
                    } else {
                        println("DATA is Fresh")
                    }
                } else {
                    println("DATABASE needs Data")
                    cacheTheData()
                }
            } else if (localCache.isError()) {
                println("ERROR reading local database = ${localCache.getErrorMessage()}")
            }

            apiService.getLatestExchangeRates()
            getRateStatus()
        } catch (e: Exception) {
            println("fetchNewRates ${e.message}")
        }
    }

    private suspend fun cacheTheData() {
        val fetchRemoteData = apiService.getLatestExchangeRates()
        if (fetchRemoteData.isSuccessful()) {
            mongoRepository.cleanUp()
            fetchRemoteData.getSuccessData()?.forEach {
                println("Adding Currency in DB:  ${it.code}")
                mongoRepository.insertCurrencyData(it)
            }
            fetchRemoteData.getSuccessData()?.let { _allCurrencies.addAll(it) }
        } else if (fetchRemoteData.isError()) {
            println("Fetching Remote Data Failed ${fetchRemoteData.getErrorMessage()}")
        }
    }

    private suspend fun getRateStatus() {
        _rateStatus.value = if (preferencesRepository.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        ) {
            RateStatus.Fresh
        } else {
            RateStatus.Stale
        }
    }
}