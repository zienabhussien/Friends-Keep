package com.lightfeather.friendskeep.presentation.base

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.jakewharton.threetenabp.AndroidThreeTen
import com.lightfeather.friendskeep.application.friendDatabaseModule
import com.lightfeather.friendskeep.application.friendRepositoryModule
import com.lightfeather.friendskeep.application.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AndroidThreeTen.init(this);
        startKoin {
            androidContext(this@BaseApplication)
            modules(
                listOf(
                    friendDatabaseModule,
                    friendRepositoryModule,
                    viewModelModule
                )
            )
        }
    }
}