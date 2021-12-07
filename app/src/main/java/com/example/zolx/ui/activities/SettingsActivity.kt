package com.example.zolx.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.zolx.R
import com.example.zolx.databinding.ActivitySettingsBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.User
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingsBinding
    private lateinit var mUserDetails:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarSetting.setNavigationOnClickListener {
            finish()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(this,LoginActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        binding.btnEdit.setOnClickListener {
            val intent=Intent(this,UserProfileActivity::class.java)
            intent.putExtra("user_details",mUserDetails)
            startActivity(intent)
        }

    }

    private fun getUserDetailsFromFirestore(){

        FirestoreClass().getUserDetails(this)

    }

    fun userDetailsDone(user:User){

        mUserDetails=user

        //loading user image
        Glide.with(this)
            .load(user.image)
            .placeholder(R.drawable.user_photo_background)
            .centerCrop()
            .apply(RequestOptions.circleCropTransform())
            .into(binding.userPhotoSettings)
        //assigning other values
        binding.nameSettings.text="${user.firstName} ${user.lastName}"
        binding.genderSettings.text=user.gender
        binding.emailSettings.text=user.email
        binding.mobileSettings.text= user.mobile.toString()
        binding.addressSettings.text=user.address
        binding.dobSettings.text=user.dob

    }

    override fun onResume() {
        super.onResume()
        getUserDetailsFromFirestore()
    }
}