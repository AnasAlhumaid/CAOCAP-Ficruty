package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class NewCommentOnProject(
    var comment: String? = null,
    val commentDate: String? = null,
    val freelancerName: String? = null,
    val id: Int? = null,
    val projectId: Int? = null,
    val replyComment: List<ReplyTo_aComment>? = null,



    val commentFrom: String? = null,
    val gender: String? = null,
    val image: Any? = null,
    val userId: Int? = null
)