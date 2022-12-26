package com.lightfeather.friendskeep.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_table")
data class FriendModel(
    val friendName: String,
    val favColor: String,
    val birthDate: String,
    val friendImg: String,
    val otherAttributes: List<Triple<String, String, Int>>,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    ) : java.io.Serializable
