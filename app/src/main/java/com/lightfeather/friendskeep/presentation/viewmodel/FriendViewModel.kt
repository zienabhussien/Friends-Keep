package com.lightfeather.friendskeep.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.application.ReminderWorker
import com.lightfeather.friendskeep.application.millisUntilNextBirthDay
import com.lightfeather.friendskeep.application.FriendRepository
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import java.util.concurrent.TimeUnit

class FriendViewModel(
    private val friendsApplication: Application,
    private val friendRepository: FriendRepository
) : AndroidViewModel(friendsApplication) {

    val getFriendsList = friendRepository.getAllFriends().map { it.toList() }

    suspend fun insertNewFriend(friendModel: FriendModel) {
        friendRepository.insertFriend(friendModel)
    }

    suspend fun deleteFriend(friendModel: FriendModel) {
        friendRepository.deleteFriend(friendModel)
    }

    fun registerFriendBirthdayNotification(friendModel: FriendModel) {
        val friendName = friendModel.friendName
        val friendBirthDateSplit = friendModel.birthDate.split("/")
        val day = friendBirthDateSplit[0].trim().toInt()
        val month = friendBirthDateSplit[1].trim().toInt()
        val year = friendBirthDateSplit[2].trim().toInt()

        val timeUntilFriendBirthdayInMillis =
            LocalDate.of(year, month, day).millisUntilNextBirthDay()
        val myWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(365, TimeUnit.DAYS)
            .setInitialDelay(timeUntilFriendBirthdayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf("message" to "It's $friendName birthday !")
            )
            .build()
        WorkManager.getInstance(friendsApplication).enqueue(myWorkRequest)
    }

    fun updateFriend(currentFriendData: FriendModel) {
        friendRepository.updateFriend(currentFriendData)
    }

}