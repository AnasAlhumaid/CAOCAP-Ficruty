package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class UpdateClient(
    val about: String?,
    val country: String?,
    val createdDate: String?,
    val dateOfBirth: String?,
    val email: String?,
    val firstName: String?,
    val freelancerId: Int?,
    val gender: String?,
    val imageUrl: String?,
    val lastName: String?,
    val listOfRating: Any?,
    val nationalId: String?,
    val password: Any?,
    val phone: String?,
    val userId: Int?,
    val userType: String?,
    val username: String?
): MessageResponse()