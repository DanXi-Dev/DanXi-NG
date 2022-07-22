package com.fduhole.danxinative

import android.app.Application
import com.fduhole.danxinative.state.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DanXiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DanXiApplication)
            modules(appModule)
        }
    }
}