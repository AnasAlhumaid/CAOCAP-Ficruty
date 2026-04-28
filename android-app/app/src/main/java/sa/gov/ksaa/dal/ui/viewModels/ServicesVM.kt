package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel

class ServicesVM: BaseVM() {

    val servicesLD = MutableLiveData<NewResource<List<ServicesModel>>>()

    fun getAllServices(): MutableLiveData<NewResource<List<ServicesModel>>> {

        viewModelScope.launch(coroutineExceptionHandler) {
            servicesLD.postValue(NewResource.Loading())
            servicesLD.postValue(
                newHandleResponse(
                    repository.getAllServices()
                ))
        }

        return servicesLD
    }
}