package com.example.hm5_coroutinesflow.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hm5_coroutinesflow.model.CartoonPerson


@Database(entities = [CartoonPerson::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao
}