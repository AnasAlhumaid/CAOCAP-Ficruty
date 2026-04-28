package sa.gov.ksaa.dal.data.webservices.newDal.responses

import java.util.Date

data class ProjectUnderway(
    val amount: String?,
    val clientName: String?,
    val durationOfProject: String?,
    val freelanceId: Int?,
    val freelancerBidAmount: String?,
    val freelancerName: String?,
    val id: Int?,
    val proejctCategory: Any?,
    val projectDescription: String?,
    val projectId: Int?,
    val projectStatus: String?,
    val projectTitle: String?,
    val rating: Double?,
    val reviewCount: Int?,
    val startDate: Date?,
    val typeOfCertificate: String?,
    val typeOfServices: Any?,
    val clientUserId: Int?, // freelancerUserId
    val listOfCategory: List<Skill>? = null,
    val listOfServices: List<Skill>? = null,
    val freelancerUserId : Int?,
    val userType: String?,

    val gender: String?,
    val image: String?,

    val clientLastName: String?,
    val freelancerLastName: String?,
    val freelancerLevel: String?,
) {
    var isClosed = false
    var isReviewed = false
}