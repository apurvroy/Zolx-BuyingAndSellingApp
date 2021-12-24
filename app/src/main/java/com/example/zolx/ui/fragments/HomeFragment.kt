package com.example.zolx.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
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
    private lateinit var textCartItemCount:TextView
    private var mCartItemCount: Int=0


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
//                        Toast.makeText(activity,"No items Found!",Toast.LENGTH_SHORT).show()
                        binding.textHome.text="No items Found \"$searchText\""
                        binding.textHome.visibility=View.VISIBLE
                    }else{
                        binding.textHome.visibility=View.GONE
                    }

                    binding.rvHomeItems.adapter?.notifyDataSetChanged()
                }
                else{
                    binding.textHome.visibility=View.GONE
                    mTempProductList.clear()
                    mTempProductList.addAll(mProductList)
                    binding.rvHomeItems.adapter?.notifyDataSetChanged()

                }
                return false
            }

        })

        //badge on cart
        val cartItem=menu.findItem(R.id.cart)
        val actionView=cartItem.actionView
        textCartItemCount=actionView.findViewById(R.id.cart_badge)
        setupBadge()

        actionView.setOnClickListener {
            onOptionsItemSelected(cartItem)
        }


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
        mProductList.clear()
        mTempProductList.clear()
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

    fun getCartItemsCountDone(cartItemsCount:Int){
        Log.d("cart",cartItemsCount.toString())
        mCartItemCount=cartItemsCount
        setupBadge()

    }
    private fun setupBadge(){
        if(textCartItemCount!=null){
            if(mCartItemCount==0){
                textCartItemCount.visibility=View.GONE
            }else{
                textCartItemCount.text=mCartItemCount.toString()
                textCartItemCount.visibility=View.VISIBLE
            }
        }
    }

    override fun onResume() {
//        getProductListFromFirestore()
        FirestoreClass().getCartItemsCount(this)
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