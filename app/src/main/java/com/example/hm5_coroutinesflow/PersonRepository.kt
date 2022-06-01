package com.example.hm5_coroutinesflow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PersonRepository {
    suspend fun getUsers(page: Int) = withContext(Dispatchers.IO){
        ServiceLocator.rickMortyApi.getUsers(page)
    }
}