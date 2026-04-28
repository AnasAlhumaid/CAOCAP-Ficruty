package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Favourite_aProjectRes
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject

class FavouriteProjectsVM: BaseVM(){
    fun favouriteProject(querymap: Map<String, String>): MutableLiveData<NewResource<Favourite_aProjectRes>> {
        val favouriteLD = MutableLiveData<NewResource<Favourite_aProjectRes>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            favouriteLD.postValue(NewResource.Loading())
            favouriteLD.postValue(newHandleResponse(repository.favouriteProject(querymap)))
        }
        return favouriteLD
    }

    // freelancerId=4
    fun getFavoriteProjects(freelancerId : Int): MutableLiveData<NewResource<List<NewProject>>>{
        val favoriteProjectsLD = MutableLiveData<NewResource<List<NewProject>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            favoriteProjectsLD.postValue(NewResource.Loading())
            favoriteProjectsLD.postValue(newHandleResponse(repository.getFavoriteProjects(mapOf("userId" to freelancerId.toString()))))
        }
        return favoriteProjectsLD
    }
}