package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.models.Certification
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.dal.responses.EmailAvailabilityResponse

class CertificationsVM: BaseVM(){
    fun create_aCertification(certification: Certification) : MutableLiveData<Resource<Certification>>{
        val liveDate = MutableLiveData<Resource<Certification>>()
        viewModelScope.launch (coroutineExceptionHandler){
            liveDate.postValue(Resource.Loading())
//            liveDate.postValue(handleResponse(repository.create_aCertification(certification)))
        }
        return liveDate
    }
}