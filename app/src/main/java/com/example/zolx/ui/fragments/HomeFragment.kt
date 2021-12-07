package com.example.zolx.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zolx.R
import com.example.zolx.databinding.FragmentHomeBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.Product
import com.example.zolx.ui.activities.CartActivity
import com.example.zolx.ui.activities.ProductDetailsActivity
import com.example.zolx.ui.activities.SettingsActivity
import com.example.zolx.ui.adapters.HomeProductsAdapter
import com.example.zolx.ui.adapters.IHomeProductsAdapter

class HomeFragment : Fragment(), IHomeProductsAdapter {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mAdapter:HomeProductsAdapter
    private var mTempProductList=ArrayList<Product>()
    private var mProductList=ArrayList<Product>()


    override fun onCreate(savedInstanceState: Bundle?) {
        //to use options menu
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        getProductListFromFirestore()
        return root



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu,menu)


        //Searching
        val item=menu.findItem(R.id.search_home)
        val searchView=item.actionView as SearchView

        searchView.maxWidth= Int.MAX_VALUE

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                mTempProductList.clear()
                val searchText=newText!!.trim().lowercase()
//                Log.e("search",searchText)

                if(searchText.isNotEmpty()){

                    mProductList.forEach {
                        if(it.title.lowercase().contains(searchText)){
                            mTempProductList.add(it)
//                            Log.e("common",it.title.lowercase())
                        }
                    }
                    if(mTempProductList.isEmpty()){
                        Toast.makeText(activity,"No items Found!",Toast.LENGTH_SHORT).show()
                    }

                    binding.rvHomeItems.adapter?.notifyDataSetChanged()
                }
                else{
                    mTempProductList.clear()
                    mTempProductList.addAll(mProductList)
                    binding.rvHomeItems.adapter?.notifyDataSetChanged()

                }
                return false
            }

        })


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        when(id){
            R.id.settings->{
                val intent=Intent(activity,SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.cart->{
                val intent=Intent(activity,CartActivity::class.java)
                startActivity(intent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun getProductListFromFirestore(){
        FirestoreClass().getHomeItems(this)
    }

    fun productListDone(productsList:ArrayList<Product>){
        mProductList.addAll(productsList)
        mTempProductList.addAll(productsList)
        if(mProductList.size>0){
            binding.rvHomeItems.visibility=View.VISIBLE
            binding.textHome.visibility=View.GONE
            buildRecyclerView()

        }else{
            binding.rvHomeItems.visibility=View.GONE
            binding.textHome.visibility=View.VISIBLE
        }
    }

    private fun buildRecyclerView(){
//        Log.e("products",mTempProductList.toString())
        val recyclerView=binding.rvHomeItems
        recyclerView.layoutManager=GridLayoutManager(activity,2)
        mAdapter= HomeProductsAdapter(requireActivity(),mTempProductList,this)
        recyclerView.adapter=mAdapter
    }

    override fun onResume() {
//        getProductListFromFirestore()
        super.onResume()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCLick(productID: String,ownerID:String) {
        val intent=Intent(context,ProductDetailsActivity::class.java)
        intent.putExtra("product_id",productID)
        intent.putExtra("owner_id",ownerID)
        startActivity(intent)
    }
}