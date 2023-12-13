package com.lukasstancikas.booklists.di

import com.lukasstancikas.booklists.ui.BookListsViewModel
import com.lukasstancikas.booklists.usecase.PopulatedBookListsUseCase
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

object BookListModule {
    fun get() = module {
        factoryOf(::PopulatedBookListsUseCase)
        viewModelOf(::BookListsViewModel)
    }
}