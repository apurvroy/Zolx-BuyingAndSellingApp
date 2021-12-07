package com.example.zolx.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zolx.databinding.FragmentOrdersBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.Order
import com.example.zolx.ui.adapters.MyOrdersAdapter

class OrdersFragment : Fragment() {
    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAdapter:MyOrdersAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    fun getOrdersList(){
        FirestoreClass().getOrdersList(this)
    }

    fun getOrdersListFromFirestoreDone(ordersList:ArrayList<Order>){
        
        if(ordersList.size>0){
            binding.rvOrderItems.visibility=View.VISIBLE
            binding.noOrderFound.visibility=View.GONE

            val recyclerView=binding.rvOrderItems
            recyclerView.layoutManager= LinearLayoutManager(activity)
            mAdapter= MyOrdersAdapter(requireActivity(),ordersList)
            recyclerView.adapter=mAdapter

        }else{
            binding.rvOrderItems.visibility=View.GONE
            binding.noOrderFound.visibility=View.VISIBLE
        }
    }

    override fun onResume() {
        getOrdersList()
        super.onResume()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}