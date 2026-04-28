package sa.gov.ksaa.dal.data.models

import java.util.Date

data class WorkingProject(
    var created_date: Date? = null,
    var updated_date: Date? = null,
    var pay_status: String? = null,
    var project_status: String? = null,
    var start_date: Date? = null,
    )