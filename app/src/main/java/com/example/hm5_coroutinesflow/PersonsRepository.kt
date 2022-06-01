package com.example.hm5_coroutinesflow

import com.example.hm5_coroutinesflow.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PersonsRepository {
    suspend fun getPersons(page: Int) = withContext(Dispatchers.IO) {
       val persons =  ServiceLocator.rickMortyApi.getPersons(page)
    }
}