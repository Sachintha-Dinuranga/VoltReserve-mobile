package com.example.voltreserve.client

import android.content.Context
import com.example.voltreserve.helpers.SessionDbHelper
import com.example.voltreserve.services.AuthApiService
import com.example.voltreserve.services.OwnerApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  RetrofitClient {
    private const val BASE_URL = "http://192.168.8.135:5029/"

    // For public endpoints (owner register/login, staff login)
    private val publicRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val ownerPublic: OwnerApiService by lazy {
        publicRetrofit.create(OwnerApiService::class.java)
    }

    val staffPublic: AuthApiService by lazy {
        publicRetrofit.create(AuthApiService::class.java)
    }

    // For authenticated owner endpoints (adds Bearer from SQLite)
    fun ownerAuthed(context: Context): OwnerApiService {
        val db = SessionDbHelper(context)
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                db.getSession()?.let { builder.addHeader("Authorization", "Bearer $it") }
                chain.proceed(builder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OwnerApiService::class.java)
    }

    fun staffAuthed(context: Context): AuthApiService {
        val db = SessionDbHelper(context)
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val b = chain.request().newBuilder()
                db.getSession()?.let { b.addHeader("Authorization", "Bearer $it") }
                chain.proceed(b.build())
            }.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create())
            .build().create(AuthApiService::class.java)
    }
}
