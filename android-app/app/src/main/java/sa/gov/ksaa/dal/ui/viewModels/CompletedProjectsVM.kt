package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject

class CompletedProjectsVM : BaseVM(){

//    fun getById(id: Int): MutableLiveData<Resource<CompletedProject>> {
//        val ongoingProjectLD = MutableLiveData<Resource<CompletedProject>>()
//        viewModelScope.launch(coroutineExceptionHandler) {
//            ongoingProjectLD.postValue(Resource.Loading())
//            ongoingProjectLD.postValue(handleResponse(repository.getClosedProjectById(id)))
//        }
//        return ongoingProjectLD
//    }

//    fun update_anOngoingProject(ongoingProject: OngoingProject): MutableLiveData<Resource<OngoingProject>> {
//        val ongoingProjectLD = MutableLiveData<Resource<OngoingProject>>()
//        viewModelScope.launch(coroutineExceptionHandler) {
//            ongoingProjectLD.postValue(Resource.Loading())
//            ongoingProjectLD.postValue(handleResponse(repository.update_anOngoingProject(ongoingProject)))
//        }
//        return ongoingProjectLD
//    }

    // user_type=freelancer&user_id=1
    fun getAllByUserTypeAndUserId(querMap: Map<String, String>): MutableLiveData<NewResource<List<ClosedProject>>> {
        val ongoingProjectsLD = MutableLiveData<NewResource<List<ClosedProject>>> ()
        viewModelScope.launch(coroutineExceptionHandler) {
            ongoingProjectsLD.postValue(NewResource.Loading())
            ongoingProjectsLD.postValue(newHandleResponse(repository.getCompletedProjectByUserTypeAndUserId(querMap)))
        }
        return ongoingProjectsLD
    }
}