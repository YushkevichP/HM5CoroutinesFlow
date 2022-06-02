package com.example.hm5_coroutinesflow

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hm5_coroutinesflow.adapter.LoadingViewHolder
import com.example.hm5_coroutinesflow.adapter.PersonViewHolder
import com.example.hm5_coroutinesflow.databinding.ItemLoadingBinding
import com.example.hm5_coroutinesflow.databinding.ItemPersonBinding
import com.example.hm5_coroutinesflow.model.CartoonPerson
import com.example.hm5_coroutinesflow.model.ItemType


class ItemAdapter(
    context: Context,
    private val onUserClicked: (ItemType<CartoonPerson>) -> Unit,
) : ListAdapter<ItemType<CartoonPerson>, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ItemType.Content -> TYPE_CONTENT
            ItemType.Loading -> TYPE_LOADING
        }
    }

    val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CONTENT -> {
                PersonViewHolder(
                    binding = ItemPersonBinding.inflate(layoutInflater, parent, false),
                    onUserClicked = onUserClicked
                )
            }
            TYPE_LOADING -> {
                LoadingViewHolder(
                    binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                )
            }
            else -> error("Incorrect ViewType = $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_CONTENT -> {
                val personVH = holder as? PersonViewHolder ?: return
                val item = getItem(position) as? ItemType.Content ?: return
                personVH.bind(item)
            }
        }
    }

    companion object {

        private const val TYPE_CONTENT = 0
        private const val TYPE_LOADING = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemType<CartoonPerson>>() {
            override fun areItemsTheSame(
                oldItem: ItemType<CartoonPerson>,
                newItem: ItemType<CartoonPerson>,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ItemType<CartoonPerson>,
                newItem: ItemType<CartoonPerson>,
            ): Boolean {

                val oldPersonItem = oldItem as? ItemType.Content ?: return false
                val newPersonItem = newItem as? ItemType.Content ?: return false

                return (oldPersonItem.data.idApi == newPersonItem.data.idApi
                        && oldPersonItem.data.imageApi == newPersonItem.data.imageApi
                        && oldPersonItem.data.nameApi == newPersonItem.data.nameApi)
            }
        }
    }
}