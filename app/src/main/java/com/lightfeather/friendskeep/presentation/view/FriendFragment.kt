package com.lightfeather.friendskeep.presentation.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.databinding.FragmentFriendsBinding
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.application.getCapturedImage
import com.lightfeather.friendskeep.application.toBase64String
import com.lightfeather.friendskeep.application.toBitmap
import com.lightfeather.friendskeep.application.toHexString
import com.lightfeather.friendskeep.presentation.adapter.CardItemRecyclerAdapter
import com.lightfeather.friendskeep.presentation.util.changeLayersColor
import com.lightfeather.friendskeep.presentation.view.FriendFragmentAccessConstants.ADD
import com.lightfeather.friendskeep.presentation.view.FriendFragmentAccessConstants.UPDATE
import com.lightfeather.friendskeep.presentation.viewmodel.FriendViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.IOException
import kotlin.properties.Delegates


class FriendFragment : Fragment() {
    private val TAG = "FriendFragment"
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var args: FriendFragmentArgs
    private val attributesList = mutableListOf<Triple<String, String, Int>>()
    private val friendViewModel: FriendViewModel by sharedViewModel()
    private val accessState: FriendFragmentAccessConstants by lazy { args.accessType }
    private val currFriend: FriendModel? by lazy { args.currFriend }
    private var stringImage: String = ""
    private var chosenYear = 0
    private var chosenMonth = 0
    private var chosenDay = 0
    private var hexFavColor: String? by Delegates.observable(null) { _, _, newValue ->
        newValue?.let {
            binding.animatedBackground1.changeLayersColor(newValue)
            binding.animatedBackground2.changeLayersColor(newValue)
            binding.favColorEt.setHintTextColor(Color.parseColor(newValue))
            binding.addAttrBtn.setBackgroundColor(Color.parseColor(newValue))
        }
    }
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                onImageSelected(result.data)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)

        with(binding) {
            args = FriendFragmentArgs.fromBundle(requireArguments())
            addAttrBtn.setOnClickListener {
                showAddAttrsDialog { attrTitle, attrValue, img ->
                    Log.d(TAG, "onCreateView: $attributesList")
                    attributesList += Triple(attrTitle, attrValue, img)
                    otherAttrs.adapter = CardItemRecyclerAdapter(attributesList)
                }
            }
            addImageView.setOnClickListener { selectImage() }
            actionBtn.setOnClickListener {
                when (accessState) {
                    ADD -> validateThen(::insertNewFriend)
                    UPDATE -> validateThen(::updateFriend)
                }
            }
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        return binding.root
    }

    private fun selectImage() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        resultLauncher.launch(galleryIntent)
    }

    private fun insertNewFriend() {
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                friendViewModel.insertNewFriend(getCurrentFriendData())
            }
            launch {
                friendViewModel.registerFriendBirthdayNotification(getCurrentFriendData())
            }
            withContext(Dispatchers.Main) { findNavController().navigateUp() }
        }
    }

    private fun updateFriend() {
        CoroutineScope(Dispatchers.IO).launch {
            friendViewModel.updateFriend(getCurrentFriendData().copy(id = currFriend?.id ?: 0))
            withContext(Dispatchers.Main) { findNavController().navigateUp() }
        }
    }

    private fun getCurrentFriendData(): FriendModel {
        val friendName = binding.nameEt.text.toString()
        val friendBirthDate = binding.birthDateEt.text.toString()
        return FriendModel(friendName, hexFavColor!!, friendBirthDate, stringImage, attributesList)
    }


    private fun onImageSelected(data: Intent?) {
        binding.friendImage.setImageURI(data?.data)
        try {
            val bitmap: Bitmap = requireContext().getCapturedImage(data?.data ?: return)
            stringImage = bitmap.toBase64String()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun validateThen(onValid: () -> Unit) {
        if (stringImage.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_select_img),
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.nameEt.text.isEmpty()) {
            binding.nameEt.error = getString(R.string.enter_friend_name)
        } else if (binding.birthDateEt.text.isEmpty()) {
            binding.birthDateEt.error = getString(R.string.select_bd)
        } else {
            onValid()
        }
    }


    private fun setUpUIWithMode(
        binding: FragmentFriendsBinding, accessState: FriendFragmentAccessConstants
    ) {
        binding.nameEt.isEnabled = true
        setupFab()
        setupClickListeners()
        setupAttrsList()
        if (accessState == UPDATE) setUpUIForUpdating(binding)
    }

    private fun setupFab() {
        binding.addAttrBtn.visibility = View.VISIBLE
        binding.actionBtn.setImageResource(R.drawable.ic_baseline_save_24)
    }

    private fun setupAttrsList() {
        with(binding) {
            otherAttrs.adapter = CardItemRecyclerAdapter(attributesList)
        }
    }

    private fun setUpUIForUpdating(binding: FragmentFriendsBinding) {
        with(binding) {
            currFriend?.let {
                attributesList += it.otherAttributes
                stringImage = it.friendImg
                nameEt.setText(it.friendName)
                birthDateEt.setText(it.birthDate)
                friendImage.setImageBitmap(it.friendImg.toBitmap())
                hexFavColor = it.favColor
                otherAttrs.adapter = CardItemRecyclerAdapter(it.otherAttributes)
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            birthdateCard.setOnClickListener {
                showDatePicker { year, month, day ->
                    val date = "$day /${month + 1} /$year"
                    binding.birthDateEt.setText(date)
                    chosenYear = year
                    chosenMonth = month
                    chosenDay = day
                }
            }
            favColorCard.setOnClickListener {
                showColorPicker(getString(R.string.choose_favortie_color)) { color: Int ->
                    binding.favColorEt.setTextColor(color)
                    binding.favColorEt.setHintTextColor(color)
                    hexFavColor = color.toHexString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setUpUIWithMode(binding, accessState)
    }
}