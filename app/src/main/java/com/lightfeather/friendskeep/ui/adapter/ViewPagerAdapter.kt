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
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.domain.AttributesModel
import com.lightfeather.friendskeep.domain.FriendModel

class ViewPagerAdapter(private val friendList: List<FriendModel>) :
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
        var friendImage: ImageView = itemView.findViewById(R.id.friend_image)
        var friendName: EditText = itemView.findViewById(R.id.nameEt)
        var friendBirthDate: EditText = itemView.findViewById(R.id.birthDateEt)
        var friendFavColor: EditText = itemView.findViewById(R.id.favColorEt)
        var addAttrBtn: Button = itemView.findViewById(R.id.addAttrBtn)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerview_id)

        fun bind(friendModel: FriendModel) {
            friendName.setText(friendModel.friendName)
            friendBirthDate.setText(friendModel.birthDate)
            friendFavColor.setText(friendModel.favColor)
          //  how to set image here
           //TODO:  decode base64 string to image
              val bytes = Base64.decode(friendModel.friendImg, Base64.DEFAULT)
              val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
              // set bitmap on imageView
            friendImage.setImageBitmap(bitmap)

            // send this list to recyclerAdapter
            var attrList = ArrayList<AttributesModel>()

              var attributesHashMap = friendModel.otherAtributes

            for(attributeKey in attributesHashMap.keys ){
                attrList.add(AttributesModel(attributeKey,
                    attributesHashMap[attributeKey].toString()
                ))
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