package com.lightfeather.friendskeep.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lightfeather.friendskeep.databinding.FragmentHomeBinding
import com.lightfeather.friendskeep.presentation.viewModelModule
import com.lightfeather.friendskeep.ui.adapter.ViewPagerAdapter
import com.lightfeather.friendskeep.ui.viewmodel.FriendViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var viewPagerAdapter: ViewPagerAdapter
    private val friendViewModel: FriendViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        getFriendsList()

        binding.addFriendBtn.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections
                .actionHomeFragmentToFriendsFragment2())
        }

        return binding.root
    }



    private fun getFriendsList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            friendViewModel.getFriendsList.collect{
                viewPagerAdapter = ViewPagerAdapter(it)
                binding.viewPager.adapter = viewPagerAdapter
            }
        }
    }
    /**
 *     fun loadDummyData(): ArrayList<FriendModel> {
var getFriendsList = ArrayList<FriendModel>()
var firstFriendAttributes = HashMap<String, String>()
firstFriendAttributes.put("Sort", "Man")
firstFriendAttributes.put("FavAnimal", "Cat")
firstFriendAttributes.put("Favfilm", "Toy Story")
firstFriendAttributes.put("Wish", "To be a doctor")

val firstFriend = FriendModel(0,
"Mohamed",
"Blue",
"1/9/1998",
R.drawable.cute_imge,
firstFriendAttributes
)
var secondFriendAttributes = HashMap<String, String>()
secondFriendAttributes.put("Sort", "Girl")
secondFriendAttributes.put("FavAnimal", "Dog")
secondFriendAttributes.put("Favfilm", "Frozen")
secondFriendAttributes.put("Wish", "To be a designer")
val secondFriend = FriendModel(1,
"Fatima",
"Bink",
"20/9/2004",
R.drawable.girl_photo,
secondFriendAttributes
)
var thirdFriendAttributes = HashMap<String, String>()
thirdFriendAttributes.put("Sort", "Girl")
thirdFriendAttributes.put("FavAnimal", "Rabitt")
thirdFriendAttributes.put("Favfilm", "Moana")
thirdFriendAttributes.put("Wish", "To be a  Writer")
val thirdFriend = FriendModel(2,
"Amira",
"Baby blue",
"12/5/2000",
R.drawable.arnoob,
thirdFriendAttributes
)
getFriendsList.add(firstFriend)
getFriendsList.add(secondFriend)
getFriendsList.add(thirdFriend)
return getFriendsList
}
 */


}