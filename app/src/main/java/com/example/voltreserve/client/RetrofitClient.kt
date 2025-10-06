package com.example.voltreserve.client

import android.content.Context
import com.example.voltreserve.helpers.SessionDbHelper
import com.example.voltreserve.services.OwnerApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  RetrofitClient {
    private const val BASE_URL = "http://192.168.8.135:5029/"

    // Public instance (for login/register, no token needed)
    val instance: OwnerApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(OwnerApiService::class.java)
    }

    // Authenticated instance (automatically adds token to requests)
    fun getOwnerService(context: Context): OwnerApiService {
        val dbHelper = SessionDbHelper(context)

        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder()

                // Fetch token dynamically from SQLite
                val token = dbHelper.getSession()
                token?.let {
                    builder.addHeader("Authorization", "Bearer $it")
                }

                chain.proceed(builder.build())
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(OwnerApiService::class.java)
    }
}
