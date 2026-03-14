package com.hobotech.facesenseai

import android.app.Application
import com.hobotech.facesenseai.di.appModule
import com.hobotech.facesenseai.di.domainModule
import com.hobotech.facesenseai.di.platformModule
import com.hobotech.facesenseai.facesense.di.faceSenseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FaceSenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FaceSenseApplication)
            modules(
                platformModule(),
                domainModule,
                faceSenseModule,
                appModule
            )
        }
    }
}
