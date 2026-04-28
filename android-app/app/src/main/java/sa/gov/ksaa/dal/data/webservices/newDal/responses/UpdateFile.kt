package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class UpdateFile(
    val id: Int?,
    val createdDat: String?,
    val fileCategory: String?,
    val fileDescription:String?,
    val fileName:String?,
    val fileUrl:String?,
    val freelancerId:Int?


): MessageResponse()