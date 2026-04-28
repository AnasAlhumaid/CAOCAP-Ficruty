package sa.gov.ksaa.dal.data.webservices.newDal.responses

import java.util.Date

data class TechnicalSupportRequest(
    val category: String?,
    val createdDate: Date?,
    val description: String?,
    val listOfMessage: List<Any?>?,
    val objectionFrom: Int?,
    val objectionTo: Int?,
    val referenceCode: String?,
    val ticketNumber: Int?,
    val ticketStatus: String?
){
    fun isClosed(): Boolean{
        return ticketStatus == "closed"
    }

    companion object {
        const val INPROGRESS_STATUS = "in-progress"
    }



}