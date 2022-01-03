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

open class MyProductsAdapter(private val context: Context,private var items:ArrayList<Product>,private val listener:IMyProductsAdapter ):RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.item_products,parent,false)
        val viewHolder=ProductViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct=items[position]

        holder.name.text=currentProduct.title
        holder.price.text="\u20B9${currentProduct.price}"
        holder.quantity.text="#${currentProduct.quantity}"
        Glide.with(context).load(currentProduct.image).into(holder.image)

        holder.deleteBtn.setOnClickListener {
            listener.deleteProduct(currentProduct.product_id)
        }
        holder.editBtn.setOnClickListener {
            listener.editProduct(currentProduct.product_id)
        }
        holder.itemView.setOnClickListener{
            listener.onClick(currentProduct.product_id,currentProduct.user_id)
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }


}

class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val image=itemView.findViewById<ImageView>(R.id.product_item_image)
    val name=itemView.findViewById<TextView>(R.id.item_name)
    val price=itemView.findViewById<TextView>(R.id.item_price)
    val quantity=itemView.findViewById<TextView>(R.id.item_available_quantity)
    val deleteBtn=itemView.findViewById<ImageView>(R.id.btn_delete_product)
    val editBtn=itemView.findViewById<ImageView>(R.id.btn_edit_product)
}

interface IMyProductsAdapter{
    fun deleteProduct(productID:String)
    fun editProduct(productID:String)
    fun onClick(productID:String,ownerID:String)
}
