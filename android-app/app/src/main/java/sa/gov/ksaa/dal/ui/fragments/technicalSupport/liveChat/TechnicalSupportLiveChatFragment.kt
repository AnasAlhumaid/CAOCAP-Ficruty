package sa.gov.ksaa.dal.ui.fragments.technicalSupport.liveChat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.ChatMessage
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewChatMessage
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.adapters.ChatMessagesRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.ChatingVM
import java.util.Date

class TechnicalSupportLiveChatFragment : BaseFragment(R.layout.fragment_technical_support_live_chat) {

    val vm: ChatingVM by viewModels()
    var chatMessages: List<ChatMessage>? = null
    lateinit var chatmessagesrvAdapter: ChatMessagesRV_Adapter
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)

        chatmessagesrvAdapter = ChatMessagesRV_Adapter(mutableListOf(), _user!!)
        messagesRV.adapter = chatmessagesrvAdapter

        vm.getChatMessagesByUserId(mapOf("userId" to _user!!.userId.toString()))
            .observe(viewLifecycleOwner){
                newHandleSuccessOrErrorResponse(it, { messages ->
                    Log.i(javaClass.simpleName, "onViewCreated: messages = $messages")
//                    chatMessages = messages
                }) {

                }
            }
        closeIB.setOnClickListener {
            findNavController().popBackStack()
        }

        sendMessageFab.setOnClickListener {
            if(isValidMessage()){
                chatmessagesrvAdapter.addMessage(chatMessage)
                messageET.text.clear()
            }
        }
    }
    lateinit var chatMessage: NewChatMessage
    private fun isValidMessage(): Boolean {
        chatMessage = NewChatMessage()
        chatMessage.message = messageET.text.toString().trim()
        chatMessage.createDate = Date(System.currentTimeMillis()).toString()
        chatMessage.sendById = _user!!.userId
        chatMessage.sendToId = NewUser.TECH_SUPPORT_USER.userId

        return true
    }

    lateinit var closeIB: ImageButton
    lateinit var messagesRV: RecyclerView
    lateinit var messageET: EditText
    lateinit var sendMessageFab: FloatingActionButton
    private fun initViews(createdView: View) {
        closeIB = createdView.findViewById(R.id.closeIB)
        messagesRV = createdView.findViewById(R.id.messagesRV)
        messageET = createdView.findViewById(R.id.messageET)
        sendMessageFab = createdView.findViewById(R.id.sendMessageFab1)
    }

    override fun onActivityCreated() {
        super.onActivityCreated()
        bottomNavigationView.visibility = View.GONE
    }
}