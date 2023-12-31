package com.lukasstancikas.booklists.ui.booklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.databinding.ItemBookBigBinding

class MyListAdapter(
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<MyListAdapter.MyViewHolder>() {
    private val items = mutableListOf<Book>()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val binding =
            ItemBookBigBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder.binding) {
        Glide
            .with(itemBookImage.context)
            .load(items[position].img)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(android.R.drawable.progress_indeterminate_horizontal)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemBookImage)
        itemBookAuthor.text = items[position].author
        itemBookProgress.isVisible = items[position].isLoading
        itemBookTitle.text = items[position].title
        root.setOnClickListener { onBookClick(items[position]) }
    }

    fun setItems(newItems: List<Book>) {
        val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    private class ItemDiffCallback(val oldItems: List<Book>, val newItems: List<Book>) :
        DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].id == newItems[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].title == newItems[newItemPosition].title
                    && oldItems[oldItemPosition].author == newItems[newItemPosition].author
                    && oldItems[oldItemPosition].img == newItems[newItemPosition].img
                    && oldItems[oldItemPosition].isLoading == newItems[newItemPosition].isLoading
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }
    }

    class MyViewHolder(val binding: ItemBookBigBinding) : RecyclerView.ViewHolder(binding.root)
}