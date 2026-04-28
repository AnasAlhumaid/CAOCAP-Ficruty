package sa.gov.ksaa.dal.data.webservices.newDal.responses

import java.util.Date

data class ClosedProject(
    val amount: String?,
    val clientName: String?,
    val durationOfProject: String?,
    val freelanceId: Int?,
    val freelancerBidAmount: String?,
    val freelancerName: String?,
    val id: Int?,
    val proejctCategory:
    List<String>?,
    val projectDescription: String?,
    val projectId: Int?,
    val projectStatus: String?,
    val projectTitle: String?,
    val rating: Double?,
    val reviewCount: Int?,
    val startDate: Date?,
    val typeOfCertificate: Any?,
    val typeOfServices: String?,
    val userId: Int?,
    val userType: String?,
    val freelancerLevel: String?,
    val clientId: Int?,
    val description: String?,
    val expectedDuration: String?,
    val freelancerId: Int?,
    val outputExpected: String?,
    val projectName: String?,

    val gender: String?,
    val image: String?,
    val listOfCategory: List<Skill>? = null,
    val listOfServices: List<Skill>? = null,

    val clientLastName: String?,
    val freelancerLastName: String?,

    ) {
    var isReviewed = false

    fun toProject(): NewProject {
        return NewProject(
            amount = amount ?: freelancerBidAmount,
            clientName = clientName,
            durationOfProject = durationOfProject,
            freelancerId = freelanceId ?: freelancerId,
            id = id,
            listOfCategory = listOfCategory,
            proejctCategory = proejctCategory,
            projectDescription = projectDescription,
            projectId = projectId,
            projectStatus = projectStatus,
            projectTitle = projectTitle,
            rating = rating,
            reviewCount = reviewCount,
            createdDate = startDate,
//        proejctCategory=typeOfServices,
            userId = userId,
//        type=userType,
            freelancerLevel = freelancerLevel,
//        id =clientId,
//        projectDescription=description,
//            durationOfProject=expectedDuration,
//        outputExpected=outputExpected,
//            projectTitle=projectName,
            gender = gender,
            image = image,
            clientLastName = clientLastName,
//            freelancerLastName=freelancerLastName
        )
    }
}