package com.example.zolx.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zolx.R
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.Cart
import com.google.android.material.snackbar.Snackbar

data class CartItemsAdapter(private val context: Context, private val items:ArrayList<Cart>):RecyclerView.Adapter<CartItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false)
        val viewHolder=CartItemViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val currentItem=items[position]
        Glide.with(context).load(currentItem.image).into(holder.image)
        holder.name.text=currentItem.title
        holder.price.text="\u20B9 ${currentItem.price}"
        holder.cartQuantity.text=currentItem.checkout_quantity

        if(holder.cartQuantity.text=="0"){
            holder.removeItem.visibility=View.GONE
            holder.addItem.visibility=View.GONE
            holder.cartQuantity.text="Out of stock"

        }else{
            holder.removeItem.visibility=View.VISIBLE
            holder.addItem.visibility=View.VISIBLE
        }
        holder.deleteItem.setOnClickListener {
            FirestoreClass().removeItemFromCart(context,currentItem.cart_id)
        }
        holder.addItem.setOnClickListener {
            val checkoutQuantity=currentItem.checkout_quantity.toInt()
            val itemHashMap=HashMap<String,Any>()
            if(checkoutQuantity<currentItem.quantity.toInt() && checkoutQuantity<currentItem.max_checkout_quantity.toInt()){
                itemHashMap["checkout_quantity"]=(checkoutQuantity+1).toString()
                FirestoreClass().updateMyCart(context,currentItem.cart_id,itemHashMap)
            }else if(checkoutQuantity==currentItem.max_checkout_quantity.toInt()){
                Snackbar.make(holder.itemView,"You can only buy up to ${currentItem.max_checkout_quantity} unit(s) of this product",Snackbar.LENGTH_LONG).show()
//                Toast.makeText(context,"You can only buy up to 5 unit(s) of this product",Toast.LENGTH_LONG).show()
            } else{
                Snackbar.make(holder.itemView,"Currently we only have $checkoutQuantity ${currentItem.title}s in the stocks",Snackbar.LENGTH_LONG).show()
//                Toast.makeText(context,"Currently we only have $checkoutQuantity ${currentItem.title}s in the stocks",Toast.LENGTH_LONG).show()
            }
        }
        holder.removeItem.setOnClickListener {

            if(currentItem.checkout_quantity=="1"){
                FirestoreClass().removeItemFromCart(context,currentItem.cart_id)
            }else{
                val checkoutQuantity=currentItem.checkout_quantity.toInt()
                val itemHashMap=HashMap<String,Any>()

                itemHashMap["checkout_quantity"]=(checkoutQuantity-1).toString()
                FirestoreClass().updateMyCart(context,currentItem.cart_id,itemHashMap)
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}


class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image=itemView.findViewById<ImageView>(R.id.cart_product_item_image)
    val cartQuantity=itemView.findViewById<TextView>(R.id.cart_item_quantity)
    val name=itemView.findViewById<TextView>(R.id.cart_item_name)
    val price=itemView.findViewById<TextView>(R.id.cart_item_price)
    val removeItem=itemView.findViewById<ImageButton>(R.id.cart_remove_item)
    val addItem=itemView.findViewById<ImageButton>(R.id.cart_add_item)
    val deleteItem=itemView.findViewById<ImageView>(R.id.btn_cart_delete_product)


}