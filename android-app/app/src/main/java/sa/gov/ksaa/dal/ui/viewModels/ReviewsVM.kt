package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.models.Review
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview

class ReviewsVM: BaseVM(){

    fun add(review: Review) : MutableLiveData<Resource<Review>>{
        val deliverableLD = MutableLiveData<Resource<Review>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            deliverableLD.postValue(Resource.Loading())
//            deliverableLD.postValue(handleResponse(repository.add_aReivew(review)))
        }
        return deliverableLD
    }

    // userId=2
    fun getAllReviewsByUserId(queryMap: Map<String, String>) : MutableLiveData<NewResource<List<RatingAndReview>>>{
        val deliverableLD = MutableLiveData<NewResource<List<RatingAndReview>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            deliverableLD.postValue(NewResource.Loading())
            deliverableLD.postValue(newHandleResponse(repository.getAllReviewsByUserId(queryMap)))
        }
        return deliverableLD
    }

    // projectId=49&clientId=6&freelancerId=3&rating=4&review=جيدجيد
    fun rate_aFreelancer(queryMap: Map<String, String>) : MutableLiveData<NewResource<RatingAndReview>>{
        val deliverableLD = MutableLiveData<NewResource<RatingAndReview>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            deliverableLD.postValue(NewResource.Loading())
            deliverableLD.postValue(newHandleResponse(repository.rate_aFreelancer(queryMap)))
        }
        return deliverableLD
    }

    // projectId=49&clientId=5&freelancerId=2&rating=4&review=ممتاز
    fun rate_aClient(queryMap: Map<String, String>) : MutableLiveData<NewResource<RatingAndReview>>{
        val deliverableLD = MutableLiveData<NewResource<RatingAndReview>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            deliverableLD.postValue(NewResource.Loading())
            deliverableLD.postValue(newHandleResponse(repository.rate_aClient(queryMap)))
        }
        return deliverableLD
    }

    fun reportReviews(queryMap: Map<String, String>) : MutableLiveData<NewResource<RatingAndReview>>{
        val deliverableLD = MutableLiveData<NewResource<RatingAndReview>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            deliverableLD.postValue(NewResource.Loading())
            deliverableLD.postValue(newHandleResponse(repository.reportReviews(queryMap)))
        }
        return deliverableLD
    }

}