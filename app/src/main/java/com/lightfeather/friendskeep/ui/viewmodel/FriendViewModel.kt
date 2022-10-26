package com.lightfeather.friendskeep.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.presentation.FriendRepository
import kotlinx.coroutines.flow.map

class FriendViewModel(private val friendRepository: FriendRepository): ViewModel() {

    val getFriendsList = friendRepository.getAllFriends().map { it.toList() }

   suspend fun insertNewFriend(friendModel: FriendModel){
       friendRepository.insertFriend(friendModel)
   }

    suspend fun deleteFriend(friendModel: FriendModel){
        friendRepository.deleteFriend(friendModel)
    }

}