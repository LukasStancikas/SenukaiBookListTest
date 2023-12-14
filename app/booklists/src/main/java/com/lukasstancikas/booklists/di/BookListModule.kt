package com.lukasstancikas.booklists.di

import com.lukasstancikas.booklists.ui.booklist.MyListViewModel
import com.lukasstancikas.booklists.ui.booklists.BookListsViewModel
import com.lukasstancikas.booklists.usecase.PopulatedBookListsUseCase
import com.lukasstancikas.booklists.usecase.PopulatedMyListUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

object BookListModule {
    fun get() = module {
        factoryOf(::PopulatedMyListUseCase)
        factoryOf(::PopulatedBookListsUseCase)
        viewModelOf(::BookListsViewModel)
        viewModel { parameters -> MyListViewModel(bookList = parameters.get(), get(), get()) }
    }
}