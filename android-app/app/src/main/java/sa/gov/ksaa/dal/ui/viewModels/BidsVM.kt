package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.GroupedBids
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid

class BidsVM : BaseVM(){

    val newBidsLD = MutableLiveData<NewResource<List<NewBid>>>()

    fun getBidsByUserId(queryMap: Map<String,String>): MutableLiveData<NewResource<List<GroupedBids>>> {
        val newBidsLD = MutableLiveData<NewResource<List<GroupedBids>>>()
        viewModelScope.launch (coroutineExceptionHandler){
            newBidsLD.postValue(NewResource.Loading())
            newBidsLD.postValue(newHandleResponse(repository.getBidsByUserId(queryMap)))
        }
        return newBidsLD
    }
    fun getBidsClientByUserId(queryMap: Map<String,String>): MutableLiveData<NewResource<List<NewBid>>> {
        val newBidsLD = MutableLiveData<NewResource<List<NewBid>>>()
        viewModelScope.launch (coroutineExceptionHandler){
            newBidsLD.postValue(NewResource.Loading())
            newBidsLD.postValue(newHandleResponse(repository.getBidsByCleintUserId(queryMap)))
        }
        return newBidsLD
    }
    fun deleteBidForFreelancer(queryMap: Map<String,String>): MutableLiveData<NewResource<List<GroupedBids>>> {
        val newBidsLD = MutableLiveData<NewResource<List<GroupedBids>>>()
        viewModelScope.launch (coroutineExceptionHandler){
            newBidsLD.postValue(NewResource.Loading())
            newBidsLD.postValue(newHandleResponse(repository.deleteBidForFreelancer(queryMap)))
        }
        return newBidsLD
    }

    fun getBidsByFreelancerId(queryMap: Map<String,String>): MutableLiveData<NewResource<List<NewBid>>> {
        viewModelScope.launch (coroutineExceptionHandler){
            newBidsLD.postValue(NewResource.Loading())
            newBidsLD.postValue(newHandleResponse(repository.getBidsByFreelancerId(queryMap)))
        }
        return newBidsLD
    }

    // projectId=88&bidId=31
    fun accepting_aBid(queryMap: Map<String,String>): MutableLiveData<NewResource<Any>> {
        val newBidsLD = MutableLiveData<NewResource<Any>>()
        viewModelScope.launch (coroutineExceptionHandler){
            newBidsLD.postValue(NewResource.Loading())
            newBidsLD.postValue(newHandleResponse(repository.accepting_aBid(queryMap)))
        }
        return newBidsLD
    }

    fun reject_aBid(queryMap: Map<String,String>): MutableLiveData<NewResource<Any>> {
        val newBidsLD = MutableLiveData<NewResource<Any>>()
        viewModelScope.launch (coroutineExceptionHandler){
            newBidsLD.postValue(NewResource.Loading())
            newBidsLD.postValue(newHandleResponse(repository.reject_aBid(queryMap)))
        }
        return newBidsLD
    }

    val quationLD = MutableLiveData<NewResource<NewBid>>()

    fun create_aBid(queryMap: MutableMap<String, String>, biddingFile: MyFile?): MutableLiveData<NewResource<NewBid>>{

        val _biddingFile: MultipartBody.Part?
        if(biddingFile != null)
            _biddingFile = createMultiPartBodyFromFile("biddingFile", biddingFile)
        else {
            queryMap["biddingFile"] = ""
            _biddingFile = null
        }

        val partMap = toPartMap(queryMap)

        viewModelScope.launch(coroutineExceptionHandler){
            quationLD.postValue(NewResource.Loading())
            quationLD.postValue(newHandleResponse(repository.create_aBid(partMap, _biddingFile)))
        }
        return quationLD
    }
}