package sa.gov.ksaa.dal.data.webservices.newDal

data class ChatMessage(
    val createDate: Long?,
    val id: Int?,
    val message: String?,
    val projectId: Int?,
    val sendById: Int?,
    val sendToId: Int?
)