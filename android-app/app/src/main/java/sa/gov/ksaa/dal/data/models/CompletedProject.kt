package sa.gov.ksaa.dal.data.models

import java.util.Date

data class CompletedProject(
    var created_date: Date? = null,
    var updated_date: Date?= null,
    var working_project: OngoingProject?= null,
    var working_project_id: Int?= null,
    var is_client_reviewed: Boolean?= null,
    var is_freelancer_reviewed: Boolean? = null,
    var reviews: List<Review>? = null
): MyModel()