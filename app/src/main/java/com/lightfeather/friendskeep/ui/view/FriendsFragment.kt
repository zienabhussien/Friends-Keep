package com.lightfeather.friendskeep.ui.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.cast.framework.media.ImagePicker
import com.lightfeather.friendskeep.databinding.AddAttributeDialogBinding
import com.lightfeather.friendskeep.databinding.FragmentFriendsBinding
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.ui.viewmodel.FriendViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.IOException

class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
    var attributesHashMap: HashMap<String, String> = HashMap()
    val friendViewModel: FriendViewModel by sharedViewModel()
    lateinit var stringImage: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        makeViewsEditable()

        binding.addAttrBtn.setOnClickListener {
            initDialog()
        }
        binding.addImageView.setOnClickListener {
            // select image from gallery
            selectImage()
        }
        binding.addFriendBtn.setOnClickListener {
            // save data in room database
            saveData()
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

        val newFriend = FriendModel(0, friendName,
                             friendFavColor, friendBirthDate,
                             stringImage,attributesHashMap   )

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
             friendViewModel.insertNewFriend(newFriend)
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
                attributesHashMap[attributeTitle] = attrValue
                alertDialog.dismiss()
            }
        }

    }


    private fun makeViewsEditable() {
        binding.nameEt.isEnabled = true
        binding.birthDateEt.isEnabled = true
        binding.favColorEt.isEnabled = true

    }


    companion object {
        const val REQUEST_PICK_PHOTO = 1001

    }
}