package sa.gov.ksaa.dal.ui.fragments.projects.client.ongoing


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.ui.adapters.ProjectDeliverableRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancerProfileFragment
import sa.gov.ksaa.dal.ui.fragments.projects.freelancer.ongoing.AddDeliverableToAnOngoingProjectByFreelancer
import sa.gov.ksaa.dal.ui.viewModels.DeliverablesVM
import sa.gov.ksaa.dal.ui.viewModels.OngoingProjectsVM
import java.util.Date

class OngoingProjectForClientFragment : BaseFragment(R.layout.fragment_project_ongoing_for_client),
    AddDeliverableToAnOngoingProjectByFreelancer.OnSubmitLister,
    ProjectDeliverableRV_Adapter.OnClickListener {

    companion object {
        const val onGgoinProjectIdKey = "onGgoinProjectId"
    }

    val ongoingProjectsVM: OngoingProjectsVM by viewModels()

    //    val chatingVM: ChatingVM by viewModels()
    val deliverablesVM: DeliverablesVM by viewModels()

    //    lateinit var chatmessagesrvAdapter: ChatMessagesRV_Adapter
    var onGgoinProjectId: Int? = 0
    lateinit var projectUnderway: ProjectUnderway
    private lateinit var projectdeliverablervAdapter: ProjectDeliverableRV_Adapter


    lateinit var filesList: List<NewDeliverableFile>



    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        bottomNavigationView.visibility = View.VISIBLE
        appBarLayout.visibility = View.VISIBLE

        initViews(createdView)

//        chatmessagesrvAdapter = ChatMessagesRV_Adapter(mutableListOf(), _user!!)
//        messagesRV.adapter = chatmessagesrvAdapter

        projectdeliverablervAdapter = ProjectDeliverableRV_Adapter(mutableListOf(), this,_user)
        deliverableRV.adapter = projectdeliverablervAdapter

        activityVM.ongoingProjectLD.observe(viewLifecycleOwner, Observer {
            projectUnderway = it!!
            Log.w(javaClass.simpleName, "onViewCreated: onGgoinProject = $projectUnderway")
            updateUI()
        })


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

        projectTitleTV.text = projectUnderway.projectTitle
        startDateTV.text = dateFormatAr.format(projectUnderway.startDate ?: Date())
        val remainingDate: Int = try {
            projectUnderway.durationOfProject?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
        } catch (e: NumberFormatException) {
            0 // Set to 0 if conversion fails
        }
        val days = when (remainingDate) {
            is Int, -> numberFormat.format(remainingDate) // Format if it's a number
            // Use the string as is
            else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
        }

        remainingDaysTV.text = days

        val cost: Int = try {
            projectUnderway.amount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
        } catch (e: NumberFormatException) {
            0 // Set to 0 if conversion fails
        }
        val formattedCost = when (cost) {
            is Int, -> numberFormat.format(cost) // Format if it's a number
            // Use the string as is
            else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
        }
        costTV.text = (formattedCost ?: projectUnderway.freelancerBidAmount).toString()
//        paymentStatusTV.text = "status"

        setOtherUserImage(projectUnderway.image, freelancerIV, projectUnderway.userType!!, projectUnderway.gender)

        freelancerIV.setOnClickListener {
            findNavController()
                .navigate(R.id.action_onGoingProjectForClientFragment_to_freelancerProfileFragment,
                    bundleOf(FreelancerProfileFragment.freelancerUserIdKey to projectUnderway.clientUserId)
                )
        }

        freelancerNameTV.text = "${projectUnderway.freelancerName} ${projectUnderway.freelancerLastName}"

        // userId=1
//        chatingVM.getChatMessagesByUserId(mapOf(
//            "userId" to _user!!.userId.toString()
//        )).observe(viewLifecycleOwner){res ->
//            newHandleSuccessOrErrorResponse(res, { messagesList ->
//                chatmessagesrvAdapter.setList(
//                    messagesList.filter { newChatMessage ->
//                        newChatMessage.projectId == projectUnderway.projectId
//                    })
//            })
//        }

        // projectId
        val params = mutableMapOf(
            "projectId" to projectUnderway.projectId.toString()
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
                    }
//                    {
//                    if (it.isEmpty()) {
//                        nodateTV.visibility = View.VISIBLE
//                        deliverablesCard.visibility = View.GONE
//                    } else {
//                        projectdeliverablervAdapter.setList(it)
//                    }
//                }

                )
            }




        addDeliverableBtn.setOnClickListener { _ ->
            AddDeliverableToAnOngoingProjectByFreelancer(onGgoinProjectId!!, this)
                .show(
                    mainActivity.supportFragmentManager,
                    AddDeliverableToAnOngoingProjectByFreelancer.tag
                )
        }

