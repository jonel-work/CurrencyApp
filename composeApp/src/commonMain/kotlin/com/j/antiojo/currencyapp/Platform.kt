package com.j.antiojo.currencyapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform