package ds.vuongquocthanh.socialnetwork.mvp.model.product

data class ProductsResponse(
    val current_page: Int,
    val `data`: List<Product>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val success: Int,
    val to: Int,
    val total: Int
)