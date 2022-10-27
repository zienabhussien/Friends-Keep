package com.lightfeather.friendskeep.domain.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun mapToString(input: Map<String, String>): String = Gson().toJson(input)

    @TypeConverter
    fun stringToMap(input: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(input, mapType)
    }
}