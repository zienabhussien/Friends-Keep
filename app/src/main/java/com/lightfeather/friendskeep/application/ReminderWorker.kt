package com.lightfeather.friendskeep.application

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(val context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    override fun doWork(): Result {
        NotificationService(context).showNotification(inputData.getString("message") ?: "")
        return Result.success()
    }
}