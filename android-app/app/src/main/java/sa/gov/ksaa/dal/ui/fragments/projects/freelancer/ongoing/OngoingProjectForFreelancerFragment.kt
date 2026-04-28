package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.ongoing

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.ui.adapters.ProjectDeliverableRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.client.ClientProfileFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.viewModels.ChatingVM
import sa.gov.ksaa.dal.ui.viewModels.DeliverablesVM
import sa.gov.ksaa.dal.ui.viewModels.OngoingProjectsVM
import java.util.Calendar
import java.util.Date

class OnGoingProjectForFreelancerFragment :
    BaseFragment(R.layout.fragment_project_ongoing_for_freelancer),
    AddDeliverableToAnOngoingProjectByFreelancer.OnSubmitLister,
    ProjectDeliverableRV_Adapter.OnClickListener {

    companion object {
        const val onGgoinProjectIdKey = "onGgoinProjectId"
    }

    private val ongoingProjectsVM: OngoingProjectsVM by viewModels()
    private val chatingVM: ChatingVM by viewModels()
    private val deliverablesVM: DeliverablesVM by viewModels()

    //    private lateinit var chatmessagesrvAdapter: ChatMessagesRV_Adapter
    private lateinit var projectdeliverablervAdapter: ProjectDeliverableRV_Adapter
    var onGgoinProjectId: Int? = 0
    lateinit var onGgoinProject: ProjectUnderway

    lateinit var filesList: List<NewDeliverableFile>

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        bottomNavigationView.visibility = View.GONE
        appBarLayout.visibility = View.VISIBLE

        initViews(createdView)

//        chatmessagesrvAdapter = ChatMessagesRV_Adapter(mutableListOf(), _user!!)
//        messagesRV.adapter = chatmessagesrvAdapter

        projectdeliverablervAdapter = ProjectDeliverableRV_Adapter(mutableListOf(), this,_user)
        deliverableRV.adapter = projectdeliverablervAdapter


        onGgoinProject = activityVM.ongoingProjectLD.value!!
        Log.w(javaClass.simpleName, "onViewCreated: onGgoinProject = $onGgoinProject")
        updateUI()

//        onGgoinProjectId = arguments?.getInt(onGgoinProjectIdKey, 0)
//        onGgoinProjectId?.let {
//            ongoingProjectsVM.getOngoingProjectById(it)
//                .observe(viewLifecycleOwner){ res ->
//                    handleSuccessOrErrorResponse(res, {newOnGgoinProject ->
//                        onGgoinProject = newOnGgoinProject
//                        updateUI()
//                    })
//                }
//        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        projectTitleTV.text = onGgoinProject.projectTitle
        val startDate = onGgoinProject.startDate ?: Date()
        startDateTV.text = dateFormatAr.format(startDate)
        calendar.time = startDate
//        calendar.add(Calendar.DATE, (onGgoinProject.durationOfProject ?: "0").toInt())
//        remainingDaysTV.text = dateFormatAr.format(calendar.time)

        val remainingDays: Int = try {
            onGgoinProject.durationOfProject?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
        } catch (e: NumberFormatException) {
            0 // Set to 0 if conversion fails
        }
        val formattedRemainingDays = when (remainingDays) {
            is Int, -> numberFormat.format(remainingDays) // Format if it's a number
            // Use the string as is
            else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
        }
        remainingDaysTV.text = formattedRemainingDays
        val cost: Int = try {
            onGgoinProject.amount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
        } catch (e: NumberFormatException) {
            0 // Set to 0 if conversion fails
        }
        val formattedCost = when (cost) {
            is Int, -> numberFormat.format(cost) // Format if it's a number
            // Use the string as is
            else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
        }
        costTV.text = formattedCost
//        paymentStatusTV.text = "null"

        setOtherUserImage(
            onGgoinProject.image,
            clientIV,
            NewUser.CLIENT_USER_TYPE,
            onGgoinProject.gender
        )


        clientIV.setOnClickListener {
            onGgoinProject.clientUserId?.let { userId ->
                findNavController().navigate(
                    R.id.action_onGoingProjectForFreelancerFragment_to_clientProfileFragment,
                    bundleOf(ClientProfileFragment.userIdKey to userId)
                )
            }
        }


        clientNameTV.text = "${onGgoinProject.clientName} ${onGgoinProject.clientLastName}"

//        // userId=1
//        chatingVM.getChatMessagesByUserId(
//            mapOf(
//                "userId" to _user!!.userId.toString()
//            )
//        ).observe(viewLifecycleOwner) { res ->
//            newHandleSuccessOrErrorResponse(res, { messagesList ->
//                chatmessagesrvAdapter.setList(
//                    messagesList.filter { newChatMessage ->
//                        newChatMessage.projectId == onGgoinProject.projectId
//                    })
//            })
//        }
//            messagesRV

        // projectId

        val params = mutableMapOf<String, String>(
            "projectId" to onGgoinProject.projectId.toString()
        )
        Log.i(javaClass.simpleName, "updateUI: deliverablesVM.getAll($params)")
        deliverablesVM.getAll(params)
            .observe(viewLifecycleOwner) { res ->
                newHandleSuccessOrErrorResponse(res,
                    onSuccess = { file ->

                        filesList = file.filter {
                            !it.imageUrl?.endsWith(".tmp" , false)!!

                        }
                        projectdeliverablervAdapter.setList(filesList)
                    })
//                    {
//                    if (it.isEmpty())  {
//
//                        deliverablesCard.visibility = View.GONE
//                        nodateTV.visibility = View.VISIBLE
//                    } else {
//
//
//                        projectdeliverablervAdapter.setList(it)
//                    }
//                })
            }


//        sendMessageFab.setOnClickListener {
//            if (isValidMessage()) {
//                val params = mapOf(
//                    "chat" to chatMessage.line_text!!,
//                    "sendById" to _user!!.userId!!.toString(),
//                    "sendToId" to onGgoinProject.userId!!.toString(),
//                    "projectId" to onGgoinProject.projectId!!.toString(),
//                )
//                Log.w(javaClass.simpleName, "updateUI: params = $params")
//                // chat?chat=مرحبا&sendById=5&sendToId=1&projectId=55
//                chatingVM.create_aChatMessage(params).observe(viewLifecycleOwner) { res ->
//                    newHandleSuccessOrErrorResponse(res, { message ->
//                        chatmessagesrvAdapter.addMessage(message)
//                        messageET.text.clear()
//                    })
//                }
//
//
//            }
//        }


        projectTS_Btn.setOnClickListener {
            this@OnGoingProjectForFreelancerFragment.findNavController()
                .navigate(R.id.action_onGoingProjectForFreelancerFragment_to_technicalSupportTicketsFragment)
        }
        addDeliverableBtn.setOnClickListener { _ ->

            AddDeliverableToAnOngoingProjectByFreelancer(onGgoinProjectId!!, this)
                .show(
                    mainActivity.supportFragmentManager,
                    AddDeliverableToAnOngoingProjectByFreelancer.tag
                )
        }

        liveCatBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_onGoingProjectForFreelancerFragment_to_projectLiveChatFragment)
        }
    }

