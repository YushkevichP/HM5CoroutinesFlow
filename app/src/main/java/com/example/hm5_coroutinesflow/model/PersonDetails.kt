package com.example.hm5_coroutinesflow.model

import com.google.gson.annotations.SerializedName

data class PersonDetails(

    val id: Int,
    val name: String,
    @SerializedName("image")
    val avatarApiDetails: String,
    // look at the api
    val gender: String,
    val status: String,
)
