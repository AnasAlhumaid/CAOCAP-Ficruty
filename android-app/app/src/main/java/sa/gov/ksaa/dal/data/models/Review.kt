package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date

data class Review(
    var reviewer: NewUser? = null,
    var reviewer_user_id: Int? = null,
    var reviewee: NewUser? = null,
    var reviewee_user_id: Int? = null,
    var closed_project: CompletedProject? = null,
    var closed_project_id: Int? = null,
    var review: String?= null,
    var rating: Float? = null,
    var created_date: Date? = null,
    var updated_date: Date? = null,
) : MyModel()