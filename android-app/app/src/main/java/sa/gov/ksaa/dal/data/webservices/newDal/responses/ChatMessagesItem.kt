package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class ChatMessagesItem(
    val createDate: String?,
    val id: Int?,
    val message: String?,
    val nameId: Int?,
    val projectId: Int?,
    val projectName: String?,
    val sendById: Int?,
    val sendToId: Int?
)