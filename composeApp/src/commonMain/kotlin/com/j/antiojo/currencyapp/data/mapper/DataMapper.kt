package com.j.antiojo.currencyapp.data.mapper

import com.j.antiojo.currencyapp.domain.model.Currency
import com.j.antiojo.currencyapp.domain.model.CurrencyRealm

fun CurrencyRealm.toCurrencyDTO() : Currency {
    return Currency(code, value)
}

fun Currency.toRealm() : CurrencyRealm {
    val currency = this
    return CurrencyRealm().apply {
        code = currency.code
        value = currency.value
    }
}