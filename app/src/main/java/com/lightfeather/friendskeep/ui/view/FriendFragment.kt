package com.lightfeather.friendskeep.ui.view

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.databinding.AddAttributeDialogBinding
import com.lightfeather.friendskeep.databinding.FragmentFriendsBinding
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.domain.application.toBitmap
import com.lightfeather.friendskeep.ui.changeLayersColor
import com.lightfeather.friendskeep.ui.view.FriendFragmentAccessConstants.*
import com.lightfeather.friendskeep.ui.viewmodel.FriendViewModel
import com.mrudultora.colorpicker.ColorPickerPopUp
import com.mrudultora.colorpicker.ColorPickerPopUp.OnPickColorListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates


class FriendFragment : Fragment() {
    private val TAG = "FriendFragment"
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var args: FriendFragmentArgs
    private val attributesMap = mutableMapOf<String, String>()
    private val friendViewModel: FriendViewModel by sharedViewModel()
    private val accessState: FriendFragmentAccessConstants by lazy { args.accessType }
    private val currFriend: FriendModel? by lazy { args.currFriend }
    private var stringImage: String = ""
    var hexFavColor: String? by Delegates.observable(null) { property, oldValue, newValue ->
        newValue?.let {
            binding.animatedBackground1.changeLayersColor(newValue)
            binding.animatedBackground2.changeLayersColor(newValue)
            binding.favColorEt.setHintTextColor(Color.parseColor(newValue))
            binding.addAttrBtn.setBackgroundColor(Color.parseColor(newValue))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        with(binding) {
            args = FriendFragmentArgs.fromBundle(requireArguments())

            enableEditTexts()

            addAttrBtn.setOnClickListener { initDialog() }
            addImageView.setOnClickListener { selectImage() }
            actionBtn.setOnClickListener {
                when (accessState) {
                    ADD -> validateThen(::insertNewFriend)
                    UPDATE -> validateThen(::updateFriend)
                    DISPLAY -> {}
                }

            }
        }

        return binding.root
    }

    private fun updateFriend() {
        CoroutineScope(Dispatchers.IO).launch {
            friendViewModel.updateFriend(getCurrentFriendData().copy(id = currFriend?.id ?: 0))
            withContext(Dispatchers.Main) { findNavController().navigateUp() }
        }
    }

    private fun selectImage() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO)
    }

    private fun getCurrentFriendData(): FriendModel {
        val friendName = binding.nameEt.text.toString()
        val friendBirthDate = binding.birthDateEt.text.toString()
        return FriendModel(
            friendName,
            hexFavColor ?: "",
            friendBirthDate,
            stringImage,
            attributesMap
        )
    }

    private fun insertNewFriend() {
        CoroutineScope(Dispatchers.IO).launch {
            friendViewModel.insertNewFriend(getCurrentFriendData())
            withContext(Dispatchers.Main) { findNavController().navigateUp() }
        }
    }

    private fun validateThen(onValid: () -> Unit) {
        if (stringImage.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please select an image!", Toast.LENGTH_SHORT
            ).show()
        } else if (binding.nameEt.text.isEmpty()) {
            binding.nameEt.error = "Please enter friend name"
        } else if (binding.birthDateEt.text.isEmpty()) {
            binding.birthDateEt.error = "Please select the friend birth date"
        } else {
            onValid()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
            requestCode == REQUEST_PICK_PHOTO
        ) {
            binding.friendImage.setImageURI(data?.data)


            // TODO: decoded image to string using base64
            val uri = data?.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver, uri
                )
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                // byte array
                val bytes = stream.toByteArray()
                // get base64 encoded string
                stringImage = Base64.encodeToString(bytes, Base64.DEFAULT)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    // init alertdialog to insert another attributes
    private fun initDialog() {
        val attrBinding = AddAttributeDialogBinding
            .inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(attrBinding.root)

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
                // take the values to store and display, then dismiss dialog
                val attributeTitle = attrBinding.attrEt.text.toString()
                val attrValue = attrBinding.attrValEt.text.toString()
                attributesMap[attributeTitle] = attrValue
                alertDialog.dismiss()
            }
        }

    }

    private fun enableEditTexts() {
        with(binding) {
            nameEt.isEnabled = true
            birthdateCard.setOnClickListener { selectDate() }
            favColorCard.setOnClickListener { selectFavColor() }

        }
    }

    private fun disableEditTexts() {
        with(binding) {
            nameEt.isEnabled = false
            birthDateEt.isEnabled = false
            favColorEt.isEnabled = false
        }
    }


    companion object {
        const val REQUEST_PICK_PHOTO = 1001

    }

    override fun onResume() {
        super.onResume()
        setUpUIWithMode(binding, accessState)
    }

    private fun setUpUIWithMode(
        binding: FragmentFriendsBinding,
        accessState: FriendFragmentAccessConstants
    ) {
        when (accessState) {
            ADD -> setUpUIForAdding(binding)
            UPDATE -> setUpUIForUpdating(binding)
            DISPLAY -> setUpUIForDisplaying(binding)
        }
    }

    private fun setUpUIForDisplaying(binding: FragmentFriendsBinding) {
        disableEditTexts()
        with(binding) {
            addAttrBtn.visibility = View.GONE
            actionBtn.visibility = View.GONE
        }
    }

    private fun setUpUIForUpdating(binding: FragmentFriendsBinding) {
        enableEditTexts()
        with(binding) {
            addAttrBtn.visibility = View.VISIBLE
            actionBtn.setImageResource(R.drawable.ic_baseline_save_24)
            currFriend?.let {
                stringImage = it.friendImg
                nameEt.setText(it.friendName)
                birthDateEt.setText(it.birthDate)
                animatedBackground1.changeLayersColor(it.favColor)
                animatedBackground2.changeLayersColor(it.favColor)
                binding.favColorEt.setHintTextColor(Color.parseColor(it.favColor))
                binding.addAttrBtn.setBackgroundColor(Color.parseColor(it.favColor))
                friendImage.setImageBitmap(it.friendImg.toBitmap())

            }
        }
    }

    private fun setUpUIForAdding(binding: FragmentFriendsBinding) {
        enableEditTexts()
        with(binding) {
            addAttrBtn.visibility = View.VISIBLE
            actionBtn.setImageResource(R.drawable.ic_baseline_save_24)
        }
    }

    private fun selectDate() {
        //  val
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            requireActivity(),
            { view, year, monthOfYear, dayOfMonth -> binding.birthDateEt.setText("$dayOfMonth /${monthOfYear + 1} /$year") },
            year,
            month,
            day
        )

        dpd.show()
    }

    private fun selectFavColor() {

        val colorPickerPopUp = ColorPickerPopUp(context) // Pass the context.

        colorPickerPopUp.setShowAlpha(true) // By default show alpha is true.
            .setDefaultColor(Color.RED) // By default red color is set.
            .setDialogTitle(getString(R.string.choose_favortie_color))
            .setOnPickColorListener(object : OnPickColorListener {
                override fun onColorPicked(color: Int) {
                    binding.favColorEt.setTextColor(color)
                    hexFavColor = java.lang.String.format("#%06X", 0xFFFFFF and color)
                    binding.favColorEt.setHintTextColor(Color.parseColor(hexFavColor))

                }

                override fun onCancel() {
                    colorPickerPopUp.dismissDialog() // Dismiss the dialog.
                }
            })
            .show()

    }
}
