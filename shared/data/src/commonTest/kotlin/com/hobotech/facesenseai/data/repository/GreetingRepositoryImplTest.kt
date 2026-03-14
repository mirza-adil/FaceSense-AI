package com.hobotech.facesenseai.data.repository

import com.hobotech.facesenseai.data.source.PlatformDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GreetingRepositoryImplTest {

    @Test
    fun getGreeting_returnsFormattedMessageWithPlatformName() = runTest {
        val platformDataSource = object : PlatformDataSource {
            override fun getPlatformName(): String = "Android 34"
        }
        val repository = GreetingRepositoryImpl(platformDataSource)

        val result = repository.getGreeting()

        assertEquals("Hello, Android 34!", result.message)
    }
}
