package com.example.hm5_coroutinesflow.retrofit


import com.example.hm5_coroutinesflow.model.CartoonPerson
//import com.example.hm5_coroutinesflow.model.PersonsListApi
import com.example.hm5_coroutinesflow.model.PersonDetails
import com.example.hm5_coroutinesflow.model.PersonsListApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//https://youtu.be/IDVxFjLeecA?t=10822

interface RickMortyApi {

    @GET("character")
    suspend fun getPersons(
        @Query("page") page: Int,

        ): PersonsListApi

    @GET("character/{id}")
     suspend fun getUserDetails(
        @Path("id") id: Int, // Path -подставление значения в какой-то запрос.
        //
    ): PersonDetails


}