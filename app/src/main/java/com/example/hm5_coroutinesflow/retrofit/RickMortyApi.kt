package com.example.hm5_coroutinesflow.retrofit


import com.example.hm5_coroutinesflow.model.PersonDetails
import com.example.hm5_coroutinesflow.model.wrapperForListFromApi
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//https://youtu.be/IDVxFjLeecA?t=10822

interface RickMortyApi {

    @GET("character")
    suspend fun getUsers(
        @Query("page") page: Int,

        ): wrapperForListFromApi

    @GET("character/{id}")
    suspend fun getUserDetails(
        @Path("id") id: Int, // Path -подставление значения в какой-то запрос.
        //
    ): PersonDetails

}