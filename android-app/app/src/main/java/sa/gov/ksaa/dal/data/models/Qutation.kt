package sa.gov.ksaa.dal.data.models

import java.util.Date

data class Qutation(
    var about_the_work_to_be_done: Any? = null,
    var created_date: Date? = null,
    var description: String? = null,
    var duration: String? = null,
    var expected_output_file_url: String? = null,
    var experience: String? = null,
    var freelance_id: Int? = null,
    var is_accepted: Boolean? = null,
    var is_deleted: Boolean? = null,
    var price: String? = null,
    var project_id: Int? = null,
    var updated_date: Date? = null
) {
    var attachmentDesc: String? = null
}