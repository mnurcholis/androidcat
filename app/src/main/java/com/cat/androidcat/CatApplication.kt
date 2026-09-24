package com.cat.androidcat

import android.app.Application
import com.cat.androidcat.data.repository.SessionManager

class CatApplication : Application() {
    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        sessionManager = SessionManager(this)
    }

    companion object {
        lateinit var instance: CatApplication
            private set
    }
}
