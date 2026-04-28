package sa.gov.ksaa.dal.data.webservices.newDal.responses

import java.util.Date

data class BiddingInvitation(
    var aboutClient: String?,
    var aboutProject: Any?,
    var amount: String?,
    var clientName: String?,
    var country: String?,
    var createdDate: Date?,
    var deliveryTime: String?,
    var durationOfProject: String?,
    var favourite: Boolean?,
    var freelanceRating: String?,
    var freelancerLevel: String?,
    var id: Int?,
    var inUnderway: Boolean?,
    var listOfRating: List<OfRating>?,
    var listProject: List<ProjectX>?,
    var proejctCategory: List<String>?,
    var projectDescription: String?,
    var projectDone: Int?,
    var projectTitle: String?,
    var projectsUnderway: Int?,
    var rating: Double?,
    var ratingOfFreelancer: String?,
    var request: String?,
    var reviewCount: Int?,
    var status: String?,
    var totalProject: Int?,
    var userId: Int?,
    val listOfCategory: List<Skill>? = null,
    var gender: String?,
    var image: String?,


//
    val clientLastName: String?,
    val favouriteBy: Any?,
    val listOfCompletedProject: Any?,
    val projectStatus: String?,
    val username: Any?,
){
    fun toProject(): NewProject  {
        return NewProject(aboutClient=aboutClient, amount=amount, clientName="$clientName $clientLastName", country=country,
            createdDate=createdDate, deliveryTime=deliveryTime, durationOfProject=durationOfProject,
            favourite=favourite, freelanceRating=freelanceRating, freelancerLevel=freelancerLevel,
            id=id, inUnderway=inUnderway, listOfRating=listOfRating, listProject=listProject,
            proejctCategory=proejctCategory, projectDescription=projectDescription, projectDone=projectDone,
            projectTitle=projectTitle, projectsUnderway= projectsUnderway, rating=rating,
            ratingOfFreelancer=ratingOfFreelancer, request=request, reviewCount=reviewCount,
            status=status, totalProject=totalProject, userId=userId, gender=gender, imageUrl = image, projectStatus = status?: projectStatus)
    }
}