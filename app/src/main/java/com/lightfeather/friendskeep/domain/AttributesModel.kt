package com.lightfeather.friendskeep.domain

import androidx.room.Entity

@Entity(tableName = "attributes_table")
data class AttributesModel(
    var title: String,
    var attrVal: String)
