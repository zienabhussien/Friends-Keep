package com.lightfeather.friendskeep.domain.application

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(val context: Context, private val parameters: WorkerParameters)
                                                                   : Worker(context,parameters) {
    override fun doWork(): Result {
        // call notification here
        NotificationService(context).showNotification("message")
        return  Result.success()
    }
}