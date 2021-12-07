package com.example.zolx.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart(
    val user_id:String="",
    val seller_id:String="",
    val title:String="",
    val price:String="",
    var quantity:String="",
    var checkout_quantity:String="",
    val image:String="",
    val product_id:String="",
    var cart_id:String=""
):Parcelable