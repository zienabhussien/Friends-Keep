package com.lightfeather.friendskeep.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.ColorFilter
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback

fun LottieAnimationView.changeLayersColor(
    @ColorRes colorRes: Int
) {
    val color = ContextCompat.getColor(context, colorRes)
    val filter = SimpleColorFilter(color)
    val keyPath = KeyPath("**")
    val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
    addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}

fun LottieAnimationView.changeLayersColor(colorHex: String) {
    val filter = SimpleColorFilter(Color.parseColor(colorHex))
    val keyPath = KeyPath("**")
    val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
    addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}


fun Activity.hideSoftKeyboard() {
    val inputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
    if (inputMethodManager.isAcceptingText) {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            0
        );
    }
}