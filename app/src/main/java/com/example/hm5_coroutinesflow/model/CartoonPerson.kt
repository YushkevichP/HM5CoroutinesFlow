package com.example.hm5_coroutinesflow.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class CartoonPerson(

    @PrimaryKey
    @SerializedName("id")
    val idApi: Int,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val nameApi: String,

    @ColumnInfo(name = "image")
    @SerializedName("image")// используется, чтоб переписать название из json в наше название.
    val imageApi: String,
)