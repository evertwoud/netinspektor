package com.evertwoud.netinspektor.example

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform