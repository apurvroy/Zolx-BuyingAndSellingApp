package com.example.zolx.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zolx.databinding.ActivityRegisterBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarRegister.setNavigationOnClickListener {
            finish()
        }
        binding.tvLogin.setOnClickListener {
//            val intent= Intent(this,LoginActivity::class.java)
//            startActivity(intent)
            onBackPressed()
        }
        binding.btnRegister.setOnClickListener(){
            registerUser()
        }
    }

    //validations
    private fun checkRegisterDetails():Boolean{
        if(binding.firstNameRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the First Name!",Toast.LENGTH_LONG).show()
            binding.firstNameRegister.error="Enter the First Name!"
            binding.firstNameRegister.requestFocus()
            return false
        }
        else if (binding.lastNameRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the Last Name!",Toast.LENGTH_LONG).show()
            binding.lastNameRegister.error="Enter the Last Name!"
            binding.lastNameRegister.requestFocus()
            return false
        }
        else if(binding.emailRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the Email", Toast.LENGTH_LONG).show()
            binding.emailRegister.error="Enter the Email!"
            binding.emailRegister.requestFocus()
            return false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailRegister.text.toString().trim()).matches()) {
//            Toast.makeText(this, "Enter the correct Email", Toast.LENGTH_LONG).show()
            binding.emailRegister.error="Enter the correct Email!"
            binding.emailRegister.requestFocus()
            return false
        }
        else if(binding.passwordRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the Password!",Toast.LENGTH_LONG).show()
            binding.passwordRegister.error="Enter the Password!"
            binding.passwordRegister.requestFocus()
            return false
        }
        else if(binding.passwordRegister.text.toString().trim().length<6){
//            Toast.makeText(this,"Enter the Password!",Toast.LENGTH_LONG).show()
            binding.passwordRegister.error="Password should be at least 6 characters!"
            binding.passwordRegister.requestFocus()
            return false
        }
        else if(binding.confirmPasswordRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the Confirm Password!",Toast.LENGTH_LONG).show()
            binding.confirmPasswordRegister.error="Enter the Confirm Password!"
            binding.confirmPasswordRegister.requestFocus()
            return false
        }
        else if(binding.passwordRegister.text.toString().trim()!=binding.confirmPasswordRegister.text.toString().trim()){
//            Toast.makeText(this,"Password is not matching",Toast.LENGTH_LONG).show()
            binding.confirmPasswordRegister.error="Password is not matching"
            binding.confirmPasswordRegister.requestFocus()
            return false
        }
        else if(!binding.cbTAndC.isChecked){
            Toast.makeText(this,"You need to agree to the terms and conditions",Toast.LENGTH_LONG).show()
            binding.tvTAndC.error="You need to agree to the terms and conditions"
            return false
        }
        else{

            return true
        }
    }

    //creating a new user in the database
    private fun registerUser(){
        //validation
        if(checkRegisterDetails()){


            val email:String=binding.emailRegister.text.toString().trim()
            val password:String=binding.passwordRegister.text.toString().trim()

            //registering a user using firebase
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task->


                    if(task.isSuccessful){
                        val firebaseUser:FirebaseUser= task.result!!.user!!

                        val user= User(
                            id=firebaseUser.uid,
                            firstName = binding.firstNameRegister.text.toString().trim(),
                            lastName = binding.lastNameRegister.text.toString().trim(),
                            email=binding.emailRegister.text.toString().trim()

                        )
                        FirestoreClass().registerUser(this,user)


                        FirebaseAuth.getInstance().signOut()
                        finish()

                    }else{
//                      Log.e("firebase",task.exception!!.message.toString())
                        Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
        }
    }


    fun registrationSuccess(){

        Toast.makeText(this,"successfully Registered!!",Toast.LENGTH_LONG).show()
    }

}
