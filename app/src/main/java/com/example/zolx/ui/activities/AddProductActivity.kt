package com.example.zolx.ui.activities

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.zolx.R
import com.example.zolx.databinding.ActivityAddProductBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.Product
import com.example.zolx.models.User
import com.google.android.material.snackbar.Snackbar

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddProductBinding
    private val READ_STORAGE_PERMISSION_CODE =0
    private var mSelectedImageUri: Uri?=null
    private var mProductImageUrl:String=""
    private val PRODUCT_IMAGE="Product_Image"

    //to check whether we are going to upload or update a product
    private var editProduct=false
    private var mProductId=""


    private lateinit var mProductDetails:Product
    private lateinit var mUserDetails:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarAddProduct.setNavigationOnClickListener {
            finish()
        }

        // when we only want to update
        if(intent.hasExtra("product_id")){
            editProduct=true
            mProductId= intent.getStringExtra("product_id").toString()
            binding.addProductTitle.text = "Update Product"
            binding.btnUploadProduct.setText(R.string.btn_update)
            FirestoreClass().getMyProductDetails(this,mProductId)
        }

        binding.addProductIcon.setOnClickListener {
            //if permission is already allowed
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

                //Toast.makeText(this,"Now you have external storage permission",Toast.LENGTH_LONG).show()
                selectImage.launch("image/*")

            }else{

                //request for permission
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),READ_STORAGE_PERMISSION_CODE)

            }
        }

        binding.btnUploadProduct.setOnClickListener{
            startProgressIndicator()

            //while editing the product
            if(editProduct && checkProductDetailsToEdit()){
//                Log.e("tag","update")
                if(mSelectedImageUri==null){
                    mProductImageUrl=mProductDetails.image
                    updateProduct()
                }
                else{
                    FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageUri!!,PRODUCT_IMAGE)
                }


            }
            //while adding the product
            else if(!editProduct && checkProductDetails()){
//                Toast.makeText(this, "Your product is uploaded", Toast.LENGTH_LONG).show()
                FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageUri!!,PRODUCT_IMAGE)
            }
            else{
                stopProgressIndicator()
            }
        }

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
                    Snackbar.make(binding.btnUploadProduct,"You denied the permission,You can still provide permission from settings",Snackbar.LENGTH_LONG).show()
