package com.example.hm5_coroutinesflow.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit


// retrofit
//object RickMortyService {
//
//    private val retrofit by lazy(LazyThreadSafetyMode.NONE) { provideRetrofit() }
//
//    val personApi by lazy(LazyThreadSafetyMode.NONE) {
//        retrofit.create<RickMortyApi>()
//    }
//
//    // https://youtu.be/IDVxFjLeecA?t=11972
//    private fun provideRetrofit(): Retrofit {
//        val client = OkHttpClient.Builder()
//            .readTimeout(20, TimeUnit.SECONDS)
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl("https://rickandmortyapi.com/api/")
//            .addConverterFactory(GsonConverterFactory.create()) // преобразует json обхекты в наши оюъекты
//            .client(client)
//            .build()
//    }
//
//}