package com.lightfeather.friendskeep.ui.view

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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.databinding.FragmentFriendsBinding
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.domain.application.ReminderWorker
import com.lightfeather.friendskeep.domain.application.getCapturedImage
import com.lightfeather.friendskeep.domain.application.toBase64String
import com.lightfeather.friendskeep.domain.application.toBitmap
import com.lightfeather.friendskeep.ui.adapter.RecyclerAdapter
import com.lightfeather.friendskeep.ui.changeLayersColor
import com.lightfeather.friendskeep.ui.view.FriendFragmentAccessConstants.ADD
import com.lightfeather.friendskeep.ui.view.FriendFragmentAccessConstants.UPDATE
import com.lightfeather.friendskeep.ui.viewmodel.FriendViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import java.io.IOException
import org.threeten.bp.Period
import org.threeten.bp.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
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
    private var chosenYear = 0
    private var chosenMonth = 0
    private var chosenDay = 0
    private var hexFavColor: String? by Delegates.observable(null) { property, oldValue, newValue ->
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
                showAddAttrsDialog { attrTitle, attrValue ->
                    attributesMap[attrTitle] = attrValue
                    otherAttrs.adapter?.notifyDataSetChanged()
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
            friendViewModel.insertNewFriend(getCurrentFriendData())
            withContext(Dispatchers.Main) { findNavController().navigateUp() }
        }
        val friendName = binding.nameEt.text.toString()
        val friendBirthDate = binding.birthDateEt.text.toString()
        val datesplit = friendBirthDate.split("/")
        val day = datesplit[0].trim().toInt()
        val month = datesplit[1].trim().toInt()
        val year = datesplit[2].trim().toInt()
        //val formatter = DateTimeFormatter.ofPattern("dd /mm /yyyy")
        val initialDelay = LocalDate.of(year, month, day).millisUntilNextBirthDay()
        createWorkRequest(friendName, initialDelay)
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
        return FriendModel(friendName, hexFavColor!!, friendBirthDate, stringImage, attributesMap)
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
            Toast.makeText(requireContext(), "Please select an image!", Toast.LENGTH_SHORT).show()
        } else if (binding.nameEt.text.isEmpty()) {
            binding.nameEt.error = "Please enter friend name"
        } else if (binding.birthDateEt.text.isEmpty()) {
            binding.birthDateEt.error = "Please select the friend birth date"
        } else {
            onValid()
        }
    }


    private fun setUpUIWithMode(
        binding: FragmentFriendsBinding, accessState: FriendFragmentAccessConstants
    ) {
        enableEditTexts()
        setupAttrsList()
        when (accessState) {
            ADD -> setUpUIForAdding(binding)
            UPDATE -> setUpUIForUpdating(binding)
        }
    }

    private fun setupAttrsList() {
        with(binding) {
            otherAttrs.adapter = RecyclerAdapter(attributesMap)
        }
    }

    private fun setUpUIForUpdating(binding: FragmentFriendsBinding) {
        with(binding) {
            addAttrBtn.visibility = View.VISIBLE
            actionBtn.setImageResource(R.drawable.ic_baseline_save_24)
            currFriend?.let {
                stringImage = it.friendImg
                nameEt.setText(it.friendName)
                birthDateEt.setText(it.birthDate)
                friendImage.setImageBitmap(it.friendImg.toBitmap())
                hexFavColor = it.favColor
            }
        }
    }

    private fun setUpUIForAdding(binding: FragmentFriendsBinding) {
        with(binding) {
            addAttrBtn.visibility = View.VISIBLE
            actionBtn.setImageResource(R.drawable.ic_baseline_save_24)
        }
    }

    private fun enableEditTexts() {
        with(binding) {
            nameEt.isEnabled = true
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
                    hexFavColor = String.format("#%06X", 0xFFFFFF and color)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setUpUIWithMode(binding, accessState)
    }

    fun LocalDate.periodUntilNextBirthDay(): Period? {
        val today = LocalDate.now()
        var nextBDay = withYear(today.year)
        if (nextBDay.isBefore(today) || nextBDay.isEqual(today)) {
            nextBDay = nextBDay.plusYears(1)
        }
        return Period.between(today, nextBDay)
    }

    fun LocalDate.millisUntilNextBirthDay(): Long {
        val today = LocalDate.now()
        var nextBDay = withYear(today.year)
        if (nextBDay.isBefore(today) || nextBDay.isEqual(today)) {
            nextBDay = nextBDay.plusYears(1)
        }
        return Duration.between(today.atStartOfDay(), nextBDay.atStartOfDay()).toMillis()
    }

    private fun createWorkRequest(friendName: String, timeDelayInMilliSeconds: Long) {
        val myWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(365, TimeUnit.DAYS)
            .setInitialDelay(timeDelayInMilliSeconds, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf("message" to "Its $friendName birthday !")
            )
            .build()
        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
    }
}