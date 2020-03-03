package com.h.downloaddemo.mvp

import ds.vuongquocthanh.socialnetwork.mvp.View
import ds.vuongquocthanh.socialnetwork.mvp.model.product.ProductsResponse

interface ProductViewPresenter : View{
    fun getAllProduct(response : ProductsResponse)
}