package sa.gov.ksaa.dal.data.models

import java.util.Date

data class TechSupportTicket(
    var created_date: Date? = null,
    var updated_date: Date? = null,
    var status: String? = null,
    var sequence_number: Int? = null,

): MyModel() {
    companion object {
        const val REPLIED_STATE = "تم الرد"
    }
    fun isClosed(): Boolean{
        return status == REPLIED_STATE
    }
}