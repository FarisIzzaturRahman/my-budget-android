package com.example.mybudget

import android.app.Application
import com.example.mybudget.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(listOf(appModule, authModule, initModule, menuModule, cartModule))
        }
    }
}