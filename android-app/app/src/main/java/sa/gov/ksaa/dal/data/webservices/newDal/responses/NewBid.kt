package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class NewBid(
    val attachment: Any?,
    val attachmentDesc: String?,
    val freelancerBiddingAmount: String?,
    val freelancerExpectedTime: String?,
    val freelancerId: Int?,
    val freelancerName: String?,
    val freelancerOutputExpected: String?,
    val freelancerProjectDesc: String?,
    val id: Int?,
    val projectDesc: Any?,
    val projectId: Int?,
    val projectTitle: String?,
    val rating: Double?,
    val typeOfCertificate: String?,
    val typeOfService: String?,
    val userId: Int?,
    val freelancerUserId: Int?,


    val attachmentDescription: String?,
    val description: String?,
    val expectedDuration: String?,
    val outputExpected: String?,
    val projectStatus: Any?,



    val bidId: Int?,
    val freelancerLevel: String?,


    val gender: String?,
    val image: String?,


    val attachmentUrl: String?,
    val frelancerLastName: String?,

    val listOfCategory: List<Skill>? = null,
    val listOfServices: List<Skill>? = null,
    val aboutProject: String?,
    val attDesc: String?,
    val biddingId: Int?,
    val clientFirstName: String?,
    val clientLastName: String?,
    val createdDate: String?,
    val expectedTime: String?,
    val freelancerLastName: String?,
    val price: String?,
    val proejctCategory: String?,


    val clientImageUrl: String?,
    val fileDescription: String?,
    val fileUrl: String?,
)