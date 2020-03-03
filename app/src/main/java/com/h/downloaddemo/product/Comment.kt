package ds.vuongquocthanh.socialnetwork.mvp.model.product

data class Comment(
    val comment: String,
    val created_at: String,
    val id: Int,
    val item_id: String,
    val member: Member,
    val member_id: String,
    val rate: String,
    val reply_id: Any,
    val reply_top_id: Any,
    val sub_comments: List<Any>,
    val type: String,
    val updated_at: String
)