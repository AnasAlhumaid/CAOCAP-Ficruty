package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.http.QueryMap
import sa.gov.ksaa.dal.data.models.CommentOnProject
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportMail
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest

class TechnicalSupportVM: BaseVM() {


    //userType=Freelancer
    fun getAllMailsByUserType(queryMap: Map<String, String>): MutableLiveData<NewResource<List<TechnicalSupportMail>>> {
        val technicalSupportMailsMLD = MutableLiveData<NewResource<List<TechnicalSupportMail>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            technicalSupportMailsMLD.postValue(NewResource.Loading())
            val response = repository.getAllMailsByUserType(queryMap)
            technicalSupportMailsMLD.postValue(newHandleResponse(response))
        }
        return technicalSupportMailsMLD
    }

    //userType=Freelancer
    fun getTechSupportRequests(queryMap: Map<String, String>): MutableLiveData<NewResource<List<TechnicalSupportRequest>>> {
        val technicalSupportMailsMLD = MutableLiveData<NewResource<List<TechnicalSupportRequest>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            technicalSupportMailsMLD.postValue(NewResource.Loading())
            val response = repository.getTechSupportRequests(queryMap)
            technicalSupportMailsMLD.postValue(newHandleResponse(response))
        }
        return technicalSupportMailsMLD
    }



}