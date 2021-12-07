package com.example.zolx.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zolx.R
import com.example.zolx.models.Order

class MyOrdersAdapter(private val context:Context,private var items:ArrayList<Order>): RecyclerView.Adapter<OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.item_order,parent,false)
        val viewHolder=OrderViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentItem=items[position]
        var title=""
        var count=1
        for(i in currentItem.items){
            title+="${count}.${i.title}[${i.checkout_quantity} item(s)]: ₹${i.price}(each)\n\n"
            count+=1
        }

        holder.title.text=title
        holder.total_amount.text="\u20B9${currentItem.total_amount}"
        holder.date.text=currentItem.orderDate
        holder.address.text=currentItem.address

    }

    override fun getItemCount(): Int {
        return items.size
    }
}


class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val title=itemView.findViewById<TextView>(R.id.order_items)
    val date=itemView.findViewById<TextView>(R.id.order_date)
    val address=itemView.findViewById<TextView>(R.id.order_address)
    val total_amount=itemView.findViewById<TextView>(R.id.order_total_amount)
}