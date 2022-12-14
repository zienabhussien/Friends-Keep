package com.lightfeather.friendskeep.application

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import java.io.ByteArrayOutputStream

fun String.toBitmap(): Bitmap? {
    val bytes = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Context.getCapturedImage(selectedPhotoUri: Uri): Bitmap {
    val bitmap = when {
        Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
            this.contentResolver,
            selectedPhotoUri
        )
        else -> {
            val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri)
            ImageDecoder.decodeBitmap(source)
        }
    }
    return bitmap
}

fun Bitmap.toBase64String(): String {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 50, stream)
    val bytes = stream.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

fun LocalDate.millisUntilNextBirthDay(): Long {
    val today = LocalDate.now()
    var nextBDay = withYear(today.year)
    if (nextBDay.isBefore(today) || nextBDay.isEqual(today)) {
        nextBDay = nextBDay.plusYears(1)
    }
    return Duration.between(today.atStartOfDay(), nextBDay.atStartOfDay()).toMillis()
}

fun Int.toHexString() = String.format("#%06X", 0xFFFFFF and this)