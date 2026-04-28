package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewCommentOnProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ReplyTo_aComment

class CommentsOnProjectVM: BaseVM() {

    val projectCommentLD = MutableLiveData<NewResource<NewCommentOnProject>>()
    fun add_aProjectComment(q: Map<String, String>): MutableLiveData<NewResource<NewCommentOnProject>> {

        viewModelScope.launch(coroutineExceptionHandler) {
            projectCommentLD.postValue(NewResource.Loading())
            val response = repository.add_aProjectComment(q)
            projectCommentLD.postValue(newHandleResponse(response))
        }
        return projectCommentLD
    }

    val projectCommentsLD = MutableLiveData<NewResource<List<NewCommentOnProject>>>()

    // projectId=18
    fun getCommentsByProjectId(querMap: Map<String, String>): MutableLiveData<NewResource<List<NewCommentOnProject>>> {

        viewModelScope.launch(coroutineExceptionHandler) {
            projectCommentsLD.postValue(NewResource.Loading())
            val response = repository.getCommentsByProjectId(querMap)
            projectCommentsLD.postValue(newHandleResponse(response))
        }
        return projectCommentsLD
    }

    val projectRpCommentsLD = MutableLiveData<NewResource<ReplyTo_aComment>>()
    fun replayComment(querMap: Map<String, String>): MutableLiveData<NewResource<ReplyTo_aComment>> {

        viewModelScope.launch(coroutineExceptionHandler) {
            projectRpCommentsLD.postValue(NewResource.Loading())
            val response = repository.add_aReplyToComment(querMap)
            projectRpCommentsLD.postValue(newHandleResponse(response))
        }
        return projectRpCommentsLD
    }
}