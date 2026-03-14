package com.hobotech.facesenseai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform