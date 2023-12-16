package com.lukasstancikas.booklists.ui.booklists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.databinding.ItemBookListBinding

class BookListsAdapter(
    private val onAllClick: (BookList) -> Unit, private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookListsAdapter.MyViewHolder>() {
    private val items = mutableListOf<BookList>()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val binding =
            ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onBookClick)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = with(holder.binding) {
        itemBookListTitle.text = items[position].title
        itemBookListProgress.isVisible = items[position].isLoading
        itemBookListAll.setOnClickListener { onAllClick(items[position]) }
        holder.bookAdapter.setItems(items[position].books)
    }

    fun setItems(newItems: List<BookList>) {
        val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    private class ItemDiffCallback(val oldItems: List<BookList>, val newItems: List<BookList>) :
        DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].id == newItems[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].title == newItems[newItemPosition].title
                    && oldItems[oldItemPosition].books == newItems[newItemPosition].books
                    && oldItems[oldItemPosition].isLoading == newItems[newItemPosition].isLoading
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }
    }

    class MyViewHolder(val binding: ItemBookListBinding, onBookClick: (Book) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        val bookAdapter = BookSmallAdapter(onBookClick)
        init {
            binding.itemBookListRecycler.adapter = bookAdapter
        }
    }
}