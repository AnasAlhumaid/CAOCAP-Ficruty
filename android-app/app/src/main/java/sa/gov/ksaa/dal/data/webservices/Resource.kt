package sa.gov.ksaa.dal.data.webservices

import sa.gov.ksaa.dal.data.webservices.dal.responses.GenericResponse

sealed class Resource<DATA_TYPE>(
    val response: GenericResponse<DATA_TYPE>? = null,
    val message: String? = null
){
    //  GenericResponse<Project, List<Project>?>
    class Success<DATA_TYPE>(response: GenericResponse<DATA_TYPE>) : Resource<DATA_TYPE>(response)
    class Error<DATA_TYPE>(response: GenericResponse<DATA_TYPE>? = null, message: String? = null) : Resource<DATA_TYPE>(response= response, message = message)
    class Loading<DATA_TYPE>: Resource<DATA_TYPE>()
}