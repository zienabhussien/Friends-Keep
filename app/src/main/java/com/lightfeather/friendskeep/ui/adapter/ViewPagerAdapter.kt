package com.lightfeather.friendskeep.ui.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.domain.FriendModel

class ViewPagerAdapter(
    private val friendList: List<FriendModel>,
    private val onDeleteClick: (FriendModel) -> Unit = {}
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_friends, parent, false)
        )
    }

    override fun getItemCount(): Int = friendList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friendList[position])
    }


    inner class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendImage: ImageView = itemView.findViewById(R.id.friend_image)
        val friendName: EditText = itemView.findViewById(R.id.nameEt)
        val friendBirthDate: EditText = itemView.findViewById(R.id.birthDateEt)
        val friendFavColor: EditText = itemView.findViewById(R.id.favColorEt)
        val addAttrBtn: Button = itemView.findViewById(R.id.addAttrBtn)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerview_id)
        val actionBtn: FloatingActionButton = itemView.findViewById(R.id.actionBtn)
        val addImg: ImageView = itemView.findViewById(R.id.add_imageView)
        val deleteImg: ImageView = itemView.findViewById(R.id.delete_friend)
        val editImg: ImageView = itemView.findViewById(R.id.edit_friend)
        val favImg: ImageView = itemView.findViewById(R.id.fav_friend)

        fun bind(friendModel: FriendModel) {
            friendName.setText(friendModel.friendName)
            friendBirthDate.setText(friendModel.birthDate)
            friendFavColor.setText(friendModel.favColor)
            addAttrBtn.visibility = View.GONE
            actionBtn.visibility = View.GONE
            addImg.visibility = View.GONE
            deleteImg.visibility = View.VISIBLE
            editImg.visibility = View.VISIBLE
            favImg.visibility = View.VISIBLE

            deleteImg.setOnClickListener {
                onDeleteClick(friendModel)
            }

            //  how to set image here
            //TODO:  decode base64 string to image
            val bytes = Base64.decode(friendModel.friendImg, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            // set bitmap on imageView
            friendImage.setImageBitmap(bitmap)

            // send this list to recyclerAdapter
            val attrList = mutableMapOf<String, String>()

            val attributesHashMap = friendModel.otherAttributes

            for (attributeKey in attributesHashMap.keys) {
                attrList += attributeKey to attributesHashMap[attributeKey].toString()
                val adapter = RecyclerAdapter(attrList)
                recyclerView.adapter = adapter
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