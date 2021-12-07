package com.example.zolx.ui.activities

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zolx.databinding.ActivityCartBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.Cart
import com.example.zolx.models.Order
import com.example.zolx.models.Product
import com.example.zolx.ui.adapters.CartItemsAdapter
import java.text.DateFormat

class CartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCartBinding
    private lateinit var mAdapter:CartItemsAdapter
    private lateinit var mProductList:ArrayList<Product>
    private lateinit var mCartItemsList:ArrayList<Cart>
    private  var mtotalamount:Double=0.0
    private val mDeliveryCharge:Double=40.0
    private var mUserAddress:String=""
    private lateinit var mOrderDetails:Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarMyCart.setNavigationOnClickListener {
            finish()
        }
        binding.btnPlaceOrder.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                placeOrder()
            }
        }
        FirestoreClass().getUserAddress(this)

    }

    private fun getCartItems(){
        FirestoreClass().getCartItems(this)
    }
    private  fun getProducts(){
        FirestoreClass().getAllProductsDetails(this)
    }

    fun getCartItemsDone(cartList:ArrayList<Cart>){

        for(product in mProductList){
            for(cartItem in cartList){
                if(product.product_id==cartItem.product_id){
                    cartItem.quantity=product.quantity

                    if(product.quantity.toInt()==0){
                        cartItem.checkout_quantity=product.quantity
                    }
                }
            }
        }

        mCartItemsList=cartList

        if(mCartItemsList.size>0){
            binding.rvCartItems.visibility= View.VISIBLE
            binding.noItemsFound.visibility=View.GONE
            binding.llCheckout.visibility=View.VISIBLE

            val recyclerView=binding.rvCartItems
            recyclerView.layoutManager=LinearLayoutManager(this)
            mAdapter=CartItemsAdapter(this,cartList)
            recyclerView.adapter=mAdapter
            var finalPrice =0.0
            for(item in mCartItemsList){

                val availableQuantity=item.quantity.toInt()
                if(availableQuantity>0){
                    val price=item.price.toDouble()
                    val quantity=item.checkout_quantity.toInt()
                    finalPrice+=(price*quantity)
                }

            }

            binding.price.text="\u20B9 ${finalPrice}"
            if(finalPrice>=499){
                binding.deliveryCharges.text="Free"
                binding.totalAmount.text="\u20B9 ${finalPrice}"
                mtotalamount=finalPrice

            }else{
                binding.deliveryCharges.text="\u20B9 $mDeliveryCharge"
                binding.totalAmount.text="\u20B9 ${finalPrice+mDeliveryCharge}"
                mtotalamount=finalPrice+mDeliveryCharge
            }

        }else{
            binding.rvCartItems.visibility= View.GONE
            binding.llCheckout.visibility=View.GONE
            binding.noItemsFound.visibility=View.VISIBLE
        }
    }

    fun getProductsFromFireStoreDone(productList:ArrayList<Product>){
        mProductList=productList
        getCartItems()
    }
    fun cartItemRemoveDone(){
        Toast.makeText(this,"item is removed from your cart!",Toast.LENGTH_SHORT).show()
        getCartItems()
    }

    fun cartItemUpdateDone(){
        getCartItems()
    }

    //for placing a order
    @RequiresApi(Build.VERSION_CODES.N)
    private fun placeOrder(){
        val currentDate = Calendar.getInstance().time
        val date = DateFormat.getDateInstance(DateFormat.FULL).format(currentDate)

        if(mUserAddress!=""){
            mOrderDetails= Order(
                user_id = FirestoreClass().getCurrentUserID(),
                items = mCartItemsList,
                title ="My order ${System.currentTimeMillis()}",
                orderDate=date,
                address = mUserAddress,
                total_amount = mtotalamount.toString()
            )
            FirestoreClass().addOrder(this,mOrderDetails)
        }

    }
    fun placeOrderDone(){
        FirestoreClass().updateAllDetails(this,mCartItemsList,mOrderDetails)
    }


    fun allDetailsUpdateDone(){
        Toast.makeText(this,"Your order is placed successfully!",Toast.LENGTH_SHORT).show()
        val intent=Intent(this,DashboardActivity::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun getUserAddressDone(address:String){
        mUserAddress=address
    }


    override fun onResume() {
        getProducts()
        super.onResume()

    }
}