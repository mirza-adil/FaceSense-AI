package com.hobotech.facesenseai

import android.app.Application
import com.hobotech.facesenseai.di.appModule
import com.hobotech.facesenseai.di.domainModule
import com.hobotech.facesenseai.di.initKoin
import com.hobotech.facesenseai.di.platformModule
import com.hobotech.facesenseai.greeting.di.greetingModule

class FaceSenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            listOf(
                platformModule(),
                domainModule,
                greetingModule,
                appModule
            )
        )
    }
}
