package sa.gov.ksaa.dal.data.webservices.newDal

import retrofit2.Response

sealed class NewResource<DATA_TYPE>(
    val data: DATA_TYPE? = null,
    val message: String? = null
){
    //  GenericResponse<Project, List<Project>?>
    class Success<DATA_TYPE>(data: DATA_TYPE) : NewResource<DATA_TYPE>(data)
    class Error<DATA_TYPE>(response: Response<DATA_TYPE>? = null, message: String? = null) : NewResource<DATA_TYPE>(data= response?.body(), message = message)
    class Loading<DATA_TYPE>: NewResource<DATA_TYPE>()
}