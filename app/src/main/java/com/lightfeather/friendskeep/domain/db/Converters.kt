package com.lightfeather.friendskeep.domain.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun mapToString(input: List<Triple<String, String, Int>>): String = Gson().toJson(input)

    @TypeConverter
    fun stringToMap(input: String): List<Triple<String, String, Int>> {
        if (input.isEmpty()) return listOf()
        val listType = object : TypeToken<List<Triple<String, String, Int>>>(){}.type
        return Gson().fromJson(input, listType)
    }
}