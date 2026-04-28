package sa.gov.ksaa.dal.data.webservices.newDal.responses

import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import java.util.Date

data class NewProject(
    var aboutClient: String? = null,
    var aboutProject: String? = null,
    var amount: String? = null,
    var clientName: String? = null,
    var country: String? = null,
    var createdDate: Date? = null,
    var deliveryTime: String? = null,
    var durationOfProject: String? = null,
    var favourite: Boolean? = null,
    var id: Int? = null,
    var inUnderway: Boolean? = null,
    var listOfRating: List<OfRating>? = null,
    var listProject: List<ProjectX>? = null,
    var proejctCategory: List<String>? = null,
    var listOfCategory: List<Skill>? = null,
    var projectDescription: String? = null,
    var projectDone: Int? = null,
    var projectTitle: String? = null,
    var projectsUnderway: Int? = null,
    var rating: Double? = null,
    var ratingOfFreelancer: String? = null,
    var reviewCount: Int? = null,
    var totalProject: Int? = null,
    var userId: Int? = null,
    var freelanceRating: String? = null,
    var freelancerLevel: String? = null,
    var request: String? = null,
    var status: String? = null,
    var freelancerId: Int? = null,
    var projectAbout: String? = null,




    var message: Any? = null,
    var numberOfOffers: Int? = null,
    var projectValue: String? = null,
    var expectedTime: String? = null,
    var durationOfOffer: String? = null,
    var projectId: Int? = null,

    var gender: String? = null,
    var imageUrl: Any? = null,
    var image: Any? = null,

    val projectStatus: String? = null,


    val clientLastName: String? = null,
    val favouriteBy: Any? = null,
    val listOfCompletedProject: List<Any>? = null,
    val username: Any? = null,




    val numberOfBidding: Int? = null,
    val numberOfOffer: String? = null,
) {
    companion object {
        const val GENERAL_REQUEST_TYPE = "general"
        const val PRIVATE_REQUEST_TYPE = "private"
        const val STATUS_ACCEPTED = "status"
    }
    var addFile: MyFile? = null
    val clientFullName: String?
        get() {
            return if (clientName == null && clientLastName == null) null else "${clientName?: ""} ${clientLastName?: ""}"
        }

    fun canSubmitBid(): Boolean {
        return inUnderway == false && !listOfCompletedProject.isNullOrEmpty()
                && status.equals("Accepted")
    }
}