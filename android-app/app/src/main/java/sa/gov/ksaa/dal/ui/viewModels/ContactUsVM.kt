package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ObjectAndComplaint
import sa.gov.ksaa.dal.data.webservices.newDal.responses.SpamCommint

class ContactUsVM: BaseVM(){
    val enquiryLD = MutableLiveData<NewResource<ObjectAndComplaint>>()

    val spamCommintLD = MutableLiveData<NewResource<SpamCommint>>()

    fun create_anEnquiry(enquiry: Map<String, String>): MutableLiveData<NewResource<ObjectAndComplaint>>{

        viewModelScope.launch (coroutineExceptionHandler){
            enquiryLD.postValue(NewResource.Loading())
            val response = repository.create_anObjectionOrComplain(enquiry)
            enquiryLD.postValue(newHandleResponse(response))
        }
        return enquiryLD
    }

    fun create_commwent_spam(enquiry: Map<String, String>): MutableLiveData<NewResource<SpamCommint>>{

        viewModelScope.launch (coroutineExceptionHandler){
            spamCommintLD.postValue(NewResource.Loading())
            val response = repository.create_spamCommint(enquiry)
            spamCommintLD.postValue(newHandleResponse(response))
        }
        return spamCommintLD
    }
}