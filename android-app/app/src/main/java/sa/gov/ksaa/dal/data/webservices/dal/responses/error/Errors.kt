package sa.gov.ksaa.dal.data.webservices.dal.responses.error

data class Errors(
    val errorsList: List<Error?>?,
    val message: String?,
    val name: String?,
    val code: Int?
)