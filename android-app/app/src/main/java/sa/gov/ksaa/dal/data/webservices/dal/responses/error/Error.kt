package sa.gov.ksaa.dal.data.webservices.dal.responses.error


data class Error(
    val path: String?,
//    val instance: T?,
    val key: String?,
    val message: String?,
    val type: Any?,
    val value: String?
)