package sa.gov.ksaa.dal.data.models

import com.google.gson.annotations.SerializedName
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date


data class ClientFavoriteFreelancer(
    var id: Int? = null,
    var user_id: Int? = null,
    var freelance_id: Int? = null,
    var created_date: Date? = null,
    var updated_date:  Date? = null,
    @SerializedName("Freelance")
    var freelancer:  Freelancer? = null,
    @SerializedName("User")
    var user: NewUser? = null,
)