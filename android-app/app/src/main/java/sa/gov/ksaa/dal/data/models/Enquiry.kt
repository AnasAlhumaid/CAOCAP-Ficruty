package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date

data class Enquiry(
    // userId=6&objectionTo=2&category=التصنيف&description=test&status
    var created_date: Date? = null,
    var email: String? = null,
    var is_deleted: Boolean? = null,
    var message: String? = null,
    var name: String? = null,
    var status: String? = null,
    var updated_date: Date? = null,
    var user_id: Int? = null,
    var user: NewUser? = null,
    var category: String? = null
)