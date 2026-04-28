package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date

data class Bid(
    var about_the_work_to_be_done: String?,
    var client_id: Int?,
    var created_date: Date?,
    var description: String?,
    var duration: String?,
    var expected_output_file_url: String?,
    var experience: String?,
    var freelance_id: Int?,
    var id: Int?,
    var is_accepted: Boolean?,
    var is_rejected: Boolean?,
    var is_deleted: Boolean?,
    var price: String?,
    var project_id: Int?,
    var updated_date: String?,
    var user_id: Int?,
    var freelancer: Freelancer?,
    var project: Project?,
    var user: NewUser?,
    var clientIndividual: Any?,
) {

}