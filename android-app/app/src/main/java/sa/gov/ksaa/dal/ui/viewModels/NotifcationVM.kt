package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Notifcation

class NotifcationVM : BaseVM() {


    fun getNotifcation(queryMap: Map<String, String>): MutableLiveData<NewResource<List<Notifcation>>> {
        val notifcation = MutableLiveData<NewResource<List<Notifcation>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            notifcation.postValue(NewResource.Loading())
            notifcation.postValue(newHandleResponse(repository.getNotifcation(queryMap)))
        }
        return notifcation
    }
}