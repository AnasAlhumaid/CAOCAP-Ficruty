package sa.gov.ksaa.dal.data.webservices.newDal.responses

import android.media.Image

data class ReplyTo_aComment(
    val commentId: Int?,
    val gender : String?,
    val id: Int?,
    val image : String?,
    val replyComment: String?,
    val rCommentDate: String? = null,
    val replyFrom : String?,
    val userId: Int?,
    val projectid: Int?
)