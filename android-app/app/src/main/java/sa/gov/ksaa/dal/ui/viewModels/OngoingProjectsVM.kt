package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.models.OngoingProject
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.CloseProjectRes
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway

class OngoingProjectsVM : BaseVM(){
    val ongoingProjectLD = MutableLiveData<Resource<OngoingProject>>()
//    fun getOngoingProjectById(id: Int): MutableLiveData<Resource<OngoingProject>> {
//
//        viewModelScope.launch(coroutineExceptionHandler) {
//            ongoingProjectLD.postValue(Resource.Loading())
//            ongoingProjectLD.postValue(handleResponse(repository.getOngoingProjectById(id)))
//        }
//        return ongoingProjectLD
//    }

//    fun update_anOngoingProject(ongoingProject: OngoingProject): MutableLiveData<Resource<OngoingProject>> {
//        viewModelScope.launch(coroutineExceptionHandler) {
//            ongoingProjectLD.postValue(Resource.Loading())
//            ongoingProjectLD.postValue(handleResponse(repository.update_anOngoingProject(ongoingProject)))
//        }
//        return ongoingProjectLD
//    }

    // user_type=&user_id=6
    fun getAllByUserTypeAndUserId(querMap: Map<String, String>): MutableLiveData<NewResource<List<ProjectUnderway>>> {
        val ongoingProjectsLD = MutableLiveData<NewResource<List<ProjectUnderway>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            ongoingProjectsLD.postValue(NewResource.Loading())
            ongoingProjectsLD.postValue(newHandleResponse(repository.getOngoingProjectsByUserTypeAndUserId(querMap)))
        }
        return ongoingProjectsLD
    }


    // projectId=79
    fun close_aProject(querMap: Map<String, String>): MutableLiveData<NewResource<CloseProjectRes>> {
        val ongoingProjectsLD = MutableLiveData<NewResource<CloseProjectRes>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            ongoingProjectsLD.postValue(NewResource.Loading())
            ongoingProjectsLD.postValue(newHandleResponse(repository.close_aProject(querMap)))
        }
        return ongoingProjectsLD
    }


}