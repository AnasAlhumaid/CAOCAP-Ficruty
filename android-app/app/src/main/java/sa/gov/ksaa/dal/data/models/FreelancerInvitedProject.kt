package sa.gov.ksaa.dal.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date


data class FreelancerInvitedProject(
    var project_id: Int? = null,
    var freelance_id: Int? = null,
    var created_date: Date? = null,
    var updated_date:  Date? = null,
    @SerializedName("Freelance")
    var freelancer:  Freelancer? = null,
    @SerializedName("Project")
    var project: Project? = null,
): MyModel()