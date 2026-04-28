package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class ObjectAndComplaint(
    val category: String?,
    val createdDate: Any?,
    val description: String?,
    val name: String?,
    val objectionFrom: Int?,
    val objectionTo: Int?,
    val referenceCode: String?,
    val replyContent: Any?,
    val replyFrom: Any?,
    val ticketNumber: Int?
)