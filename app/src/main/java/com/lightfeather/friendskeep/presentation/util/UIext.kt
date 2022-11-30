package com.lightfeather.friendskeep.presentation.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.google.android.material.snackbar.Snackbar
import com.lightfeather.friendskeep.R


private const val TAG = "ext"

fun LottieAnimationView.changeLayersColor(color: Int) {
    try {
        val filter = SimpleColorFilter(color)
        val keyPath = KeyPath("**")
        val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
        addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
    } catch (iae: Exception) {
        Log.e(TAG, "changeLayersColor: ${iae.localizedMessage}")
    }
}

fun LottieAnimationView.changeLayersColor(colorHex: String) {
    try {
        changeLayersColor(Color.parseColor(colorHex))
    } catch (iae: Exception) {
        Log.e(TAG, "changeLayersColor: ${iae.localizedMessage}")
    }
}

fun Context.showAlertDialog(
    title: String,
    message: String,
    posButtonText: String = getString(R.string.sure),
    negButtonText: String = getString(R.string.cancel),
    posAction: () -> Unit,
    negAction: () -> Unit = {}
) {

    val dialog = AlertDialog.Builder(this)
        .setMessage(message)
        .setTitle(title)
        .setNegativeButton(negButtonText) { dialog, _ ->
            negAction()
            dialog.cancel()
        }
        .setPositiveButton(posButtonText) { _, _ -> posAction() }
        .create()
    dialog.show()
}

fun View.showLongSnackbar(message: String, actionMessage: String, action: (View) -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAction(actionMessage, action)
        .show()
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