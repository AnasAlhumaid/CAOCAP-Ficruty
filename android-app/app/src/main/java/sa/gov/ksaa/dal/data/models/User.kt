package sa.gov.ksaa.dal.data.models

import java.util.Date

//data class User(
//    var createdAt: Any? = null,
//    var created_date: Date? = null,
//    var email: String? = null,
//    var first_name: String? = null,
//    var user_id:Int? = null,
//    var is_deleted: Boolean? = null,
//    var last_name: String? = null,
//    var phone_no: String? = null,
//    var role_id: Int? = null,
//    var updatedAt: Any? = null,
//    var updated_date: Date? = null,
//    var user_type: String? = null,
//    var password: String? = null,
//    var freelancer: Freelancer? = null,
//    var pwd_reset_code: Int? = null,
//    var username: String? = null,
//    var about: String? = null,
//    var reviews_he_gets: List<Review>? = null,
//    var projects: List<Project>? = null,
//){
//    companion object {
//        const val FREELANCER_USER_TYPE = "Freelancer"
//        const val INDIVIDUAL_CLIENT_USER_TYPE = "Client_individual"
//        const val ADMIN_USER_TYPE = "Admin"
//
//        const val ADMIN_ROLE = 1
//        const val CLIENT_ROLE = 2
//        const val FREELANCER_ROLE = 3
//
//        val TECH_SUPPORT_USER = User(first_name="الدعم", last_name = "الفني", role_id= ADMIN_ROLE, user_type= ADMIN_USER_TYPE)
//    }
//    fun getFullName(): String? {
//        if (first_name == null || last_name == null)
//            return null
//        return "$first_name $last_name"
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return (this === other || (other is User && other.user_id == user_id))
//    }
//
//    fun isClient(): Boolean{
//            return user_type == INDIVIDUAL_CLIENT_USER_TYPE || role_id == CLIENT_ROLE
//    }
//
//    fun isFreelancer(): Boolean{
//        return user_type == FREELANCER_USER_TYPE || role_id == FREELANCER_ROLE
//    }
//
//    fun getUserType(): String?{
//        if (user_type == FREELANCER_USER_TYPE || role_id == FREELANCER_ROLE){
//            return FREELANCER_USER_TYPE
//        } else if (user_type == INDIVIDUAL_CLIENT_USER_TYPE || role_id == CLIENT_ROLE){
//            return INDIVIDUAL_CLIENT_USER_TYPE
//        }
//        else if (user_type == ADMIN_USER_TYPE || role_id == ADMIN_ROLE){
//            return ADMIN_USER_TYPE
//        }
//        return null
//    }
//
//    fun toNewUser(): NewUser{
////        var date_of_birth: Date? = null,
////        var national_id: Int? = null,
////    var about: String? = null,
////    var modificationReason: String? = null,
////        var country: String? = null,
//
//        return NewUser(user_id = user_id, first_name = first_name, last_name = last_name, email= email,
//            password=password, phone_no = phone_no, user_type = getUserType(), username = username,
//            is_deleted = is_deleted, role = role_id, created_date = created_date, updated_date = updated_date,
//            isUsername = false)
//    }
//}