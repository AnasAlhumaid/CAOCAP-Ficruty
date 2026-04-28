package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.data.webservices.newDal.ChatMessage
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewChatMessage

class ChatingVM: BaseVM(){

    // userId=1
    fun getChatMessagesByUserId(queryMap: Map<String, String>): MutableLiveData<NewResource<List<NewChatMessage>>> {
        val messagesLD = MutableLiveData<NewResource<List<NewChatMessage>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            messagesLD.postValue(NewResource.Loading())
            messagesLD.postValue(newHandleResponse(repository.getChatMessagesByUserId(queryMap)))
        }
        return messagesLD
    }

    // userId=1
    fun create_aChatMessage(querMap: Map<String, String>): MutableLiveData<NewResource<NewChatMessage>> {
        val messageLD = MutableLiveData<NewResource<NewChatMessage>> ()
        viewModelScope.launch(coroutineExceptionHandler) {
            messageLD.postValue(NewResource.Loading())
            messageLD.postValue(newHandleResponse(repository.create_aChatMessage(querMap)))
        }
        return messageLD
    }
}