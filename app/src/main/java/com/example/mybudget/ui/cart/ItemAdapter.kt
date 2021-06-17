package com.example.mybudget.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mybudget.R
import com.example.mybudget.data.model.Item
import com.example.mybudget.data.model.Time
import com.example.mybudget.utils.gone
import com.example.mybudget.utils.invisible
import com.example.mybudget.utils.visible
import kotlinx.android.synthetic.main.item_cart.view.*

class ItemAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var allItems: List<Item> = ArrayList()
    private var filteredItems: List<Item> = ArrayList()
    private var timeFilter: Time? = null
    private var lastDate: Int? = 0

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_cart,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.bind(differ.currentList[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitAllList(list: List<Item>) {
        allItems = list
        if (filteredItems.isNotEmpty()) {
            filterItems(timeFilter)
        }
        submitList(list)
    }

    fun submitList(list: List<Item>) {
        lastDate = 0
        differ.submitList(list)
    }

    fun filterItems(time: Time?) {
        timeFilter = time
        val filteredItems = allItems.filter { item ->
            item.time?.year == time?.year && item.time?.month == time?.month
        }
        submitList(filteredItems)
    }

    inner class ItemViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Item, position: Int) = with(itemView) {
            setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            if (position == 0) {
                viewSeparator.gone()
            } else {
                viewSeparator.visible()
            }

            tvName.text = item.name
            tvPrice.text = context.getString(R.string.money_format, item.price)

            val time = item.time!!
            if (time.date!!.toInt() != lastDate) {
                llDate.visible()
                tvDate.text = time.date
                tvDay.text = time.day!!.slice(0..2)
                lastDate = time.date!!.toInt()
            } else {
                llDate.invisible()
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Item)
    }
}