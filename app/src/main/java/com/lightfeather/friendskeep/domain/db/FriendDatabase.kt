package com.lightfeather.friendskeep.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lightfeather.friendskeep.domain.AttributesModel
import com.lightfeather.friendskeep.domain.FriendModel

@Database(entities = [FriendModel::class,AttributesModel::class], version = 1, exportSchema = false)
 abstract class FriendDatabase : RoomDatabase(){

  abstract fun getFriendDao(): FriendDao

}