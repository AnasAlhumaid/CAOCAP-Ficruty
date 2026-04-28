package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.models.FreelancerInvitedProject
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.dal.requests.FreelancersInvitation
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.BiddingInvitation

class ProjectInvitatinsVM : BaseVM(){
//    fun getFreelancersInvitedProjects(params: Map<String, String>? = null): MutableLiveData<Resource<List<FreelancerInvitedProject>>> {
//        val freelancersProjectsInvitationsLD = MutableLiveData<Resource<List<FreelancerInvitedProject>>>()
//        viewModelScope.launch(coroutineExceptionHandler) {
//            freelancersProjectsInvitationsLD.postValue(Resource.Loading())
//            freelancersProjectsInvitationsLD.postValue(handleResponse(repository.getFreelancersInvitedProjects(params)))
//        }
//        return freelancersProjectsInvitationsLD
//    }

    //user_id=1
    fun getBiddingInvitationsByUserId(querMap: Map<String, String>): MutableLiveData<NewResource<List<BiddingInvitation>>> {
        val freelancersProjectsInvitationsLD =  MutableLiveData<NewResource<List<BiddingInvitation>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            freelancersProjectsInvitationsLD.postValue(NewResource.Loading())
            freelancersProjectsInvitationsLD.postValue(newHandleResponse(repository.getBiddingInvitations(querMap)))
        }
        return freelancersProjectsInvitationsLD
    }

    fun inviteFreelancersTo_aProject(freelancersInvitation: FreelancersInvitation): MutableLiveData<Resource<List<FreelancerInvitedProject>>> {
        val freelancersProjectsInvitationsLD = MutableLiveData<Resource<List<FreelancerInvitedProject>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            freelancersProjectsInvitationsLD.postValue(Resource.Loading())
//            freelancersProjectsInvitationsLD.postValue(handleResponse(repository.inviteFreelancersTo_aProject(freelancersInvitation)))
        }
        return freelancersProjectsInvitationsLD
    }

//    fun declineAnInvitation(queryMap: Map<String, String>?): MutableLiveData<Resource<DeleteItemsRespose>>{
//        val mutableLiveData = MutableLiveData<Resource<DeleteItemsRespose>>()
//        viewModelScope.launch(coroutineExceptionHandler) {
//            mutableLiveData.postValue(Resource.Loading())
//            mutableLiveData.postValue(handleResponse(repository.declineAnInvitation(queryMap)))
//        }
//        return mutableLiveData
//    }
}