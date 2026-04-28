package sa.gov.ksaa.dal.data.webservices.newDal.responses


data class ClientItem(
    val clientFirstName: String?,
    val clientLastName: String?,
    val email: String?,
    val phone: String?,
    val userId: Int?,
    val userType: String?,
    val username: String?,
    val about : String?,
    val imageUrl : String?
) {
    fun toUser(): NewUser{
        return NewUser()
    }

    fun getFullName(): String{
        return "$clientFirstName $clientLastName"
    }
}