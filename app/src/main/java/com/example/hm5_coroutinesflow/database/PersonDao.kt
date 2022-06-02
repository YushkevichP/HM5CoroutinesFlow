package com.example.hm5_coroutinesflow.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hm5_coroutinesflow.model.CartoonPerson
import com.example.hm5_coroutinesflow.model.ItemType

@Dao
interface PersonDao {
    @Query("SELECT * FROM cartoonperson")
    suspend fun getAllPersons(): List<CartoonPerson>

    @Query("SELECT * FROM cartoonperson WHERE idApi BETWEEN 1 AND 20")
    suspend fun getFirstTwenty(): List<CartoonPerson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersons(list: List<CartoonPerson>)
}