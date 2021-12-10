package com.example.zolx.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zolx.R
import com.example.zolx.databinding.FragmentProductsBinding
import com.example.zolx.firestore.FirestoreClass
import com.example.zolx.models.Product
import com.example.zolx.ui.activities.AddProductActivity
import com.example.zolx.ui.activities.ProductDetailsActivity
import com.example.zolx.ui.adapters.IMyProductsAdapter
import com.example.zolx.ui.adapters.MyProductsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ProductsFragment : Fragment(), IMyProductsAdapter {

    private var _binding: FragmentProductsBinding? = null

    private lateinit var mAdapter:MyProductsAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
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

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        getProductListFromFirestore()
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu,menu)


        //Searching
        val item=menu.findItem(R.id.search_my_products)
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
//                        Snackbar.make(binding.noProductsFound,"No items Found!",Snackbar.LENGTH_SHORT).show()
//                        Toast.makeText(activity,"No items Found!",Toast.LENGTH_SHORT).show()
                        binding.noProductsFound.text="No items Found \"$searchText\""
                        binding.noProductsFound.visibility=View.VISIBLE
                    }else{
                        binding.noProductsFound.visibility=View.GONE
                    }

                    binding.rvMyProductItems.adapter?.notifyDataSetChanged()
                }
                else{
                    binding.noProductsFound.visibility=View.GONE
                    mTempProductList.clear()
                    mTempProductList.addAll(mProductList)
                    binding.rvMyProductItems.adapter?.notifyDataSetChanged()

                }
                return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        when(id){
            R.id.add_product->{
                val intent= Intent(activity, AddProductActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }




    fun getProductListFromFirestore(){
        FirestoreClass().getProductDetails(this)
    }

    fun productsListDone(productsList:ArrayList<Product>){
        mProductList.clear()
        mTempProductList.clear()
        mProductList.addAll(productsList)
        mTempProductList.addAll(productsList)
        if(mProductList.size>0){
            binding.rvMyProductItems.visibility=View.VISIBLE
            binding.noProductsFound.visibility=View.GONE

            buildRecyclerView()


        }else{
            binding.rvMyProductItems.visibility=View.GONE
            binding.noProductsFound.visibility=View.VISIBLE
        }

    }

    private fun buildRecyclerView(){
        val recyclerView=binding.rvMyProductItems
        recyclerView.layoutManager= LinearLayoutManager(activity)
        mAdapter=MyProductsAdapter(requireActivity(),mTempProductList,this)
        recyclerView.adapter=mAdapter
    }

    override fun deleteProduct(productID: String) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setCancelable(false)
                .setTitle("Delete?")
                .setMessage("Are you sure you want to Delete this product?")
                .setNeutralButton("Cancel") { dialog, which ->
                    // Respond to neutral button press
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { dialog, which ->
                    // Respond to positive button press
                    FirestoreClass().deleteProduct(this,productID)
                }
                .show()
        }
//        val builder = AlertDialog.Builder(requireActivity())
//        builder.setMessage("Are you sure you want to Delete?")
//            .setCancelable(false)
//            .setPositiveButton("Yes") { dialog, id ->
//                //call to delete the product
//                FirestoreClass().deleteProduct(this,productID)
//            }
//            .setNegativeButton("No") { dialog, id ->
//                // Dismiss the dialog
//                dialog.dismiss()
//            }
//        val alert = builder.create()
//        alert.show()
//        Toast.makeText(requireActivity(),"$productID",Toast.LENGTH_SHORT).show()
    }


    fun productDeleteDone(){
        Snackbar.make(binding.noProductsFound,"Product Deleted!",Snackbar.LENGTH_LONG).show()
//        Toast.makeText(requireActivity(),"Product Deleted!",Toast.LENGTH_SHORT).show()
        getProductListFromFirestore()
    }


    override fun editProduct(productID: String) {
        val intent=Intent(context, AddProductActivity::class.java)
        intent.putExtra("product_id",productID)
        startActivity(intent)
    }

    override fun onClick(productID: String,ownerID:String) {
        val intent=Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra("product_id",productID)
        intent.putExtra("owner_id",ownerID)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFirestore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}