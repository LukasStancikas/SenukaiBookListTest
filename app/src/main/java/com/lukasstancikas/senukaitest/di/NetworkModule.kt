package com.lukasstancikas.senukaitest.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lukasstancikas.booklists.di.ApiModule
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkModule {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    fun get() = module {
        single { provideOkHttpClient(provideHttpLoggingInterceptor(), provideHeaderInterceptor()) }
        factoryOf(::provideRetrofitBuilder)
    }

    private fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private fun provideHeaderInterceptor(): HeaderInterceptor = HeaderInterceptor()

    private fun provideRetrofitBuilder() = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(HeaderInterceptor.CONTENT_TYPE_HEADER_VALUE.toMediaType()))


    private const val TIMEOUT = 8L
}