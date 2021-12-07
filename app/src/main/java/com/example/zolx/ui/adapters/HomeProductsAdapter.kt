package com.example.zolx.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zolx.R
import com.example.zolx.models.Product

class HomeProductsAdapter(private val context:Context,private val items:ArrayList<Product>,private val listener:IHomeProductsAdapter): RecyclerView.Adapter<HomeProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeProductViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_home_products,parent,false)
        val viewHolder=HomeProductViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: HomeProductViewHolder, position: Int) {
        val currentProduct=items[position]
        holder.name.text=currentProduct.title
        holder.price.text="\u20B9${currentProduct.price}"
        Glide.with(context).load(currentProduct.image).into(holder.image)
        holder.itemView.setOnClickListener{
            listener.onCLick(currentProduct.product_id,currentProduct.user_id)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class HomeProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val image=itemView.findViewById<ImageView>(R.id.item_product_image)
    val name=itemView.findViewById<TextView>(R.id.item_product_title)
    val price=itemView.findViewById<TextView>(R.id.item_product_price)
}

interface IHomeProductsAdapter{
    fun onCLick(productID:String,ownerID:String)
}