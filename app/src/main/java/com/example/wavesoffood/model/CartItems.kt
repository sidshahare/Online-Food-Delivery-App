package com.example.wavesoffood.model

//data class CartItems(
//    val foodName: String? = null,
//    val foodPrice: String? = null,
//    val foodDescription: String? = null,
//    val foodImage: String? = null,
//    val foodQuantity: Int? = null,
//    val foodIngredient: String? = null
//)

data class CartItems(
    var foodName: String?=null,
    var foodPrice: String?=null,
    var foodQuantity: Int?=null,
    var foodImage: String?=null,
    var foodDescription: String?=null,
    var foodIngredients: String?=null

)