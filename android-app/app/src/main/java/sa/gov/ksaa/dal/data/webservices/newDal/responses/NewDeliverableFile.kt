package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class NewDeliverableFile(
    val createdDate: String?,
    val fileCategory: String?,
    val fileDescription: String?,
    val status:String?,
    val fileName: String?,
    val id: Int?,
    val imageUrl: String?,
    val uploadBy: String?,
    val uploadById: Int?
)