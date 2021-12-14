package com.example.zolx.firestore

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import com.example.zolx.models.*
import com.example.zolx.ui.activities.*
import com.example.zolx.ui.fragments.HomeFragment
import com.example.zolx.ui.fragments.OrdersFragment
import com.example.zolx.ui.fragments.ProductsFragment
import com.example.zolx.ui.fragments.SoldProductsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {


    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, user: User) {
        //creating a collection named users if it does not already exist.
        mFirestore.collection("users")
            //document id for collection users
            .document(user.id)
            //user is the fields
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.registrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.stopProgressIndicator()
                Log.e("firestore", "Error while registering the user", e)
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }


    //retrieving the user details
    fun getUserDetails(activity: Activity) {
        mFirestore.collection("users")
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
//                Log.i("firestore", document.toString())

                //document snapshot to user data model(User Class)
                val user = document.toObject(User::class.java)!!


                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedIn(user)
                    }
                    is SettingsActivity->{
                        activity.userDetailsDone(user)
                    }
                    is AddProductActivity->{
                        activity.userDetailsDone(user)
                    }
                    is SplashActivity->{
                        activity.userDetailsDone(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("firestore", "Error getting document", e)
            }
    }


    //updating the user profile
    fun updateUserProfile(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFirestore.collection("users")
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateDone()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("firestore", "Error updating user profile", e)
            }

    }


    //uploading images to cloud storage
    fun uploadImageToCloudStorage(activity: Activity, imageUri: Uri,imageType:String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType+System.currentTimeMillis()+"."
                    + MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(imageUri))
        )
        sRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                //image is successfully uploaded

                //to get downloadable url
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e(
                            "firebase image url: ",
                            taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                        )
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadDone(uri.toString())
                            }
                            is AddProductActivity->{
                                activity.imageUploadDone(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {

                    }
                    is AddProductActivity->{
                        activity.stopProgressIndicator()
                    }
                }
                Log.e(activity.javaClass.simpleName, e.message, e)
            }
    }

    fun uploadProductDetails(activity:AddProductActivity,productinfo:Product){
        mFirestore.collection("products")
            .document()
            .set(productinfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadDone()
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error wile uploading product details",e)

            }
    }

    fun getProductDetails(fragment: Fragment){
        mFirestore.collection("products")
            .whereEqualTo("user_id", getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
//                Log.i("product List",document.documents.toString())
                val productsList=ArrayList<Product>()
                for(i in document.documents){

                    val product=i.toObject(Product::class.java)
                    product!!.product_id=i.id

                    productsList.add(product)
                }

                when(fragment){
                    is ProductsFragment->{
                        fragment.productsListDone(productsList)
                    }
                }
            }
            .addOnFailureListener {e->
                Log.e("firestore","Error while getting products details",e)
            }
    }
    fun getProductDetails(activity:ProductDetailsActivity,productID: String){
        mFirestore.collection("products")
            .document(productID)
            .get()
            .addOnSuccessListener { document->
//                Log.i("product List",document.toString())
                val product=document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsDone(product)
                }
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while getting products details",e)
            }
    }
    fun getHomeItems(fragment:HomeFragment){
        mFirestore.collection("products")
            .get()
            .addOnSuccessListener { document->
//                Log.i("firestore",document.documents.toString())
                val productsList=ArrayList<Product>()
                for(i in document.documents){

                    val product=i.toObject(Product::class.java)
                    product!!.product_id=i.id

                    productsList.add(product)
                }
                fragment.productListDone(productsList)

            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while getting home products details",e)
            }
    }
    fun deleteProduct(fragment:ProductsFragment,productID:String){
        mFirestore.collection("products")
            .document(productID)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteDone()
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while deleting product",e)
            }
    }

    fun addCartItems(activity:ProductDetailsActivity,cart:Cart){
        mFirestore.collection("cart_items")
            .document()
            .set(cart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartDone()
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while adding product to cart",e)
            }
    }
    fun checkIfItemAlreadyInCart(activity: ProductDetailsActivity,productId: String){
        mFirestore.collection("cart_items")
            .whereEqualTo("user_id",getCurrentUserID())
            .whereEqualTo("product_id",productId)
            .get()
            .addOnSuccessListener { document->
                if(document.documents.size>0){
                    activity.productAlreadyInCart()
                }
            }
            .addOnFailureListener {e->
                Log.e("firestore","Error while adding product to cart",e)
            }
    }

    fun getCartItems(activity: Activity){
        mFirestore.collection("cart_items")
            .whereEqualTo("user_id",getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                val cartList=ArrayList<Cart>()
                for(i in document.documents){

                    val item=i.toObject(Cart::class.java)
                    item!!.cart_id=i.id
                    cartList.add(item)
                }
                when(activity){
                    is CartActivity->{
                        activity.getCartItemsDone(cartList)
                    }
                }
            }
            .addOnFailureListener {e->
                Log.e("firestore","Error while getting cart items",e)
            }
    }
    fun getAllProductsDetails(activity: CartActivity){
        mFirestore.collection("products")
            .get()
            .addOnSuccessListener { document->

                val productsList=ArrayList<Product>()
                for(i in document.documents){
                    val product=i.toObject(Product::class.java)
                    product!!.product_id=i.id
                    productsList.add(product)
                }
                activity.getProductsFromFireStoreDone(productsList)

            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while getting  products details",e)
            }
    }
    fun removeItemFromCart(context: Context,cart_id:String){
        mFirestore.collection("cart_items")
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when(context){
                    is CartActivity->{
                        context.cartItemRemoveDone()
                    }
                }
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while deleting  cart items",e)
            }
    }
    fun updateMyCart(context: Context,cart_id:String,itemHashMap:HashMap<String,Any>){
        mFirestore.collection("cart_items")
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when(context){
                    is CartActivity->{
                        context.cartItemUpdateDone()
                    }
                }
            }
            .addOnFailureListener {e->
                Log.e("firestore","Error while updating  cart items",e)
            }
    }
    fun getUserAddress(activity: CartActivity){
        mFirestore.collection("users")
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener{ document->
               val user= document.toObject(User::class.java)
                activity.getUserAddressDone(user!!.address)
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while getting user address",e)
            }
    }
    fun getContactNumberAndName(activity: ProductDetailsActivity,user_id:String){
        mFirestore.collection("users")
            .document(user_id)
            .get()
            .addOnSuccessListener { document->
                val user=document.toObject(User::class.java)
//                Log.e("firestore",user.toString())
//                Log.e("firestore",user!!.mobile.toString())
                val userName=user!!.firstName+" "+user!!.lastName
                activity.getContactNumberAndNameDone(user!!.mobile.toString(),userName)
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while getting seller contact number",e)
            }
    }

    fun addOrder(activity: CartActivity,order:Order){
        mFirestore.collection("orders")
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.placeOrderDone()
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while adding a order",e)
            }
    }

    fun updateAllDetails(activity: CartActivity,cartList:ArrayList<Cart>,order:Order){
        val writeBatch=mFirestore.batch()


        //adding the products into the sold products table
        for(cartItem in cartList){

            val soldProduct=SoldProduct(
                user_id=cartItem.seller_id,
                title = cartItem.title,
                sold_quantity = cartItem.checkout_quantity,
                image = cartItem.image,
                order_date = order.orderDate,
                total_amount = order.total_amount,
                address = order.address,
                order_id = order.title
            )
            val docRef=mFirestore.collection("sold_products")
                .document()

            writeBatch.set(docRef,soldProduct, SetOptions.merge())
        }

        //updating the current available product quantity
        for (cartItem in cartList){
            val productItemHashMap=HashMap<String,Any>()

            productItemHashMap["quantity"]=(cartItem.quantity.toInt()-cartItem.checkout_quantity.toInt()).toString()
            val docRef=mFirestore.collection("products")
                .document(cartItem.product_id)

            writeBatch.update(docRef,productItemHashMap)
        }

        //deleting the item from cart
        for(cartItem in cartList){
            val docRef=mFirestore.collection("cart_items")
                .document(cartItem.cart_id)
            writeBatch.delete(docRef)
        }

        writeBatch.commit()
            .addOnSuccessListener {
                activity.allDetailsUpdateDone()
            }
            .addOnFailureListener {e->
                Log.e("firestore","Error while updating products and cart after placing a order",e)
            }

    }

    fun getOrdersList(fragment: OrdersFragment){
        mFirestore.collection("orders")
            .whereEqualTo("user_id",getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                val ordersList=ArrayList<Order>()
//                Log.e("firestore",document.documents.toString())
                for(i in document.documents){
                    val orderItem=i.toObject(Order::class.java)
                    orderItem!!.order_id=i.id

                    ordersList.add(orderItem)
                }
                fragment.getOrdersListFromFirestoreDone(ordersList)
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while getting orders",e)
            }
    }
    fun getSoldProductList(fragment:SoldProductsFragment){
        mFirestore.collection("sold_products")
            .whereEqualTo("user_id",getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                val soldProductsList=ArrayList<SoldProduct>()
                for(i in document.documents){
                    val soldProduct=i.toObject(SoldProduct::class.java)
                    soldProduct!!.sold_product_id=i.id

                    soldProductsList.add(soldProduct)
                }
                fragment.getSoldProductDone(soldProductsList)
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while getting the sold products",e)
            }
    }

    //getting product details to edit my product
    fun getMyProductDetails(activity: AddProductActivity,productID: String){
        mFirestore.collection("products")
            .document(productID)
            .get()
            .addOnSuccessListener { document->
//                Log.e("firestore",document.toString())
                val product=document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsDone(product)
                }
            }
            .addOnFailureListener { e->
                Log.e("firestore","Error while getting my product details for editing a product",e)
            }
    }

    // to update product
    fun updateProduct(activity: AddProductActivity,productHashMap:HashMap<String,Any>,productID:String){
        mFirestore.collection("products")
            .document(productID)
            .update(productHashMap)
            .addOnSuccessListener {
                Log.e("firestore","product updated successfully!")
                activity.productUpdateDone()
            }
            .addOnFailureListener { e->
                activity.stopProgressIndicator()
                Log.e("firestore","Error while updaing the product details",e)
            }
    }
}