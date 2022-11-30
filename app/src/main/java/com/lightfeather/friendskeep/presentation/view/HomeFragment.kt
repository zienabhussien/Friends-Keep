package com.lightfeather.friendskeep.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.databinding.FragmentHomeBinding
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.presentation.adapter.ViewPagerAdapter
import com.lightfeather.friendskeep.presentation.util.showAlertDialog
import com.lightfeather.friendskeep.presentation.util.showLongSnackbar
import com.lightfeather.friendskeep.presentation.viewmodel.FriendViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val friendViewModel: FriendViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        with(binding) {
            addFriendBtn.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections
                        .actionHomeFragmentToFriendsFragment(
                            FriendFragmentAccessConstants.ADD,
                            null
                        )
                )
            }
        }
        observeFriendsList()
        return binding.root
    }


    private fun observeFriendsList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            friendViewModel.getFriendsList.collect {
                Log.d(TAG, "getFriendsList: inlist $it")
                binding.tempLayout.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                viewPagerAdapter = ViewPagerAdapter(
                    it,
                    onDeleteClick = ::showDeleteDialog, onUpdateClicked = ::navigateUpdateFriend
                )
                binding.viewPager.adapter = viewPagerAdapter

            }
        }
    }


    private fun showDeleteDialog(friendModel: FriendModel) {
        with(binding) {
            context?.showAlertDialog(
                title = getString(R.string.delete),
                message = getString(R.string.want_to_delete_your_friend),
                posAction = {
                    lifecycleScope.launchWhenCreated {
                        friendViewModel.deleteFriend(friendModel)
                        viewPagerAdapter.notifyDataSetChanged()
                        root.showLongSnackbar(
                            getString(R.string.friend_removed),
                            getString(R.string.undo)
                        ) {
                            Log.d(TAG, "getFriendsList: undo")
                            CoroutineScope(Dispatchers.IO).launch {
                                friendViewModel.insertNewFriend(
                                    friendModel
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    private fun navigateUpdateFriend(friend: FriendModel) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToFriendsFragment(
                FriendFragmentAccessConstants.UPDATE, friend
            )
        )
    }
}