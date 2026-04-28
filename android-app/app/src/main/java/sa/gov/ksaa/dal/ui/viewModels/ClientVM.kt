package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClientItem

class ClientVM: BaseVM(){


    fun getClientsById(clientId: Int) : MutableLiveData<NewResource<List<ClientItem>>>{ // clientId=2
        val mutableLiveData = MutableLiveData<NewResource<List<ClientItem>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            mutableLiveData.postValue(NewResource.Loading())
            mutableLiveData.postValue(newHandleResponse(repository.getClientsById(mapOf("clientId" to clientId.toString()))))
        }
        return mutableLiveData
    }
}