//    lateinit var chatMessage: ChatMessage
//    private fun isValidMessage(): Boolean {
//        chatMessage = ChatMessage()
//        chatMessage.line_text = messageET.text.toString().trim()
//        chatMessage.date_time = Date(System.currentTimeMillis())
//        chatMessage.from = _user
////        chatMessage.to = onGgoinProject?.freelancer?.user
//        return true
//    }

    lateinit var projectTitleTV: TextView
    lateinit var startDateTV: TextView
    lateinit var remainingDaysTV: TextView
    lateinit var costTV: TextView
    lateinit var paymentStatusTV: TextView
    lateinit var clientIV: ImageView
    lateinit var clientNameTV: TextView

    //    lateinit var messagesRV: RecyclerView
//    lateinit var messageET: EditText
//    lateinit var sendMessageFab: FloatingActionButton
    lateinit var projectTS_Btn: Button
    lateinit var addDeliverableBtn: Button
    lateinit var deliverableRV: RecyclerView
    lateinit var deliverablesCard: CardView
    lateinit var nodateTV: TextView
    lateinit var liveCatBtn: Button
    lateinit var deleteDrawable : ImageButton


    private fun initViews(createdView: View) {
        projectTitleTV = createdView.findViewById(R.id.projectTitleTV)
        startDateTV = createdView.findViewById(R.id.startDateTV)
        remainingDaysTV = createdView.findViewById(R.id.remainingDaysTV)
        costTV = createdView.findViewById(R.id.costTV)
        paymentStatusTV = createdView.findViewById(R.id.paymentStatusTV)
        clientIV = createdView.findViewById(R.id.clientIV)
        clientNameTV = createdView.findViewById(R.id.clientNameTV)
//        messagesRV = createdView.findViewById(R.id.messagesRV)
//        messageET = createdView.findViewById(R.id.messageET)
//        sendMessageFab = createdView.findViewById(R.id.sendMessageFab)
        projectTS_Btn = createdView.findViewById(R.id.projectTS_Btn)
        addDeliverableBtn = createdView.findViewById(R.id.addDeliverableBtn)
        deliverableRV = createdView.findViewById(R.id.deliverableRV)
        deliverablesCard = createdView.findViewById(R.id.deliverablesCard)
        nodateTV = createdView.findViewById(R.id.nodateTV)
        liveCatBtn = createdView.findViewById(R.id.liveCatBtn)



    }

    override fun onDeliverableSubmitted(projectDeliverable: NewDeliverableFile) {
        if (deliverablesCard.visibility != View.VISIBLE) {
            deliverablesCard.visibility = View.VISIBLE
            nodateTV.visibility = View.GONE
        }

        projectdeliverablervAdapter.addDeliverable(projectDeliverable)
    }

    override fun onDeliverableClicked(deliverable: NewDeliverableFile) {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(deliverable.imageUrl))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
//        activityVM.deliverable.postValue(deliverable)
//        findNavController()
//            .navigate(R.id.action_onGoingProjectForFreelancerFragment_to_fileViewerFragment)
    }

    override fun onDeleteDeliverabl(deliverable: NewDeliverableFile) {
        val action = OnGoingProjectForFreelancerFragmentDirections.actionOnGoingProjectForFreelancerFragmentToDeletefragment()
        activityVM.deliverable.postValue(deliverable)
        findNavController().navigate(R.id.action_onGoingProjectForFreelancerFragment_to_deletefragment)
    }
}