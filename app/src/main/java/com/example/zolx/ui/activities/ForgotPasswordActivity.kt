package com.example.zolx.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zolx.databinding.ActivityForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener{
            changePassword()
        }

    }

    private fun changePassword() {
        val email=binding.fpEmail.text.toString().trim()
        if(binding.fpEmail.text.toString().trim().isEmpty()){
            binding.tilFpEmail.error="Enter the Email"
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.fpEmail.text.toString().trim()).matches()) {
            binding.tilFpEmail.error="Enter the correct Email"
        }
        else{
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){

                        Snackbar.make(binding.btnSubmit,"Email sent successfully to reset your password!",Snackbar.LENGTH_LONG).show()
                        binding.fpEmail.text?.clear()
                        binding.tilFpEmail.error=null
//                        Toast.makeText(this,"Email sent successfully to reset your password!",Toast.LENGTH_LONG).show()
//                        finish()

                    }else{
//                        Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                        Snackbar.make(binding.btnSubmit,task.exception!!.message.toString(),Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }
}