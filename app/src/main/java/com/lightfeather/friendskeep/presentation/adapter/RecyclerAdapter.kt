package com.lightfeather.friendskeep.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lightfeather.friendskeep.databinding.CardItemBinding

class RecyclerAdapter(private val attributeList: Map<String, String>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(CardItemBinding.inflate(LayoutInflater.from(parent.context)))


    override fun getItemCount(): Int = attributeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(attributeList.entries.elementAt(position))
    }

    inner class ViewHolder(private val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(attributesModel: Map.Entry<String, String>) {
            with(binding) {
                attrTitleET.text = attributesModel.key
                attrValEt.text = (attributesModel.value)
            }
        }
    }
}