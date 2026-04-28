package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class ObjectionAndComplaintRes(
    val category: String?,
    val createdDate: Any?,
    val description: String?,
    val name: Any?,
    val objectionFrom: Int?,
    val objectionTo: Int?,
    val referenceCode: String?,
    val replyContent: Any?,
    val replyFrom: Any?,
    val ticketNumber: Int?
)