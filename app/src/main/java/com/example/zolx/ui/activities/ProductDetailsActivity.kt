package com.example.zolx.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.zolx.R
import com.example.zolx.databinding.ActivityProductDetailsBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.Cart
import com.example.zolx.models.Product
import com.google.android.material.snackbar.Snackbar

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProductDetailsBinding
    private var mProductId:String=""
    private var mOwnerId:String=""
    private var mOwnerNumber:String=""
    private lateinit var mProductDetails:Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarProductDetails.setNavigationOnClickListener {
            finish()
        }

        if(intent.hasExtra("product_id")){
            mProductId= intent.getStringExtra("product_id")!!

        }
        if(intent.hasExtra("owner_id")){
            mOwnerId= intent.getStringExtra("owner_id")!!
        }
        getOwnerNumberAndName()

        binding.btnAddToCart.setOnClickListener {
            if(mProductDetails.quantity.toInt()==0){
                Snackbar.make(binding.btnAddToCart,"Currently the product is out of stock!",Snackbar.LENGTH_SHORT).show()
//                Toast.makeText(this,"Currently the product is out of stock!",Toast.LENGTH_LONG).show()
            }else{
                addToCart()
            }
        }
        binding.btnGoToCart.setOnClickListener {
            val intent= Intent(this,CartActivity::class.java)
            startActivity(intent)
        }
    }

    fun getProductDetails(){
        FirestoreClass().getProductDetails(this,mProductId)
    }
    fun productDetailsDone(product:Product){
        mProductDetails=product


        Glide.with(this).load(product.image).into(binding.productDetailsImage)
        binding.productDetailsProductTitle.text=product.title
        binding.productDetailsProductPrice.text="₹ ${product.price}"
        binding.productDetails.text=product.details
        binding.productDetailsQuantity.text=product.quantity

        if(product.quantity.toInt()==0){
            binding.productDetailsQuantity.text="Out of stock"
            binding.productDetailsQuantity.setTextColor(ContextCompat.getColor(applicationContext,R.color.red))
        }else{
            if(FirestoreClass().getCurrentUserID()!=product.user_id){
                FirestoreClass().checkIfItemAlreadyInCart(this,mProductId)
            }
        }


    }

    private fun addToCart(){
        val cartItem= Cart(
            user_id = FirestoreClass().getCurrentUserID(),
            seller_id=mOwnerId,
            product_id = mProductId,
            title = mProductDetails.title,
            price = mProductDetails.price,
            image = mProductDetails.image,
            checkout_quantity = "1"
        )
        FirestoreClass().addCartItems(this,cartItem)
    }
    fun addToCartDone(){
        binding.btnAddToCart.visibility=View.GONE
        binding.btnGoToCart.visibility=View.VISIBLE
        Snackbar.make(binding.btnAddToCart,"added to the cart",Snackbar.LENGTH_LONG).show()
//        Toast.makeText(this,"added to the cart",Toast.LENGTH_SHORT).show()
    }
    fun productAlreadyInCart(){
        binding.btnAddToCart.visibility=View.GONE
        binding.btnGoToCart.visibility=View.VISIBLE
    }

    fun getOwnerNumberAndName(){
        FirestoreClass().getContactNumberAndName(this,mOwnerId)
    }

    fun getContactNumberAndNameDone(mobile:String,name:String){
        mOwnerNumber=mobile
        binding.productDetailsSellerContactsNumber.text = "Mobile: $mOwnerNumber"

        if(FirestoreClass().getCurrentUserID()==mOwnerId){
            binding.productDetailsSellerName.text="Name: $name(You)"
        }else{
            binding.productDetailsSellerName.text="Name: $name"
        }
    }

    override fun onResume() {
        getProductDetails()
        if(FirestoreClass().getCurrentUserID()==mOwnerId){
            binding.btnAddToCart.visibility= View.GONE
            binding.btnGoToCart.visibility=View.GONE
        }else{
            binding.btnAddToCart.visibility=View.VISIBLE
            binding.btnGoToCart.visibility=View.VISIBLE
        }
        super.onResume()

    }
}