package ds.vuongquocthanh.socialnetwork.api

import ds.vuongquocthanh.socialnetwork.mvp.model.product.ProductsResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface ApiService {
    //Product
    @GET("api/product")
    fun getAllProduct(
        @Query("page") page: Int, @Query("limit") limit: Int, @Header("Authorization") Authorization: String
    )
            : Observable<ProductsResponse>


}