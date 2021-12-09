package com.example.zolx.ui.activities


import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.zolx.R
import com.example.zolx.databinding.ActivityUserProfileBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.User
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.HashMap


class UserProfileActivity : AppCompatActivity() {
    private  lateinit var binding:ActivityUserProfileBinding
    val READ_STORAGE_PERMISSION_CODE =0
    private lateinit var mUserDetails: User
    private var mSelectedImageUri: Uri?=null
    private var mProfileImageUrl:String=""
    private val USER_PROFILE_IMAGE="User_Profile_Image"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        setSupportActionBar(binding.toolbarUserProfile)


        if(intent.hasExtra("user_details")){
            mUserDetails= intent.getParcelableExtra("user_details")!!
        }
        if(mUserDetails.profileCompleted==0){
            binding.tvTitle.text="Complete Profile"
            binding.toolbarUserProfile.setNavigationIcon(R.drawable.ic_baseline_keyboard_return_24)
            binding.btnUpdateProfile.setText(R.string.btn_submit)
            binding.firstNameProfile.setText(mUserDetails.firstName)
            binding.lastNameProfile.setText(mUserDetails.lastName)
            binding.emailProfile.setText(mUserDetails.email)


            binding.toolbarUserProfile.setNavigationOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,LoginActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }else{
            binding.tvTitle.setText(R.string.profile)
            binding.btnUpdateProfile.setText(R.string.btn_update)
            Glide.with(this)
                .load(mUserDetails.image)
                .placeholder(R.drawable.user_photo_background)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(binding.userPhoto)


            if(mUserDetails.mobile!=0L)
                binding.mobileNumberProfile.setText(mUserDetails.mobile.toString())

            if(mUserDetails.address.isNotEmpty()){
                binding.addressProfile.setText(mUserDetails.address)
            }
            if(mUserDetails.dob.isNotEmpty()){
                binding.dobProfile.setText(mUserDetails.dob)
            }


            if(mUserDetails.gender=="male")
                binding.rbMale.isChecked=true
            else
                binding.rbFemale.isChecked=true

            binding.firstNameProfile.setText(mUserDetails.firstName)
            binding.lastNameProfile.setText(mUserDetails.lastName)
            binding.emailProfile.setText(mUserDetails.email)

            binding.toolbarUserProfile.setNavigationOnClickListener {
                finish()
            }
        }


        //clicking on user photo
        binding.userPhoto.setOnClickListener {
            //if permission is already allowed
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){

                //Toast.makeText(this,"Now you have external storage permission",Toast.LENGTH_LONG).show()
                selectImage.launch("image/*")

            }else{

                //request for permission
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),READ_STORAGE_PERMISSION_CODE)

            }
        }

        //DOB
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            binding.dobProfile.setText("$dayOfMonth/$monthOfYear/$year")
        }

        binding.dobProfile.setOnClickListener {
            val dpd=DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.maxDate=System.currentTimeMillis()
            dpd.show()
        }


        binding.btnUpdateProfile.setOnClickListener {


            if(checkUserProfileDetails()){
//                Toast.makeText(this,"Profile Updated!",Toast.LENGTH_SHORT).show()
                if(mSelectedImageUri!=null)
                    FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageUri!!,USER_PROFILE_IMAGE)
                else{
                    updateProfileDetails()
                }

            }
        }



    }

    private fun updateProfileDetails(){
        val userHashMap= HashMap<String,Any>()

        val firstName=binding.firstNameProfile.text.toString().trim()
        val lastName=binding.lastNameProfile.text.toString().trim()
        val mobileNumber=binding.mobileNumberProfile.text.toString().trim()
        val address=binding.addressProfile.text.toString().trim()
        val gender=if(binding.rbMale.isChecked) "male" else "female"
        val dob=binding.dobProfile.text.toString()

        if(mProfileImageUrl.isNotEmpty()){
            userHashMap["image"]=mProfileImageUrl
        }
        userHashMap["firstName"]=firstName
        userHashMap["lastName"]=lastName
        userHashMap["mobile"]=mobileNumber.toLong()
        userHashMap["address"]=address
        userHashMap["gender"]=gender
        userHashMap["profileCompleted"]=1
        userHashMap["dob"]=dob

//        Log.e("user profile",userHashMap.toString())
        FirestoreClass().updateUserProfile(this,userHashMap)
    }

    //AFTER getting the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            //read external storage permission reqCode:0
            READ_STORAGE_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    Toast.makeText(this,"Now you have external storage permission",Toast.LENGTH_LONG).show()

                    // Permission is granted. Continue the action or workflow
                    // in your app.

                    selectImage.launch("image/*")

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(this,"You denied the permission,Now you can't upload your profile photo,You can still provide permission from settings",Toast.LENGTH_LONG).show()
                }
                return
            }

        }
    }
