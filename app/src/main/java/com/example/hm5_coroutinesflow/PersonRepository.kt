package com.example.hm5_coroutinesflow

import com.example.hm5_coroutinesflow.model.PersonDetails
import com.example.hm5_coroutinesflow.model.wrapperForListFromApi
import com.example.hm5_coroutinesflow.retrofit.RickMortyApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PersonRepository(private val rickMortyApi:RickMortyApi) {

    suspend fun getUser(page: Int) = withContext(Dispatchers.IO){
       // delay(4000)
       rickMortyApi.getUsers(page)
    }

    suspend fun getPersonDetails(id: Int) = withContext(Dispatchers.IO){
        rickMortyApi.getUserDetails(id)
    }


}