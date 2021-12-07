package com.example.zolx.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SoldProduct(
    val user_id:String="",
    val title:String="",
    val sold_quantity:String="",
    val order_id:String="",
    val order_date:String="",
    val total_amount:String="",
    val image:String="",
    val address:String="",
    var sold_product_id:String="",
):Parcelable