//    private fun openGalleryForImage(){
//        val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent,PICK_IMAGE_REQUEST_CODE)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode== Activity.RESULT_OK){
//            if(requestCode==PICK_IMAGE_REQUEST_CODE){
//                if(data!=null){
//
//                }
//            }
//        }
//    }
    val selectImage=registerForActivityResult(ActivityResultContracts.GetContent()){
            uri->
        mSelectedImageUri=uri
    //    binding.userPhoto.setImageURI(uri)
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.user_photo_background)
            .centerCrop()
            .apply(RequestOptions.circleCropTransform())
            .into(binding.userPhoto)

    }
    private fun checkUserProfileDetails():Boolean{
        var flag=true
        val mobile=binding.mobileNumberProfile.text.toString().trim().toLongOrNull()
        if(binding.firstNameProfile.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Please enter your first name!",Toast.LENGTH_LONG).show()
            binding.tilFirstNameProfile.error="Please enter your first name!"
//            binding.firstNameProfile.requestFocus()
            flag= false
        }else{
            binding.tilFirstNameProfile.error=null
        }
        if(binding.lastNameProfile.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Please enter your last name!",Toast.LENGTH_LONG).show()
            binding.tilLastNameProfile.error="Please enter your last name!"
//            binding.lastNameProfile.requestFocus()
            flag= false
        }else{
            binding.tilLastNameProfile.error=null
        }
        if(binding.mobileNumberProfile.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Please enter your mobile number!",Toast.LENGTH_LONG).show()
            binding.tilMobileNumberProfile.error="Please enter your mobile number!"
//            binding.mobileNumberProfile.requestFocus()
            flag= false
        }else if(mobile==null){
//            Toast.makeText(this, "Enter the valid mobile numbers(Only Digits)", Toast.LENGTH_LONG).show()
            binding.tilMobileNumberProfile.error="Enter the valid mobile numbers(Only Digits)"
//            binding.mobileNumberProfile.requestFocus()
            flag= false
        }
        else if(mobile<0){
//            Toast.makeText(this, "mobile number can not be negative", Toast.LENGTH_LONG).show()
            binding.tilMobileNumberProfile.error="mobile number can not be negative"
//            binding.mobileNumberProfile.requestFocus()
            flag= false
        }
        else if(binding.mobileNumberProfile.text.toString().trim().length !in 10 downTo 5){
//            Toast.makeText(this,"Please Enter a ten digit valid number!",Toast.LENGTH_LONG).show()
            binding.tilMobileNumberProfile.error="It should be at least 6 digits and at max 10 digits"
//            binding.mobileNumberProfile.requestFocus()
            flag= false
        }else{
            binding.tilMobileNumberProfile.error=null
        }
        if(binding.addressProfile.text.toString().trim().isEmpty()){
//            Toast.makeText(this,"Please enter your address!",Toast.LENGTH_LONG).show()
            binding.tilAddressProfile.error="Please enter your address!"
//            binding.addressProfile.requestFocus()
            flag= false
        }
//        else if(binding.dobProfile.text.toString().isEmpty()){
//            binding.dobProfile.error="Please enter your DOB!"
//            binding.dobProfile.requestFocus()
//            return false
//        }
        return flag
    }
    fun userProfileUpdateDone(){
        Toast.makeText(this,"your profile is updated successfully",Toast.LENGTH_SHORT).show()
        val intent= Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun imageUploadDone(imageUrl:String){
//        Toast.makeText(this,"your image is uploaded successfully image URL:$imageUrl",Toast.LENGTH_SHORT).show()
        mProfileImageUrl=imageUrl
        updateProfileDetails()
    }

}