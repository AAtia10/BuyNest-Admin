package com.example.buynest_admin.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ShopifyRetrofitBuilder {
    private const val BASE_URL = "https://mad45-alex-and03.myshopify.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Shopify-Access-Token", "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")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ShopifyService = retrofit.create(ShopifyService::class.java)
}
