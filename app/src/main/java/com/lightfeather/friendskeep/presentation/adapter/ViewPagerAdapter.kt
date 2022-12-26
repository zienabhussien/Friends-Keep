package com.lightfeather.friendskeep.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.lightfeather.friendskeep.databinding.FragmentFriendsBinding
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.application.toBitmap
import com.lightfeather.friendskeep.presentation.util.changeLayersColor

class ViewPagerAdapter(
    private val friendList: List<FriendModel>,
    private val onDeleteClick: (FriendModel) -> Unit = {},
    private val onUpdateClicked: (FriendModel) -> Unit = {}
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentFriendsBinding.inflate(LayoutInflater.from(parent.context)).apply {
                root.layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            })
    }

    override fun getItemCount(): Int = friendList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(friendList[position])


    inner class ViewHolder(private val binding: FragmentFriendsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friendModel: FriendModel) {
            with(binding) {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                nameEt.setText(friendModel.friendName)
                birthDateEt.setText(friendModel.birthDate)
                if (friendModel.friendImg.isNotEmpty()) {
                    friendImage.setImageBitmap(friendModel.friendImg.toBitmap())
                }

                animatedBackground1.changeLayersColor(friendModel.favColor)
                animatedBackground2.changeLayersColor(friendModel.favColor)
                favColorCard.visibility = View.GONE
                addAttrBtn.visibility = View.GONE
                actionBtn.visibility = View.GONE
                addImageView.visibility = View.GONE
                friendManagementContainer.visibility = View.VISIBLE
                deleteFriend.setOnClickListener { onDeleteClick(friendModel) }
                editFriend.setOnClickListener { onUpdateClicked(friendModel) }
                otherAttrs.adapter = CardItemRecyclerAdapter(friendModel.otherAttributes)
            }
        }
    }
}