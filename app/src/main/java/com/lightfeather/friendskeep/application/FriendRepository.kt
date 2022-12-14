package com.lightfeather.friendskeep.application

import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.domain.db.FriendDatabase
import kotlinx.coroutines.flow.Flow

class FriendRepository(private val friendDatabase: FriendDatabase) {

    fun getAllFriends(): Flow<List<FriendModel>> = friendDatabase.getFriendDao().getAllUser()


    suspend fun insertFriend(friend: FriendModel) =
        friendDatabase.getFriendDao().insertFriend(friend)


    suspend fun deleteFriend(friend: FriendModel) =
        friendDatabase.getFriendDao().deleteFriend(friend)

    fun updateFriend(currentFriendData: FriendModel) =
        friendDatabase.getFriendDao().updateFriend(currentFriendData)
}