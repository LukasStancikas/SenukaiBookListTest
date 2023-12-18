package com.lukasstancikas.booklists.di

import com.lukasstancikas.booklists.network.BooksRepository
import com.lukasstancikas.booklists.network.BooksRepositoryImpl
import com.lukasstancikas.booklists.network.NetworkChecker
import com.lukasstancikas.booklists.network.NetworkCheckerImpl
import com.lukasstancikas.booklists.network.retrofit.BooksApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create

object ApiModule {

    fun get() = module {
        single { provideApi(provideRetrofit(get(), get())) }
        single<NetworkChecker> { NetworkCheckerImpl(androidContext()) }

        single<BooksRepository> { BooksRepositoryImpl(get(), get(), get()) }
    }

    private fun provideApi(retrofit: Retrofit): BooksApi = retrofit.create()

    private fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseRetrofitBuilder: Retrofit.Builder
    ): Retrofit = baseRetrofitBuilder.baseUrl(BooksApi.BASE_URL).client(okHttpClient).build()
}