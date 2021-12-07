package com.example.zolx.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zolx.databinding.FragmentSoldProductsBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.SoldProduct
import com.example.zolx.ui.adapters.SoldProductsAdapter

class SoldProductsFragment : Fragment() {
    private lateinit var binding:FragmentSoldProductsBinding
    private lateinit var mAdapter:SoldProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSoldProductsBinding.inflate(inflater,container,false)
//        return inflater.inflate(R.layout.fragment_sold_products, container, false)
        return binding.root
    }

    private fun getSoldProductListFromFirestore(){
        FirestoreClass().getSoldProductList(this)
    }

    fun getSoldProductDone(soldProductsList:ArrayList<SoldProduct>){

//        Log.e("sold","${soldProductsList.size}")
        if(soldProductsList.size>0){
            binding.rvSoldProducts.visibility=View.VISIBLE
            binding.noProductSold.visibility=View.GONE


            val recyclerView=binding.rvSoldProducts
            recyclerView.layoutManager=LinearLayoutManager(activity)
            mAdapter= SoldProductsAdapter(requireActivity(),soldProductsList)
            recyclerView.adapter=mAdapter

        }else{
            binding.rvSoldProducts.visibility=View.GONE
            binding.noProductSold.visibility=View.VISIBLE
        }

    }

    override fun onResume() {
        getSoldProductListFromFirestore()
        super.onResume()
    }


}