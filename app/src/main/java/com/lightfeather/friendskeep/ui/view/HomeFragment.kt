package com.lightfeather.friendskeep.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lightfeather.friendskeep.databinding.FragmentHomeBinding
import com.lightfeather.friendskeep.ui.adapter.ViewPagerAdapter
import com.lightfeather.friendskeep.ui.viewmodel.FriendViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

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
                if(it.isNotEmpty()) {
                    binding.tempLayout.visibility = View.GONE

                    viewPagerAdapter = ViewPagerAdapter(it)
                    binding.viewPager.adapter = viewPagerAdapter
                }else{
                    binding.tempLayout.visibility = View.VISIBLE
                }
            }
        }
    }


}