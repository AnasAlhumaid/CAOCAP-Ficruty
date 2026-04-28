package sa.gov.ksaa.dal.data.webservices.dal.responses

import retrofit2.Response
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors

 data class GenericResponse<DATA_TYPE>(
    val data: DATA_TYPE?,
    val errors: Errors?
)