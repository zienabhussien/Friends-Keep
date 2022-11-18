package com.lightfeather.friendskeep.ui.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.widget.Toast
import com.lightfeather.friendskeep.databinding.AddAttributeDialogBinding
import com.mrudultora.colorpicker.ColorPickerPopUp
import java.util.*

fun FriendFragment.showAddAttrsDialog(onValidAttempt: (String, String) -> Unit = { _, _ -> }) {
    val attrBinding = AddAttributeDialogBinding.inflate(LayoutInflater.from(requireContext()))
    val builder = AlertDialog.Builder(requireContext()).setView(attrBinding.root)

    val alertDialog = builder.create()
    alertDialog.show()

    attrBinding.backBtn.setOnClickListener {
        alertDialog.dismiss()
    }
    attrBinding.addBtn.setOnClickListener {
        if (attrBinding.attrEt.text!!.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please enter name for the attribute", Toast.LENGTH_SHORT
            ).show()
        } else if (attrBinding.attrValEt.text!!.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please enter the attribute value", Toast.LENGTH_SHORT
            ).show()
        } else {

            onValidAttempt(
                attrBinding.attrEt.text.toString(),
                attrBinding.attrValEt.text.toString()
            )
            alertDialog.dismiss()
        }
    }
}

fun FriendFragment.showDatePicker(onDatePicked: (year: Int, month: Int, day: Int) -> Unit) {
    val calender = Calendar.getInstance()
    val year = calender.get(Calendar.YEAR)
    val month = calender.get(Calendar.MONTH)
    val day = calender.get(Calendar.DAY_OF_MONTH)
    DatePickerDialog(
        requireActivity(),
        { _, newYear, monthOfYear, dayOfMonth -> onDatePicked(newYear, monthOfYear, dayOfMonth) },
        year, month, day
    ).show()
}

fun FriendFragment.showColorPicker(title: String, onColorPicked: (Int) -> Unit) {

    val colorPickerPopUp = ColorPickerPopUp(context)
    colorPickerPopUp
        .setDialogTitle(title)
        .setOnPickColorListener(object : ColorPickerPopUp.OnPickColorListener {
            override fun onColorPicked(color: Int) {
                onColorPicked(color)
            }

            override fun onCancel() {
                colorPickerPopUp.dismissDialog() // Dismiss the dialog.
            }
        })
        .show()
}
