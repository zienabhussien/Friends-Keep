package com.lightfeather.friendskeep.domain.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.lightfeather.friendskeep.R

class NotificationService( private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(message: String){
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, BIRTHDAY_CHANNEL_ID)
                           .setSmallIcon(R.drawable.gift)
                           .setContentTitle("Friend Birthday")
                           .setContentText(message)
                           .build()
        notificationManager.notify(1,notification)

    }




    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                BIRTHDAY_CHANNEL_ID,
                "BirthdayDate",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for friend birthday date"
            notificationManager.createNotificationChannel(channel)
        }
    }


    companion object {
        const val BIRTHDAY_CHANNEL_ID = "birthday_channel"
    }
}