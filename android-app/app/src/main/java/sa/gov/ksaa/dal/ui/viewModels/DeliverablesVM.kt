package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile

class DeliverablesVM: BaseVM(){

    // userId, projectId, uploadfile, attachmentDesc
    fun add(queryMap: MutableMap<String, String>, uploadfile: MyFile?) :
            MutableLiveData<NewResource<NewDeliverableFile>>{
        val _uploadfile: MultipartBody.Part?
        if(uploadfile == null){
            queryMap["uploadfile"] = ""
            _uploadfile = null
        }
        else {
            _uploadfile = createMultiPartBodyFromFile("uploadfile", uploadfile)
        }

        val partMap = toPartMap(queryMap)

        val deliverableLD =  MutableLiveData<NewResource<NewDeliverableFile>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            deliverableLD.postValue(NewResource.Loading())
            deliverableLD.postValue(newHandleResponse(repository.addProjectDeliverable(partMap, _uploadfile)))
        }
        return deliverableLD
    }

    // projectId
    fun getAll(queryMap: MutableMap<String, String>) :
            MutableLiveData<NewResource<List<NewDeliverableFile>>>{
        val deliverableLD =  MutableLiveData<NewResource<List<NewDeliverableFile>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            deliverableLD.postValue(NewResource.Loading())
            deliverableLD.postValue(newHandleResponse(repository.getProjectDeliverables(queryMap)))
        }
        return deliverableLD
    }
    fun deleteDraivable(queryMap: MutableMap<String, String>, uploadfile: MyFile?) :
            MutableLiveData<NewResource<NewDeliverableFile>>{
        val _uploadfile: MultipartBody.Part?
        if(uploadfile == null){
            queryMap["fileForUpdate"] = ""
            _uploadfile = null
        }
        else {
            _uploadfile = createMultiPartBodyFromFile("fileForUpdate", uploadfile)
        }

        val partMap = toPartMap(queryMap)

        val deliverableLD =  MutableLiveData<NewResource<NewDeliverableFile>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            deliverableLD.postValue(NewResource.Loading())
            deliverableLD.postValue(newHandleResponse(repository.deleteDraivable(partMap, _uploadfile)))
        }
        return deliverableLD
    }
}