package com.lightfeather.friendskeep.domain.application

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun String.toBitmap(): Bitmap? {
    val bytes = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}