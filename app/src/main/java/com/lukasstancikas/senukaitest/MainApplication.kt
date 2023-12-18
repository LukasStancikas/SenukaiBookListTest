package com.lukasstancikas.senukaitest

import android.app.Application
import com.lukasstancikas.booklists.di.ApiModule
import com.lukasstancikas.booklists.di.BookListModule
import com.lukasstancikas.booklists.di.BookDatabaseModule
import com.lukasstancikas.senukaitest.di.NetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(
                BookDatabaseModule.get(),
                NetworkModule.get(),
                ApiModule.get(),
                BookListModule.get()
            ))
        }

    }
}