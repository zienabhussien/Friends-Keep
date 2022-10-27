package com.lightfeather.friendskeep.presentation

import android.app.Application
import androidx.room.Room
import com.lightfeather.friendskeep.domain.db.FriendDao
import com.lightfeather.friendskeep.domain.db.FriendDatabase
import com.lightfeather.friendskeep.ui.viewmodel.FriendViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DATABASE_NAME = "friendDatabase"

fun providesDatabase(application: Application) : FriendDatabase =
       Room.databaseBuilder(application,FriendDatabase::class.java, DATABASE_NAME)
           .fallbackToDestructiveMigration()
           .build()

fun provideDao(friendDatabase: FriendDatabase): FriendDao =
    friendDatabase.getFriendDao()

val friendDatabaseModule = module {
    single { providesDatabase(application = androidApplication()) }
    single { provideDao(get()) }
}


val viewModelModule = module {
  viewModel { FriendViewModel(friendRepository = get()) }
}

val friendRepositoryModule = module{
    single { FriendRepository(friendDatabase = get()) }
}