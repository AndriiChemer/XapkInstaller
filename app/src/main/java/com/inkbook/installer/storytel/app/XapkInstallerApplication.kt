package com.inkbook.installer.storytel.app

import android.app.Application
import com.inkbook.installer.storytel.app.di.appModule
import com.inkbook.installer.storytel.app.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class XapkInstallerApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@XapkInstallerApplication)
            modules(
                listOf(
                    appModule,
                    mainModule
                )
            )
        }
    }
}