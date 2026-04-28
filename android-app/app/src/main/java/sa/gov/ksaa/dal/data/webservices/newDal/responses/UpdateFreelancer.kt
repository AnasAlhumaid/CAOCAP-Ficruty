package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class UpdateFreelancer(
    val about: String?,
    val country: String?,
    val createdDate: String?,
    val dateOfBirth: String?,
    val email: String?,
    val enrollmentYear: String?,
    val experince: String?,
    val firstName: String?,
    val freelancerId: Int?,
    val freelancerLevel: String?,
    val gender: String?,
    val graduationYear: String?,
    val imageUrl: String?,
    val lastName: String?,
    val nameOfUnivesity: String?,
    val nationalId: String?,
    val phone: String?,
    val typeOfCertificate: String?,
    val typeOfService: String?,
    val userId: Int?,
    val userType: String?,
    val username: String?,
    val yearOfExperience: Int?
): MessageResponse()