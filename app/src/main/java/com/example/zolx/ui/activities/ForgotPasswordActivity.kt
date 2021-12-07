package com.example.zolx.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zolx.databinding.ActivityForgotPasswordBinding
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
            Toast.makeText(this, "Enter the Email", Toast.LENGTH_LONG).show()
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.fpEmail.text.toString().trim()).matches()) {
            Toast.makeText(this, "Enter the correct Email", Toast.LENGTH_LONG).show()
        }
        else{
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){

                        Toast.makeText(this,"Email sent successfully to reset your password!",Toast.LENGTH_LONG).show()
                        finish()

                    }else{
                        Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}