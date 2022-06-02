package com.example.hm5_coroutinesflow.database

import android.app.Application
import android.content.Context
import androidx.room.Room

class Hw5CoroutinesFlowDatabase : Application() {

    private var _dataBase: AppDatabase? = null
    val dataBase
        get() = requireNotNull(_dataBase) {
            "Some troubles with Database"
        }

    override fun onCreate() {
        super.onCreate()

        //db init
        _dataBase = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "app_database"
        )
            .allowMainThreadQueries()
            .build()
    }
}

val Context.appDataBase: AppDatabase
    get() = when {
        this is Hw5CoroutinesFlowDatabase -> dataBase
        else -> applicationContext.appDataBase
    }