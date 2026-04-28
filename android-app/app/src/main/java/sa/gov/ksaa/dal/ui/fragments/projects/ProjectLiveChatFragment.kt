package sa.gov.ksaa.dal.ui.fragments.projects

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewChatMessage
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.ui.adapters.ChatMessagesRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.ChatingVM
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern


class ProjectLiveChatFragment : BaseFragment(R.layout.fragment_project_live_chat) {

    val vm: ChatingVM by viewModels()
    val chatingVM: ChatingVM by viewModels()
    lateinit var projectUnderway: ProjectUnderway
    lateinit var chatmessagesrvAdapter: ChatMessagesRV_Adapter
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)

        backBtn.setOnClickListener {
            findNavController()
                .popBackStack()

        }

        chatmessagesrvAdapter = ChatMessagesRV_Adapter(mutableListOf(), _user!!)
        messagesRV.adapter = chatmessagesrvAdapter
        messagesRV.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = linearLayoutManager.itemCount
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem < totalItemCount-1) {
                    scrollBottomFab.visibility = View.VISIBLE
                } else {
                    scrollBottomFab.visibility = View.GONE
                }
            }
        })

        projectUnderway = activityVM.ongoingProjectLD.value!!

        setProfileImage(projectUnderway.image, userIV, projectUnderway.userType, projectUnderway.gender)

        nameTV.text = if(_user!!.isClient()) projectUnderway.freelancerName else projectUnderway.clientName

        chatingVM.getChatMessagesByUserId(mapOf(
            "userId" to _user!!.userId.toString()
        )).observe(viewLifecycleOwner){res ->
            newHandleSuccessOrErrorResponse(res, { messagesList ->
                chatmessagesrvAdapter.setList(
                    messagesList.filter { newChatMessage ->

                        newChatMessage.projectId == projectUnderway.projectId &&
                        newChatMessage.message != "This project has been awarded by the client"

                    })

                scrollBottomFab.performClick()
            })
        }

        scrollBottomFab.setOnClickListener {
            messagesRV.scrollToPosition(chatmessagesrvAdapter.itemCount - 1)
        }

        sendMessageFab.setOnClickListener {
            Log.w(javaClass.simpleName, "onFileChosen: myFile = ------ ")

            if (_user!!.isClient()) {

                if (isValidMessage() && projectUnderway.clientUserId != null) {
                    val params = mapOf(
                        "chat" to chatMessage.message!!,
                        "sendById" to _user!!.userId!!.toString(),
                        "sendToId" to projectUnderway.freelancerUserId!!.toString(),
                        "projectId" to projectUnderway.projectId!!.toString()
                    )
                    Log.w(javaClass.simpleName, "updateUI: params = $params")
                    // chat?chat=مرحبا&sendById=5&sendToId=1&projectId=55
                    chatingVM.create_aChatMessage(params).observe(viewLifecycleOwner) { res ->
                        newHandleSuccessOrErrorResponse(res, { message ->
                            chatmessagesrvAdapter.addMessage(message)
                            messageET.text.clear()
                            scrollBottomFab.performClick()
                        })
                    }
                }
            }else{
                if (isValidMessage() && projectUnderway.freelancerUserId != null) {
                    val params = mapOf(
                        "chat" to chatMessage.message!!,
                        "sendById" to _user!!.userId!!.toString(),
                        "sendToId" to projectUnderway.clientUserId!!.toString(),
                        "projectId" to projectUnderway.projectId!!.toString()
                    )
                    Log.w(javaClass.simpleName, "updateUI: params = $params")
                    // chat?chat=مرحبا&sendById=5&sendToId=1&projectId=55
                    chatingVM.create_aChatMessage(params).observe(viewLifecycleOwner) { res ->
                        newHandleSuccessOrErrorResponse(res, { message ->
                            chatmessagesrvAdapter.addMessage(message)
                            messageET.text.clear()
                            scrollBottomFab.performClick()
                        })
                    }
                }

            }
        }
    }
    lateinit var input: String
    lateinit var chatMessage: NewChatMessage
    private fun isValidMessage(): Boolean {
        chatMessage = NewChatMessage()
        input = messageET.text.toString().trim()

        val p1 = Pattern.compile("[0-9]{2}[-][0-9]{3}[ ][0-9]{2}[ ][0-9]{2} ")
        val p2 = Pattern.compile("05[0-9]{8}")
        val p3 = Pattern.compile("966[0-9]{8}")
        val p4 = Pattern.compile("[٠-٩]+")
        if (p1.matcher(input).find() ||p2.matcher(input).find() ||p3.matcher(input).find() || input.contains('@')|| p4.matcher(input).find() ) {
            messageET.error = "يجب أن لا تحتوي الرسالة على رقم هاتف أو ايميل"
            messageET.requestFocus()
            return false
        }
        chatMessage.message = input
        chatMessage.createDate = Date(System.currentTimeMillis()).toString()
        chatMessage.sendById = _user!!.userId
        chatMessage.sendToId = NewUser.TECH_SUPPORT_USER.userId

        return true
    }
    lateinit var userIV: ImageView
    lateinit var nameTV: TextView
    lateinit var backBtn: ImageView

    lateinit var messagesRV: RecyclerView
    lateinit var messageET: EditText
    lateinit var sendMessageFab: FloatingActionButton

    lateinit var scrollBottomFab: FloatingActionButton
    private fun initViews(createdView: View) {
        backBtn = createdView.findViewById(R.id.backBtn)
        userIV = createdView.findViewById(R.id.userIV)
        nameTV = createdView.findViewById(R.id.nameTV)
        messagesRV = createdView.findViewById(R.id.messagesNSV)
        messageET = createdView.findViewById(R.id.messageET)
        scrollBottomFab = createdView.findViewById(R.id.scrollBottomFab)
        sendMessageFab = createdView.findViewById(R.id.sendMessageFab1)
    }

    override fun onActivityCreated() {
        super.onActivityCreated()
        bottomNavigationView.visibility = View.GONE
        appBarLayout.visibility = View.GONE
    }
}