package com.lukasstancikas.booklists.di

import com.lukasstancikas.booklists.ui.bookdetails.BookViewModel
import com.lukasstancikas.booklists.ui.booklist.MyListViewModel
import com.lukasstancikas.booklists.ui.booklists.BookListsViewModel
import com.lukasstancikas.booklists.usecase.PopulatedBookListsUseCase
import com.lukasstancikas.booklists.usecase.PopulatedMyListUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import java.time.format.DateTimeFormatter

object BookListModule {
    fun get() = module {
        single<DateTimeFormatter> { DateTimeFormatter.ISO_OFFSET_DATE_TIME }
        factoryOf(::PopulatedMyListUseCase)
        factoryOf(::PopulatedBookListsUseCase)
        viewModelOf(::BookListsViewModel)
        viewModel { parameters -> BookViewModel(book = parameters.get(), get(), get()) }
        viewModel { parameters -> MyListViewModel(bookList = parameters.get(), get(), get()) }
    }
}