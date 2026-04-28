package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ObjectAndComplaint

class ObjectionsAndEnquiriesVM: BaseVM(){
    val enqueryLD = MutableLiveData<NewResource<ObjectAndComplaint>>()
    fun addEnquery(enquiry: Map<String, String>): MutableLiveData<NewResource<ObjectAndComplaint>> {

        viewModelScope.launch(coroutineExceptionHandler) {
            enqueryLD.postValue(NewResource.Loading())
            enqueryLD.postValue(newHandleResponse(repository.create_anObjectionOrComplain(enquiry)))
        }
        return enqueryLD
    }
}