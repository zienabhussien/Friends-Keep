package com.lightfeather.friendskeep.ui.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lightfeather.friendskeep.databinding.FragmentFriendsBinding
import com.lightfeather.friendskeep.domain.FriendModel
import com.lightfeather.friendskeep.ui.changeLayersColor

class ViewPagerAdapter(
    private val friendList: List<FriendModel>,
    private val onDeleteClick: (FriendModel) -> Unit = {}
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
                nameEt.setText(friendModel.friendName)
                birthDateEt.setText(friendModel.birthDate)

                favColorCard.visibility = View.GONE
                animatedBackground1.changeLayersColor(friendModel.favColor)
                animatedBackground2.changeLayersColor(friendModel.favColor)
                addAttrBtn.visibility = View.GONE
                actionBtn.visibility = View.GONE
                addImageView.visibility = View.GONE
                friendManagementContainer.visibility = View.VISIBLE
                deleteFriend.setOnClickListener {
                    onDeleteClick(friendModel)
                }

                if (friendModel.friendImg != "") {
                    val bytes = Base64.decode(friendModel.friendImg, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    friendImage.setImageBitmap(bitmap)
                }

                // send this list to recyclerAdapter
                val attrList = mutableMapOf<String, String>()

                val attributesHashMap = friendModel.otherAttributes

                for (attributeKey in attributesHashMap.keys) {
                    attrList += attributeKey to attributesHashMap[attributeKey].toString()
                    val adapter = RecyclerAdapter(attrList)
                    this.otherAttrs.adapter = adapter
                }
            }

        }


    }
}
/**
 *  // decode base64 string to image
fun getImageFromString() {
val bytes = Base64.decode(stringImage, Base64.DEFAULT)
val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
// set bitmap on imageView
//  binding.friendImage.setImageBitmap(bitmap)
}
 */