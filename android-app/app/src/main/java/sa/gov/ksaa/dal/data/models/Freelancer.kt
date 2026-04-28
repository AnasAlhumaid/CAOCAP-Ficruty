package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser

data class Freelancer(
    var about: String? = null,
    var created_date: String? = null,
    var experience: String? = null,
    var files: List<Any?>? = null,
    var freelance_id: Int? = null,
    var freelancer_level: String? = null,
    var is_deleted: Boolean? = null,
    var number_of_project: String? = null,
    var rating: Float? = null,
    var skills: String? = null,
    var type_of_service: String? = null,
    var updated_date: String? = null,
    var user: NewUser? = null,
    var user_id: Int? = null,
    var username: String? = null,
    var verified: Boolean? = null,
    var certifications: List<Certification>? = null,
    var isFavorite: Boolean? = null
){
    fun getFullName(): String? {
//        user?.let {
//            return "${user?.first_name} ${user?.last_name}"
//        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        return (other is Freelancer && other.freelance_id == freelance_id)
    }
    var isSelected: Boolean = false
}