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
import com.example.zolx.models.SoldProduct

class SoldProductsAdapter(private val context:Context,private val items:ArrayList<SoldProduct>): RecyclerView.Adapter<SoldProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldProductViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_sold_products,parent,false)
        val viewHolder=SoldProductViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: SoldProductViewHolder, position: Int) {
        val currentItem=items[position]
        Glide.with(context)
            .load(currentItem.image)
            .placeholder(R.drawable.sold_product_item_image)
            .into(holder.image)
        holder.title.text=currentItem.title
        holder.address.text=currentItem.address
        holder.orderDate.text=currentItem.order_date
        holder.orderQuantity.text=currentItem.sold_quantity
        holder.amount.text=currentItem.total_amount

    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class SoldProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val title=itemView.findViewById<TextView>(R.id.sold_item_title)
    val image=itemView.findViewById<ImageView>(R.id.sold_item_image)
    val orderDate=itemView.findViewById<TextView>(R.id.sold_order_date)
    val address=itemView.findViewById<TextView>(R.id.sold_order_address)
    val amount=itemView.findViewById<TextView>(R.id.sold_order_total_amount)
    val orderQuantity=itemView.findViewById<TextView>(R.id.sold_order_quantity)

}