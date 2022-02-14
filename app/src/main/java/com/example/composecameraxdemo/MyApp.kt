package com.example.composecameraxdemo

import android.app.Application

class MyApp:Application() {
    companion object{
        lateinit var instance: MyApp
        fun getAppContext(): MyApp = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}