package com.lightfeather.friendskeep.domain.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lightfeather.friendskeep.domain.FriendModel
import kotlinx.coroutines.flow.Flow


@Dao
interface FriendDao {
    @Insert
    suspend fun insertFriend(friendModel: FriendModel)

    @Query("SELECT * FROM friend_table")
    suspend fun getAllUser():Flow<List<FriendModel>>

    @Delete
    suspend fun deleteFriend(friendModel: FriendModel)

}