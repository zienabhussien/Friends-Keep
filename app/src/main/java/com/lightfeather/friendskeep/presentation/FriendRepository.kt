package com.lightfeather.friendskeep.presentation

import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.domain.db.FriendDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FriendRepository(private val friendDatabase: FriendDatabase) {

    fun getAllFriends() : Flow<List<FriendModel>> =
        flow{
            friendDatabase.getFriendDao().getAllUser()
        }

    suspend fun insertFriend(friend: FriendModel) = withContext(Dispatchers.IO){
        friendDatabase.getFriendDao().insertFriend(friend)
    }

    suspend fun deleteFriend(friend: FriendModel) = withContext(Dispatchers.IO){
        friendDatabase.getFriendDao().deleteFriend(friend)
     }

//    suspend fun getAllFriends() = withContext(Dispatchers.IO){
//        friendDatabase.getFriendDao().getAllUser()
//    }

    
}