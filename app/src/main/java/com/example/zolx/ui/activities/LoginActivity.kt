package com.example.zolx.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zolx.databinding.ActivityLoginBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            val intent= Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener{
            logInUser()
        }
        binding.forgotPassword.setOnClickListener {
            val intent=Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
//        binding.forgotPassword.setOnClickListener {
//            Toast.makeText(this,"Drink a glass of water and try to remember it again :)",Toast.LENGTH_LONG).show()
//        }

    }

    fun userLoggedIn(user:User){

//        Toast.makeText(this,"Logged in Successfully!", Toast.LENGTH_SHORT).show()
        //user details
//        Log.i("first name",user.firstName)
//        Log.i("Last name",user.lastName)
//        Log.i("Email",user.email)

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

    //validation and verification
    private fun checkLoginDetails():Boolean {
        var flag=true
        if(binding.emailLogin.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the Email!", Toast.LENGTH_SHORT).show()
            binding.tilEmailLogin.error = "Enter the Email"
            flag=false

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailLogin.text.toString().trim()).matches()) {
//            Toast.makeText(this, "Enter the correct Email", Toast.LENGTH_SHORT).show()
            binding.tilEmailLogin.error = "Enter the correct Email"
            flag= false
        }else{
            binding.tilEmailLogin.error=null
        }
        if(binding.passwordLogin.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the Password!",Toast.LENGTH_SHORT).show()
            binding.tilPasswordLogin.error = "Enter the password"
            flag= false
        }else{
            binding.tilPasswordLogin.error=null
        }
        return flag
    }
    private fun logInUser(){
        if(checkLoginDetails()){

            val email=binding.emailLogin.text.toString().trim()
            val password=binding.passwordLogin.text.toString().trim()

            //signIn using firebase
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task->


                    if(task.isSuccessful){
                        FirestoreClass().getUserDetails(this)

                    }else{
                        Snackbar.make(binding.btnLogin,task.exception!!.message.toString(),Snackbar.LENGTH_LONG).show()
//                        Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }


                }

        }
    }

}

