package com.mydoctor.pressure

import android.app.Application
import com.mydoctor.pressure.data.AppContainer
import com.mydoctor.pressure.data.AppDataContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PressureApplication : Application() {
/*
    *//**
     * AppContainer instance used by the rest of classes to obtain dependencies
     *//*
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }*/
}
