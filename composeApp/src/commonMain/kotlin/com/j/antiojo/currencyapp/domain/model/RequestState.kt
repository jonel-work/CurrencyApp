package com.j.antiojo.currencyapp.domain.model

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    fun isSuccessful() = this is Success
    fun isError() = this is Error
    fun isLoading() = this is Loading

    fun getSuccessData(): T? = (this as? Success)?.data
    fun getErrorMessage(): String? = (this as? Error)?.message
}