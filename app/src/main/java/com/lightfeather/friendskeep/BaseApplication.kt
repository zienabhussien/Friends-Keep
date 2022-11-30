package com.lightfeather.friendskeep

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.lightfeather.friendskeep.presentation.friendDatabaseModule
import com.lightfeather.friendskeep.presentation.friendRepositoryModule
import com.lightfeather.friendskeep.presentation.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
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