package sa.gov.ksaa.dal.data.webservices.newDal.responses

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("User ")
    val User : String? = null
): MessageResponse()