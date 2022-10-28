package com.lightfeather.friendskeep.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.lightfeather.friendskeep.R

class RecyclerAdapter(private val attributeList: Map<String, String>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var attrHashMap: HashMap<String, String> = HashMap<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_item, parent, false)
        )
    }

    override fun getItemCount(): Int = attributeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(attributeList.entries.elementAt(position))
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: EditText = itemView.findViewById(R.id.attrTitleET)
        var attrVal: EditText = itemView.findViewById(R.id.attrValEt)

        fun bind(attributesModel: Map.Entry<String, String>) {
            title.setText(attributesModel.key)
            attrVal.setText(attributesModel.value)

        }


    }
}