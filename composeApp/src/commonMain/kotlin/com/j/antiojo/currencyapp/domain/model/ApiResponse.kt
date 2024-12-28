package com.j.antiojo.currencyapp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ApiResponse(
    val meta: MetaData,
    val data: Map<String, Currency>
)

@Serializable
data class MetaData(
    @SerialName("last_updated_at")
    val lastUpdateAt: String
)

@Serializable
data class Currency(
    @Transient
    val id: String = "",
    val code: String,
    val value: Double
)