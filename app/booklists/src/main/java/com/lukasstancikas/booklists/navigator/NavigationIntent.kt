package com.lukasstancikas.booklists.navigator

import androidx.navigation.NavController
import com.lukasstancikas.booklists.data.Book
import com.lukasstancikas.booklists.data.BookList
import com.lukasstancikas.booklists.ui.booklist.MyListFragmentDirections
import com.lukasstancikas.booklists.ui.booklists.BookListsFragmentDirections

sealed class NavigationIntent {
    data class BookListSelected(val bookList: BookList) : NavigationIntent()
    data class BookSelectedFromLists(val book: Book) : NavigationIntent()
    data class BookSelectedFromMyList(val book: Book) : NavigationIntent()

    fun navigate(navController: NavController) {
        when (this) {
            is BookListSelected ->
                BookListsFragmentDirections.actionBookListsToMyList(bookList)

            is BookSelectedFromLists ->
                BookListsFragmentDirections.actionBookListsToBookDetailsFragment(book)

            is BookSelectedFromMyList ->
                MyListFragmentDirections.actionMyListToBookDetailsFragment(book)
        }.also {
            navController.navigate(it)
        }
    }
}
