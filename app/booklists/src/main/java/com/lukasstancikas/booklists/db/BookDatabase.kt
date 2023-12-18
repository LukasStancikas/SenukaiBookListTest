package com.lukasstancikas.booklists.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lukasstancikas.booklists.db.dao.BookDao
import com.lukasstancikas.booklists.db.dao.BookListDao
import com.lukasstancikas.booklists.db.tables.BookEntity
import com.lukasstancikas.booklists.db.tables.BookListEntity
import com.lukasstancikas.booklists.util.DateTypeConverter

@Database(entities = [BookEntity::class, BookListEntity::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class BookDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun bookListDao(): BookListDao
}