package com.h.downloaddemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ds.vuongquocthanh.socialnetwork.mvp.model.product.Product
import kotlinx.android.synthetic.main.item_product.view.*

class AdapterProduct (var context : Context,var products : ArrayList<Product>,var listener : ItemOnClick) : RecyclerView.Adapter<AdapterProduct.ViewHolder>() {
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    interface ItemOnClick{
        fun imageOnClick(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(products[position].image).into(holder.itemView.ivProduct)
        holder.itemView.ivProduct.setOnClickListener {
            listener.imageOnClick(position)
        }
    }
}