//                    Toast.makeText(this,"You denied the permission,Now you can't upload your product photo,You can still provide permission from settings",
//                        Toast.LENGTH_LONG).show()
                }
                return
            }

        }
    }

    val selectImage=registerForActivityResult(ActivityResultContracts.GetContent()){
            uri->
        mSelectedImageUri=uri
//    binding.userPhoto.setImageURI(uri)
        Glide.with(this)
            .load(uri)
            .into(binding.productImage)

    }

    private fun checkProductDetails():Boolean{
        var flag=true
        val price=binding.productPrice.text.toString().toIntOrNull()
        val quantity=binding.productQuantity.text.toString().toIntOrNull()
        if(mSelectedImageUri==null) {
            Snackbar.make(binding.btnUploadProduct,"Please upload the product image",Snackbar.LENGTH_LONG).show()
//            Toast.makeText(this, "Please upload the product image", Toast.LENGTH_LONG).show()
            flag=false
        }
        if(binding.productName.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the product name", Toast.LENGTH_LONG).show()
            binding.tilProductName.error="Enter the product name"
//            binding.productName.requestFocus()
            flag= false
        }else{
            binding.tilProductName.error=null
        }
        if(binding.productPrice.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the product price", Toast.LENGTH_LONG).show()
            binding.tilProductPrice.error="Enter the product price"
//            binding.productPrice.requestFocus()
            flag=false
        }
        else if(price==null){
//            Toast.makeText(this, "Enter the valid product price(Only Digits)", Toast.LENGTH_LONG).show()
            binding.tilProductPrice.error="Enter the valid product price(Only Digits)"
//            binding.productPrice.requestFocus()
            flag= false
        }
        else if(price<0){
//            Toast.makeText(this,"Price can not be negative",Toast.LENGTH_LONG).show()
            binding.tilProductPrice.error="Price can not be negative"
//            binding.productPrice.requestFocus()
            flag= false
        }else{
            binding.tilProductPrice.error=null
        }
        if(binding.productQuantity.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the quantity", Toast.LENGTH_LONG).show()
            binding.tilProductQuantity.error="Enter the quantity"
//            binding.productQuantity.requestFocus()
            flag= false
        }
        else if(quantity==null){
//            Toast.makeText(this, "Enter the valid product quantity(Only Digits)", Toast.LENGTH_LONG).show()
            binding.tilProductQuantity.error="Enter the valid product quantity(Only Digits)"
//            binding.productQuantity.requestFocus()
            flag= false
        }
        else if(quantity<0){
//            Toast.makeText(this,"Product Quantity can not be negative",Toast.LENGTH_LONG).show()
            binding.tilProductQuantity.error="Product Quantity can not be negative"
//            binding.productQuantity.requestFocus()
            flag= false
        }else{
            binding.tilProductQuantity.error=null
        }
        if(binding.productDetails.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the product details", Toast.LENGTH_LONG).show()
            binding.tilProductDetails.error="Enter the product details"
//            binding.productDetails.requestFocus()
            flag=false
        }else{
            binding.tilProductDetails.error=null
        }

        return flag

    }

    private fun checkProductDetailsToEdit(): Boolean {
        var flag=true
        val price=binding.productPrice.text.toString().toIntOrNull()
        val quantity=binding.productQuantity.text.toString().toIntOrNull()
//        if(mSelectedImageUri==null) {
//            Toast.makeText(this, "Please upload the product image", Toast.LENGTH_LONG).show()
//            return false
//        }
        if(binding.productName.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the product name", Toast.LENGTH_LONG).show()
            binding.tilProductName.error="Enter the product name"
//            binding.productName.requestFocus()
            flag= false
        }else{
            binding.tilProductName.error=null
        }
        if(binding.productPrice.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the product price", Toast.LENGTH_LONG).show()
            binding.tilProductPrice.error="Enter the product price"
//            binding.productPrice.requestFocus()
            flag=false
        }
        else if(price==null){
//            Toast.makeText(this, "Enter the valid product price(Only Digits)", Toast.LENGTH_LONG).show()
            binding.tilProductPrice.error="Enter the valid product price(Only Digits)"
//            binding.productPrice.requestFocus()
            flag= false
        }
        else if(price<0){
//            Toast.makeText(this,"Price can not be negative",Toast.LENGTH_LONG).show()
            binding.tilProductPrice.error="Price can not be negative"
//            binding.productPrice.requestFocus()
            flag= false
        }else{
            binding.tilProductPrice.error=null
        }
        if(binding.productQuantity.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the quantity", Toast.LENGTH_LONG).show()
            binding.tilProductQuantity.error="Enter the quantity"
//            binding.productQuantity.requestFocus()
            flag= false
        }
        else if(quantity==null){
//            Toast.makeText(this, "Enter the valid product quantity(Only Digits)", Toast.LENGTH_LONG).show()
            binding.tilProductQuantity.error="Enter the valid product quantity(Only Digits)"
//            binding.productQuantity.requestFocus()
            flag= false
        }
        else if(quantity<0){
//            Toast.makeText(this,"Product Quantity can not be negative",Toast.LENGTH_LONG).show()
            binding.tilProductQuantity.error="Product Quantity can not be negative"
//            binding.productQuantity.requestFocus()
            flag= false
        }else{
            binding.tilProductQuantity.error=null
        }
        if(binding.productDetails.text.toString().trim().isEmpty()){
//            Toast.makeText(this, "Enter the product details", Toast.LENGTH_LONG).show()
            binding.tilProductDetails.error="Enter the product details"
//            binding.productDetails.requestFocus()
            flag=false
        }else{
            binding.tilProductDetails.error=null
        }

        return flag
//        if(binding.productName.text.toString().trim().isEmpty()){
////            Toast.makeText(this, "Enter the product name", Toast.LENGTH_LONG).show()
//            binding.productName.error="Enter the product name"
//            binding.productName.requestFocus()
//            return false
//        }
//        else if(binding.productPrice.text.toString().trim().isEmpty()){
////            Toast.makeText(this, "Enter the product price", Toast.LENGTH_LONG).show()
//            binding.productPrice.error="Enter the product price"
//            binding.productPrice.requestFocus()
//            return false
//        }
//        else if(price==null){
////            Toast.makeText(this, "Enter the valid product price(Only Digits)", Toast.LENGTH_LONG).show()
//            binding.productPrice.error="Enter the valid product price(Only Digits)"
//            binding.productPrice.requestFocus()
//            return false
//        }
//        else if(price<0){
////            Toast.makeText(this,"Price can not be negative",Toast.LENGTH_LONG).show()
//            binding.productPrice.error="Price can not be negative"
//            binding.productPrice.requestFocus()
//            return false
//        }
//        else if(binding.productQuantity.text.toString().trim().isEmpty()){
////            Toast.makeText(this, "Enter the quantity", Toast.LENGTH_LONG).show()
//            binding.productQuantity.error="Enter the quantity"
//            binding.productQuantity.requestFocus()
//            return false
//        }
//        else if(quantity==null){
////            Toast.makeText(this, "Enter the valid product quantity(Only Digits)", Toast.LENGTH_LONG).show()
//            binding.productQuantity.error="Enter the valid product quantity(Only Digits)"
//            binding.productQuantity.requestFocus()
//            return false
//        }
//        else if(quantity<0){
////            Toast.makeText(this,"Product Quantity can not be negative",Toast.LENGTH_LONG).show()
//            binding.productQuantity.error="Product Quantity can not be negative"
//            binding.productQuantity.requestFocus()
//            return false
//        }
//        else if(binding.productDetails.text.toString().trim().isEmpty()){
////            Toast.makeText(this, "Enter the product details", Toast.LENGTH_LONG).show()
//            binding.productDetails.error="Enter the product details"
//            binding.productDetails.requestFocus()
//            return false
//        }

//        return true
    }

    fun imageUploadDone(imageUrl:String){
//        Toast.makeText(this,"your product image is uploaded successfully image URL:$imageUrl",Toast.LENGTH_SHORT).show()
        mProductImageUrl=imageUrl

        if(editProduct){
            updateProduct()
        }else{
            getUserDetailsFromFirestore()
        }


    }

    private fun uploadProductDetails(){
        val username="${mUserDetails.firstName} ${mUserDetails.lastName}"
        val product= Product(
            user_id = mUserDetails.id,
            user_name = username,
            title = binding.productName.text.toString().trim(),
            price = binding.productPrice.text.toString().trim(),
            details = binding.productDetails.text.toString().trim(),
            quantity = binding.productQuantity.text.toString().trim(),
            image = mProductImageUrl
        )

        FirestoreClass().uploadProductDetails(this,product)
    }

    fun productUploadDone(){
        stopProgressIndicator()
        Toast.makeText(this,"your product is uploaded successfully",Toast.LENGTH_SHORT).show()
//        val intent= Intent(this,DashboardActivity::class.java)
//        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
        finish()
    }

    private fun getUserDetailsFromFirestore(){

        FirestoreClass().getUserDetails(this)

    }
    fun userDetailsDone(user: User){
        mUserDetails=user
        uploadProductDetails()
    }


    //after getting the product details
    fun productDetailsDone(product:Product){
        mProductDetails=product
        Glide.with(this)
            .load(mProductDetails.image)
            .into(binding.productImage)
        binding.productName.setText(mProductDetails.title)
        binding.productPrice.setText(mProductDetails.price)
        binding.productDetails.setText(mProductDetails.details)
        binding.productQuantity.setText(mProductDetails.quantity)
    }

    fun updateProduct(){
        val productHashMap=HashMap<String,Any>()
        val pTitle=binding.productName.text.toString().trim()
        val pPrice=binding.productPrice.text.toString().trim()
        val pQuantity=binding.productQuantity.text.toString().trim()
        val pDetails=binding.productDetails.text.toString().trim()


        productHashMap["image"]=mProductImageUrl
        productHashMap["price"]=pPrice
        productHashMap["title"]=pTitle
        productHashMap["quantity"]=pQuantity
        productHashMap["details"]=pDetails


        FirestoreClass().updateProduct(this,productHashMap,mProductId)
//                Log.e("tag",productHashMap.toString())
    }

    fun productUpdateDone(){
        stopProgressIndicator()
        Toast.makeText(this,"your product is updated successfully",Toast.LENGTH_SHORT).show()
        finish()
    }
    fun startProgressIndicator(){
        binding.addProductCpi.visibility= View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    fun stopProgressIndicator(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.addProductCpi.visibility=View.GONE
    }
}