package ds.vuongquocthanh.socialnetwork.mvp.model.product

data class Product(
    val avg_rate: String,
    val cat_id: String,
    val comment_count: String,
    val comments: List<Comment>?,
    val content: String,
    val created_at: String,
    val entity_id: String,
    val entity_type: String,
    val id: Int,
    val image: String,
    val images: List<String>?,
    val like_count: String,
    val meta_desc: String,
    val meta_key: String,
    val old_price: String,
    val percent: String,
    val price: String,
    val quantity: String,
    val rate_comment_count: Int,
    val rate_count: String,
    val reduce_percent: String,
    val shop_id: String,
    val short_desc: String,
    val slug: String,
    val sold: String,
    val status: String,
    val tags: Any?,
    val title: String,
    val trademark: String,
    val updated_at: String,
    val viewed: String
)