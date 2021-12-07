package com.example.zolx.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val user_id:String="",
    val user_name:String="",
    val title:String="",
    val price:String="",
    val details:String="",
    val quantity:String="",
    val image:String="",
    var product_id:String=""

):Parcelable
