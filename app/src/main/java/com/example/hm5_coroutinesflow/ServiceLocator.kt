package com.example.hm5_coroutinesflow

import com.example.hm5_coroutinesflow.retrofit.RickMortyApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object ServiceLocator {
   private val rickMortyApi by lazy {
            provideRetrofit().create<RickMortyApi>()
    }

    fun provideRepository():PersonRepository{
        return PersonRepository(rickMortyApi)
    }

    private fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create()) // преобразует json обхекты в наши оюъекты
            .client(client)
            .build()
    }
}








//
//object ServiceLocator {
//    val rickMortyApi by lazy {
//        provideRetrofit().create<RickMortyApi>()
//    }
//
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
//}