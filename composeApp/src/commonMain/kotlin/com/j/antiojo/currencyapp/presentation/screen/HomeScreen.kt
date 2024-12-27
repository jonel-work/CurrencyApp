package com.j.antiojo.currencyapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
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

        var amount by remember { mutableDoubleStateOf(0.0) }

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

                }
            )
        }
    }
}
