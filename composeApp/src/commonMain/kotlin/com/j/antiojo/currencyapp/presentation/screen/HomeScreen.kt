package com.j.antiojo.currencyapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.j.antiojo.currencyapp.domain.model.CurrencyType
import com.j.antiojo.currencyapp.presentation.component.CurrencyPickerDialog
import com.j.antiojo.currencyapp.presentation.component.HomeHeader
import com.j.antiojo.currencyapp.ui.theme.surfaceColor

class HomeScreen : Screen {

    @Composable
    override fun Content() {
//        LaunchedEffect(Unit) {
//            CurrencyApiServiceImpl().getLatestExchangeRates()
//        }

        val viewModel = getScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateStatus
        val sourceCurrency by viewModel.sourceCurrency
        val targetCurrency by viewModel.targetCurrency
        val allCurrency = viewModel.allCurrency

        var amount by remember { mutableDoubleStateOf(0.0) }

        var selectedCurrencyType by remember {
            mutableStateOf<CurrencyType>(CurrencyType.None)
        }

        var dialogOpened by remember { mutableStateOf(false) }

        if (dialogOpened && selectedCurrencyType != CurrencyType.None) {
            CurrencyPickerDialog(
                currencies = allCurrency,
                currencyType = selectedCurrencyType,
                onConfirmClick = { currencyCode ->
                    if (selectedCurrencyType is CurrencyType.Source) {
                        viewModel.sendEvent(
                            HomeUiEvent.SaveSourceCurrencyCode(currencyCode.name)
                        )
                    } else if (selectedCurrencyType is CurrencyType.Target) {
                        viewModel.sendEvent(
                            HomeUiEvent.SaveTargetCurrencyCode(currencyCode.name)
                        )
                    }
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false

                },
                onDismiss = {
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceColor)
        ) {
            HomeHeader(
                rateStatus = rateStatus,
                source = sourceCurrency,
                target = targetCurrency,
                amount = amount,
                onAmountChange = { amount = it },
                onRatesRefresh = {
                    viewModel.sendEvent(HomeUiEvent.RefreshRates)
                },
                onSwitchClick = {
                    viewModel.sendEvent(HomeUiEvent.SwitchCurrencies)
                },
                onCurrencyTypeSelected = { currencyType ->
                    selectedCurrencyType = currencyType
                    dialogOpened = true


                }
            )
        }
    }
}
