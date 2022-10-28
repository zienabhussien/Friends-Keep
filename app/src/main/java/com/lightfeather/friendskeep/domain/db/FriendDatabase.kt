package com.lightfeather.friendskeep.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lightfeather.friendskeep.domain.FriendModel

@Database(
    entities = [FriendModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FriendDatabase : RoomDatabase() {

    abstract fun getFriendDao(): FriendDao

}