package sa.gov.ksaa.dal.data.models

import java.util.Date

data class OngoingProject(
    var bid_id: Int?,
    var created_date: Date?,
    var freelance_id: Int?,
    var pay_status: String?,
    var project_id: Int?,
    var project_status: String?,
    var start_date: Date?,
    var updated_date: Date?,
    var project: Project?,
    var freelancer: Freelancer?,
    var winning_bid: Bid?,
    var deliverables: List<ProjectDeliverable>?,

): MyModel()