package sa.gov.ksaa.dal.data.webservices.newDal.responses

import android.util.Log
import androidx.lifecycle.MutableLiveData
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import java.util.Date

data class NewUser(
    var about: String? = null,
    var country: String? = null,
    var createdDate: Date? = null,
    var dateOfBirth: String? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var nationalId: String? = null,
    var password: String? = null,
    var phone: String? = null,
    var userId: Int? = null,
    var userType: String?= null,
//    var username: String?= null,
    var gender: String?= null,
    var freelancerLevel: String? = null,


    var freelancerId: Int?= null,
    var listOfRating: List<RatingAndReview>?= null,

    var imageUrl: String? = null,
    var image: String? = null,

): MessageResponse(){


    companion object {
        const val TECH_SUPPORT_USER_TYPE = "Technical_Support"
        val TECH_SUPPORT_USER = NewUser(userType=TECH_SUPPORT_USER_TYPE)
        const val FREELANCER_USER_TYPE = "Freelancer"
        const val INDIVIDUAL_CLIENT_USER_TYPE = "Client_individual"
        const val ORG_CLIENT_USER_TYPE = "Client_Org"
        const val CLIENT_USER_TYPE = "Client"
        const val MALE_USER_GENDER = "ذكر"
        const val FEMALE_USER_GENDER = "انثى"
        const val FEMALE__USER_GENDER = "أنثى"
    }

    fun getFullName(): String {
        return "${firstName?: ""} ${lastName?: ""}"
    }

    fun isClient(): Boolean {
        if (userType == null)
            return false
        if (userType.equals(INDIVIDUAL_CLIENT_USER_TYPE, true))
            return true
        if (userType.equals(ORG_CLIENT_USER_TYPE, true))
            return true
        if (userType.equals(CLIENT_USER_TYPE, true))
            return true
        return false
    }

    fun isFreelancer(): Boolean {
        val isFreelancer = userType != null && (userType.equals(FREELANCER_USER_TYPE, true))
        Log.w(javaClass.simpleName, "isFreelancer: isFreelancer = $isFreelancer")
        return isFreelancer
    }

    override fun equals(other: Any?): Boolean {
        return (other is NewUser && other.userId == userId)
    }
}