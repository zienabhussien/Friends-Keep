package com.lightfeather.friendskeep.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_table")
data class FriendModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val friendName: String,
    val favColor: String,
    val birthDate: String,
    val friendImg: String,
   val otherAtributes: HashMap<String, String>
)
