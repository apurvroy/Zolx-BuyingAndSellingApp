package com.example.zolx.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id:String="",
    val firstName:String="",
    val lastName:String="",
    val gender:String="",
    val email:String="",
    val image:String="",
    val mobile:Long=0,
    val address:String="",
    val dob:String="",
    val profileCompleted:Int=0):Parcelable