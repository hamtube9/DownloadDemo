package com.h.downloaddemo.mvp

import android.util.Log
import ds.vuongquocthanh.socialnetwork.api.ApiUtil
import ds.vuongquocthanh.socialnetwork.mvp.Presenter
import ds.vuongquocthanh.socialnetwork.mvp.View
import ds.vuongquocthanh.socialnetwork.mvp.model.product.ProductsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProductPresenter :Presenter{
    private lateinit var presenterView : ProductViewPresenter
    private val composite = CompositeDisposable()
    override fun attachView(view: View) {
        presenterView = view as ProductViewPresenter
    }

    override fun dispose() {
        composite.dispose()
    }

     fun getAllProduct( page :Int,limit : Int, authorization : String){
        composite.add(ApiUtil.getAPIService().getAllProduct(page,limit, authorization).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(this::onGetProductSuccess){t->onGetProductFail("Lấy dữ liệu không thành công",t)})
    }


    private fun onGetProductSuccess(response : ProductsResponse){
        if (response.success ==1){
            presenterView.getAllProduct(response)
            Log.d("onGetProductSuccess",response.data.size.toString())

        }
    }

    private fun onGetProductFail(error:String,t:Throwable){
        presenterView.showError(error)
        Log.d("onGetProductFail",t.localizedMessage)
    }

}