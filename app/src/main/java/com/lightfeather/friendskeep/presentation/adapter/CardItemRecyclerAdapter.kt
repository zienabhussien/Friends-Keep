package com.lightfeather.friendskeep.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lightfeather.friendskeep.R
import com.lightfeather.friendskeep.databinding.CardItemBinding

class CardItemRecyclerAdapter(
    private val attributeList: List<Triple<String, String, Int>>,
    private val selectingEnabled: Boolean = false,

    ) :
    RecyclerView.Adapter<CardItemRecyclerAdapter.ViewHolder>() {
    var selectedIdx: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(CardItemBinding.inflate(LayoutInflater.from(parent.context)))


    override fun getItemCount(): Int = attributeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(attributeList[position], position)
    }

    inner class ViewHolder(private val binding: CardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(attributesModel: Triple<String, String, Int>, position: Int) {
            with(binding) {
                if (selectingEnabled) {
                    if (position == selectedIdx) {
                        containerCard.setCardBackgroundColor(containerCard.context.getColor(R.color.selected_item_color))
                    } else {
                        containerCard.setCardBackgroundColor(containerCard.context.getColor(R.color.white))
                    }
                    contentContainer.setOnClickListener {
                        selectedIdx = position
                        notifyDataSetChanged()
                    }
                }
                attrTitleET.text = attributesModel.first
                attrValEt.text = attributesModel.second
                if (attributesModel.third != -1)
                    category.setImageResource(attributesModel.third)
                else{
                    category.setImageDrawable(null)
                }
            }
        }
    }
}