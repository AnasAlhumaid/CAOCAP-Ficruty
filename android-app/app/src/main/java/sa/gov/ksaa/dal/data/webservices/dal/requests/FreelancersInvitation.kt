package sa.gov.ksaa.dal.data.webservices.dal.requests
import android.os.Parcelable

data class FreelancersInvitation (var freelancers_userIds: Set<Int>? = null,
                             var project_id: Int?= null,
    var freelancers_ids : Set<Int>? = null
    )