package com.example.hm5_coroutinesflow.model


sealed class ItemType<out T> {

    data class Content<T>(val data: T) : ItemType<T>()

    object Loading : ItemType<Nothing>()

}


//sealed class ItemType {
//
//    data class CartoonPerson (
//
//        //  https://youtu.be/IDVxFjLeecA?t=10566
//        @SerializedName("id")
//        val idApi: Int,
//        @SerializedName("name")
//        val nameApi: String,
//        @SerializedName("image")// используется, чтоб переписать название из json в наше название.
//        val imageApi: String,
//    ) : ItemType()
//
//    object Loading : ItemType()
//}
//
//
//
//data class ResponseApi(
//    val results: List<ItemType.CartoonPerson>
//)
