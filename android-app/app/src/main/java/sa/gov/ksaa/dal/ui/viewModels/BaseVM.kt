package sa.gov.ksaa.dal.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.Repository
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.dal.responses.GenericResponse
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import java.io.File

open class BaseVM : ViewModel(){
    var repository: Repository = Repository.getIstance()


    val MULTIPART_FORM_DATA = "multipart/form-data"
    var activityVM: MainActivityVM
     init {
         activityVM = MainActivityVM.get_Instance()
     }
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContxt, throwable ->
        Log.e(javaClass.simpleName, "throwable = $throwable", throwable)
        activityVM.exceptionMutableLiveData.value = throwable
    }


    fun <T> handleResponse(response: Response<GenericResponse<T>>): Resource<T> {
        Log.i(TAG, "handleResponse: response = $response , response.body() = ${response.body()}")

        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                if (resultResponse.data == null) return Resource.Error(resultResponse)
                return Resource.Success(resultResponse)
            }
        } else if (response.code() == 403){
            // response.errorBody() // okhttp3.ResponseBody
            response.body()?.let { resultResponse ->
                return Resource.Error(resultResponse, resultResponse.errors?.message)
            }
        }
        return Resource.Error(message = response.message())
    }

    fun <T> newHandleResponse(response: Response<T>): sa.gov.ksaa.dal.data.webservices.newDal.NewResource<T> {
        Log.i(TAG, "handleResponse: response = $response , response.body() = ${response.body()}")

        if (response.isSuccessful){
            val resultResponse = response.body()
            if (resultResponse == null)
                return sa.gov.ksaa.dal.data.webservices.newDal.NewResource.Error()
            else
                return sa.gov.ksaa.dal.data.webservices.newDal.NewResource.Success(resultResponse)

        } else if (response.code() == 403){
            Log.i(TAG, "newHandleResponse: response.errorBody() = ${response.errorBody()}")
            response.errorBody()
            return sa.gov.ksaa.dal.data.webservices.newDal.NewResource.Error(response, response.message())
//            response.body()?.let { resultResponse ->
//                return sa.gov.ksaa.dal.data.webservices.newDal.Resource.Error(response, response.message()?)
//            }
        }
        Log.i(TAG, "newHandleResponse: response.errorBody() = ${response.errorBody()}")
        return sa.gov.ksaa.dal.data.webservices.newDal.NewResource.Error(message = response.message())
    }

    fun toPartMap(queryMap: Map<String, String>): Map<String, RequestBody> {
        val result = mutableMapOf<String, RequestBody>()
        for(key in queryMap.keys){
            result[key] = queryMap[key]!!.toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
        }
        return result
    }
    fun createMultiPartBodyFromFile(paramName: String, myFile: MyFile): MultipartBody.Part {

        return MultipartBody.Part.createFormData(
            paramName,
            myFile.name,
            RequestBody.create(myFile.mimeType!!.toMediaTypeOrNull(), myFile.inputStream!!.readBytes())
        )
    }

}