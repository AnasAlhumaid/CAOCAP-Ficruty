package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class CommentOnProject(
    val comment: String?,
    val commentDate: String?,
    val freelancerName: String?,
    val id: Int?,
    val projectId: Int?,
    val replyComment: List<ReplyComment?>?
)