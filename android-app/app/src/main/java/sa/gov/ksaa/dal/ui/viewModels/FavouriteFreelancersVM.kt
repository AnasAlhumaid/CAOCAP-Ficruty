package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FavouriteFreelancer

class FavouriteFreelancersVM: BaseVM(){

    // userId=5
    fun getFavoriteFreelancers(userId: Int): MutableLiveData<NewResource<List<FavouriteFreelancer>>> {
        val favoriteFreelancersLD = MutableLiveData<NewResource<List<FavouriteFreelancer>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            favoriteFreelancersLD.postValue(NewResource.Loading())
            favoriteFreelancersLD.postValue(newHandleResponse(repository.getFavoriteFreelancer(mapOf( "userId" to userId.toString()))))
        }
        return favoriteFreelancersLD
    }

    // clientId=6&isFavourite=true&freelancerId=2
    fun favouriteFreelancer(freelancerFavoriteProject: Map<String, String>): MutableLiveData<NewResource<FavouriteFreelancer>> {
        val favouriteFreelancerLD = MutableLiveData<NewResource<FavouriteFreelancer>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            favouriteFreelancerLD.postValue(NewResource.Loading())
            favouriteFreelancerLD.postValue(newHandleResponse(repository.favouriteFreelancer(freelancerFavoriteProject)))
        }
        return favouriteFreelancerLD
    }
}