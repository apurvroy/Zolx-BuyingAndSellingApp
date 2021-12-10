package com.example.zolx.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zolx.databinding.ActivityRegisterBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.User
import com.google.android.material.snackbar.Snackbar
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
        var flag=true
        if(binding.firstNameRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the First Name!",Toast.LENGTH_LONG).show()
            binding.tilFirstNameRegister.error="Enter the First Name!"
//            binding.firstNameRegister.requestFocus()
            flag=false
        }else{
            binding.tilFirstNameRegister.error=null
        }
        if (binding.lastNameRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the Last Name!",Toast.LENGTH_LONG).show()
            binding.tilLastNameRegister.error="Enter the Last Name!"
//            binding.lastNameRegister.requestFocus()
            flag=false
        }else{
            binding.tilLastNameRegister.error=null
        }
        if(binding.emailRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the Email", Toast.LENGTH_LONG).show()
            binding.tilEmailRegister.error="Enter the Email!"
//            binding.emailRegister.requestFocus()
            flag=false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailRegister.text.toString().trim()).matches()) {
//            Toast.makeText(this, "Enter the correct Email", Toast.LENGTH_LONG).show()
            binding.tilEmailRegister.error="Enter the correct Email!"
//            binding.emailRegister.requestFocus()
            flag=false
        }
        else{
            binding.tilEmailRegister.error=null
        }
        if(binding.passwordRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the Password!",Toast.LENGTH_LONG).show()
            binding.tilPasswordRegister.error="Enter the Password!"
//            binding.passwordRegister.requestFocus()
            flag= false
        }
        else if(binding.passwordRegister.text.toString().trim().length<6){
//            Toast.makeText(this,"Enter the Password!",Toast.LENGTH_LONG).show()
            binding.tilPasswordRegister.error="Password should be at least 6 characters!"
//            binding.passwordRegister.requestFocus()
            flag=false
        }
        else{
            binding.tilPasswordRegister.error=null
        }
        if(binding.confirmPasswordRegister.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Enter the Confirm Password!",Toast.LENGTH_LONG).show()
            binding.tilConfirmPasswordRegister.error="Enter the Confirm Password!"
//            binding.confirmPasswordRegister.requestFocus()
            flag= false
        }
        else if(binding.passwordRegister.text.toString().trim()!=binding.confirmPasswordRegister.text.toString().trim()){
//            Toast.makeText(this,"Password is not matching",Toast.LENGTH_LONG).show()
            binding.tilConfirmPasswordRegister.error="Password is not matching"
//            binding.confirmPasswordRegister.requestFocus()
            flag=false
        }
        else{
            binding.tilConfirmPasswordRegister.error=null
        }
        if(!binding.cbTAndC.isChecked){
//            Toast.makeText(this,"You need to agree to the terms and conditions",Toast.LENGTH_LONG).show()
            binding.tvTAndC.error="You need to agree to the terms and conditions"
            flag= false
        }else{
            binding.tvTAndC.error=null
        }
        return flag
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
                        Snackbar.make(binding.btnRegister,task.exception!!.message.toString(),Snackbar.LENGTH_LONG).show()
//                        Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
        }
    }


    fun registrationSuccess(){
//        Snackbar.make(findViewById(R.id.btn_login),"successfully Registered!!",Snackbar.LENGTH_LONG).show()
        Toast.makeText(this,"successfully Registered!!",Toast.LENGTH_LONG).show()
    }

}
