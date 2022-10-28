package com.lightfeather.friendskeep.ui.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.databinding.AddAttributeDialogBinding
import com.lightfeather.friendskeep.databinding.FragmentFriendsBinding
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.ui.view.FriendFragmentAccessConstants.*
import com.lightfeather.friendskeep.ui.viewmodel.FriendViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.ByteArrayOutputStream
import java.io.IOException

class FriendFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var args: FriendFragmentArgs
    private val attributesMap = mutableMapOf<String, String>()
    private val friendViewModel: FriendViewModel by sharedViewModel()
    private val accessState: FriendFragmentAccessConstants by lazy { args.accessType ?: DISPLAY }
    lateinit var stringImage: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        args = FriendFragmentArgs.fromBundle(requireArguments())
        enableEditTexts()

        binding.addAttrBtn.setOnClickListener {
            initDialog()
        }
        binding.addImageView.setOnClickListener {
            // select image from gallery
            selectImage()
        }
        binding.actionBtn.setOnClickListener {
            when (accessState) {
                ADD -> saveData()
                UPDATE -> {}
                DISPLAY -> {}
            }

        }

        return binding.root
    }

    private fun selectImage() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO)
    }

    private fun saveData() {
        val friendName = binding.nameEt.text.toString()
        val friendBirthDate = binding.birthDateEt.text.toString()
        val friendFavColor = binding.favColorEt.text.toString()

        val newFriend = FriendModel(
            0, friendName,
            friendFavColor, friendBirthDate,
            stringImage, attributesMap
        )

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            friendViewModel.insertNewFriend(newFriend)
            withContext(Dispatchers.Main) { findNavController().navigateUp() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
            requestCode == REQUEST_PICK_PHOTO
        ) {
            binding.friendImage.setImageURI(data?.data)
            val uri = data?.data
            // TODO: decoded image to string using base64
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
            birthDateEt.isEnabled = true
            favColorEt.isEnabled = true
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
        }
    }

    private fun setUpUIForAdding(binding: FragmentFriendsBinding) {
        enableEditTexts()
        with(binding) {
            addAttrBtn.visibility = View.VISIBLE
            actionBtn.setImageResource(R.drawable.ic_baseline_save_24)
        }
    }
}