package sa.gov.ksaa.dal.data.models

class InvitedFreelancer (
    FreelancerIds: MutableList<Int>? = null,

): InvitedFreelancerFilter(FreelancerIds)

open class InvitedFreelancerFilter(
    var serviceTypes: MutableList<Int>? = null,

)