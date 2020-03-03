package com.h.downloaddemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.h.downloaddemo.mvp.ProductPresenter
import com.h.downloaddemo.mvp.ProductViewPresenter
import ds.vuongquocthanh.socialnetwork.mvp.model.product.Product
import ds.vuongquocthanh.socialnetwork.mvp.model.product.ProductsResponse
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(),ProductViewPresenter ,AdapterProduct.ItemOnClick{
    private lateinit var presenter : ProductPresenter
    private lateinit var adapter : AdapterProduct
    private val products = ArrayList<Product>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        presenter = ProductPresenter()
        presenter.attachView(this)
        adapter = AdapterProduct(this,products,this)
        rvProduct.adapter= adapter
        rvProduct.layoutManager = GridLayoutManager(this,2)
        presenter.getAllProduct(1, 20, "Bearer CMfqypJoyUxqo6qkF0vI")

    }

    override fun getAllProduct(response: ProductsResponse) {
        products.clear()
        products.addAll(response.data)
        adapter.notifyDataSetChanged()
    }

    override fun showError(error: String) {
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
    }

    override fun imageOnClick(position: Int) {
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("image",products[position].image)
        intent.putExtra("slug",products[position].slug)
        startActivity(intent)
    }
}
