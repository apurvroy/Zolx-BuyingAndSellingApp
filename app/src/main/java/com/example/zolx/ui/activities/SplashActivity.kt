package com.example.zolx.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.zolx.R
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.User
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //to move from splash screen
        Handler().postDelayed({


            val user=FirebaseAuth.getInstance().currentUser
            if(user==null){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                FirestoreClass().getUserDetails(this)
            }

        }, 2000)
    }
    fun userDetailsDone(user: User){
        if(user.profileCompleted==0){
            val intent=Intent(this, UserProfileActivity::class.java)
            intent.putExtra("user_details",user)
            startActivity(intent)
        }else{
            val intent=Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}