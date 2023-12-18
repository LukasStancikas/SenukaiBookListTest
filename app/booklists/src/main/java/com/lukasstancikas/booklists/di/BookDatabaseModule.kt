package com.lukasstancikas.booklists.di

import androidx.room.Room
import com.lukasstancikas.booklists.db.BookDatabase
import com.lukasstancikas.booklists.util.DateTypeConverter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object BookDatabaseModule {
    fun get() = module {
        single<BookDatabase> {
            Room
                .databaseBuilder(
                    androidContext(),
                    BookDatabase::class.java,
                    "book_database"
                )
                .addTypeConverter(DateTypeConverter(get()))
                .build()
        }
    }
}