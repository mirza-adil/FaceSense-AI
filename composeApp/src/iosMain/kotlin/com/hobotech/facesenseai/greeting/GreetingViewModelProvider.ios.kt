package com.hobotech.facesenseai.greeting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hobotech.facesenseai.greeting.presentation.GreetingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf

@Composable
actual fun greetingViewModel(): GreetingViewModel = remember {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    GlobalContext.get().get<GreetingViewModel> { parametersOf(scope) }
}
