package com.example.zolx.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    val user_id:String="",
    var order_id:String="",
    val items: ArrayList<Cart> =ArrayList(),
    val title:String="",
    val orderDate:String="",
    val address:String="",
    val total_amount:String=""

):Parcelable