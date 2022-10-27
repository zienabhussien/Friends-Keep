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
    fun insertFriend(friendModel: FriendModel)

    @Query("SELECT * FROM friend_table")
    fun getAllUser(): Flow<List<FriendModel>>

    @Delete
    fun deleteFriend(friendModel: FriendModel)

}