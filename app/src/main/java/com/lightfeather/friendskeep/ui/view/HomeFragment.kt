package com.lightfeather.friendskeep.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.databinding.FragmentHomeBinding
import com.lightfeather.friendskeep.ui.adapter.ViewPagerAdapter
import com.lightfeather.friendskeep.ui.viewmodel.FriendViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var viewPagerAdapter: ViewPagerAdapter
    private val friendViewModel: FriendViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        getFriendsList()

        binding.addFriendBtn.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections
                    .actionHomeFragmentToFriendsFragment(FriendFragmentAccessConstants.ADD)
            )
        }

        return binding.root
    }


    private fun getFriendsList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            friendViewModel.getFriendsList.collect {
                Log.d(TAG, "getFriendsList: inlist")
                binding.tempLayout.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                if (it.isNotEmpty()) {


                    viewPagerAdapter = ViewPagerAdapter(it) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                            .setMessage(getString(R.string.want_to_delete_your_friend))
                            .setTitle(resources.getString(R.string.delete))
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
                        builder.setPositiveButton(resources.getString(R.string.sure)) { _, _ ->
                            lifecycleScope.launchWhenCreated {
                                friendViewModel.deleteFriend(it)
                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.friend_removed),
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAction("Undo") { _ ->
                                        launch { friendViewModel.insertNewFriend(it) }
                                    }
                                    .show()
                            }
                        }

                        val alert: AlertDialog = builder.create()
                        alert.show()
                    }
                    binding.viewPager.adapter = viewPagerAdapter
                } else {
                    binding.tempLayout.visibility = View.VISIBLE
                }
            }
        }
    }


}