//        sendMessageFab.setOnClickListener {
//            if(isValidMessage()){
//                val params = mapOf(
//                    "chat" to chatMessage.line_text!!,
//                    "sendById" to _user!!.userId!!.toString(),
//                    "sendToId" to projectUnderway.userId!!.toString(),
//                    "projectId" to projectUnderway.projectId!!.toString()
//                )
//                Log.w(javaClass.simpleName, "updateUI: params = $params")
//                // chat?chat=مرحبا&sendById=5&sendToId=1&projectId=55
//                chatingVM.create_aChatMessage(params).observe(viewLifecycleOwner){res ->
//                    newHandleSuccessOrErrorResponse(res, {message ->
//                        chatmessagesrvAdapter.addMessage(message)
//                        messageET.text.clear()
//                    })
//                }
//            }
//        }
        projectTS_Btn.setOnClickListener {
            findNavController().navigate(R.id.action_onGoingProjectForClientFragment_to_technicalSupportTicketsFragment)
        }

        if (projectUnderway.isClosed) {
            closeProjectBtn.isEnabled = false
            if (!projectUnderway.isReviewed) {
                val postRatingAndReview = RatingAndReview()
                postRatingAndReview.projectId = projectUnderway.projectId
                postRatingAndReview.clientUserId = _user!!.userId
                postRatingAndReview.freelancerUserId = projectUnderway!!.freelancerUserId

                activityVM.postRatingAndReview.postValue(postRatingAndReview)
                findNavController().navigate(R.id.action_onGoingProjectForClientFragment_to_addEditReviewDialogFragment)
            }
        } else {
            closeProjectBtn.setOnClickListener { _ ->

                activityVM.ongoingProjectLD.value = projectUnderway
                findNavController().navigate(R.id.action_onGoingProjectForClientFragment_to_closing_aProjectDialogFfragment)


            }
        }

        liveCatBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_onGoingProjectForClientFragment_to_projectLiveChatFragment)
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
    lateinit var freelancerIV: ImageView
    lateinit var freelancerNameTV: TextView

    //    lateinit var messagesRV: RecyclerView
//    lateinit var messageET: EditText
//    lateinit var sendMessageFab: FloatingActionButton
    lateinit var projectTS_Btn: Button
    lateinit var closeProjectBtn: Button
    lateinit var deliverableRV: RecyclerView
    lateinit var addDeliverableBtn: Button
    lateinit var liveCatBtn: Button

    private fun initViews(createdView: View) {
        projectTitleTV = createdView.findViewById(R.id.projectTitleTV)
        startDateTV = createdView.findViewById(R.id.startDateTV)
        remainingDaysTV = createdView.findViewById(R.id.remainingDaysTV)
        costTV = createdView.findViewById(R.id.costTV)
        paymentStatusTV = createdView.findViewById(R.id.paymentStatusTV)
        freelancerIV = createdView.findViewById(R.id.freelancerIV)
        freelancerNameTV = createdView.findViewById(R.id.freelancerNameTV)
//        messagesRV = createdView.findViewById(R.id.messagesRV)
//        messageET = createdView.findViewById(R.id.messageET)
//        sendMessageFab = createdView.findViewById(R.id.sendMessageFab)
        projectTS_Btn = createdView.findViewById(R.id.projectTS_Btn)
        closeProjectBtn = createdView.findViewById(R.id.closeProjectBtn)
        deliverableRV = createdView.findViewById(R.id.deliverableRV)
        deliverablesCard = createdView.findViewById(R.id.deliverablesCard)
        nodateTV = createdView.findViewById(R.id.nodateTV)
        addDeliverableBtn = createdView.findViewById(R.id.addDeliverableBtn)
        liveCatBtn = createdView.findViewById(R.id.liveCatBtn)

    }

    lateinit var deliverablesCard: CardView
    lateinit var nodateTV: TextView
    override fun onDeliverableSubmitted(projectDeliverable: NewDeliverableFile) {
        if (deliverablesCard.visibility != View.VISIBLE) {
            deliverablesCard.visibility = View.VISIBLE
            nodateTV.visibility = View.GONE
        }

        projectdeliverablervAdapter.addDeliverable(projectDeliverable)
    }

    override fun onDeliverableClicked(deliverable: NewDeliverableFile) {
        if (deliverable.imageUrl != null && URLUtil.isValidUrl(deliverable.imageUrl) &&
            Patterns.WEB_URL.matcher(deliverable.imageUrl).matches()){
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(deliverable.imageUrl))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
        }

//        activityVM.deliverable.postValue(deliverable)
//        findNavController()
//            .navigate(R.id.action_onGoingProjectForClientFragment_to_fileViewerFragment)
    }
    override fun onDeleteDeliverabl(deliverable: NewDeliverableFile) {
        activityVM.deliverable.postValue(deliverable)
        findNavController().navigate(R.id.action_onGoingProjectForClientFragment_to_deletefragment)
    }
}