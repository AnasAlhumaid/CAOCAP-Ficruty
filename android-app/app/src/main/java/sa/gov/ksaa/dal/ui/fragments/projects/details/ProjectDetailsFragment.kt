package sa.gov.ksaa.dal.ui.fragments.projects.details

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Path.Direction
import android.icu.text.RelativeDateTimeFormatter
import android.icu.util.LocaleData
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.CommentOnProject
import sa.gov.ksaa.dal.data.models.Enquiry
import sa.gov.ksaa.dal.data.models.Qutation
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewCommentOnProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ReplyTo_aComment
import sa.gov.ksaa.dal.data.webservices.newDal.responses.SpamCommint
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import sa.gov.ksaa.dal.ui.adapters.DomainsListAdapter
import sa.gov.ksaa.dal.ui.adapters.DomainsProject_Adapter
import sa.gov.ksaa.dal.ui.adapters.ProjectCommentsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.ExploreFragment
import sa.gov.ksaa.dal.ui.fragments.bids.AddEditQuotationFragment
import sa.gov.ksaa.dal.ui.fragments.client.ClientProfileFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.fragments.projects.explore.ProjectsFragment
import sa.gov.ksaa.dal.ui.viewModels.CommentsOnProjectVM
import sa.gov.ksaa.dal.ui.viewModels.DeliverablesVM
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProjectDetailsFragment : BaseFragment(R.layout.fragment_project_details),
    AddEditProjectCommentFragment.OnSubmissionListener,
    AddEditQuotationFragment.OnSendQuotationListener,
        ProjectCommentsRVadapter.OnSubmissionListener

{
    companion object {
        const val projectIdArgKey = "projectId"
    }
    val allowedLevel = mapOf(
        "نشط" to listOf("نشط", "متمرس", "متميز" , "محترف"),
        "متمرس" to listOf("متمرس", "متميز" , "محترف"),
        "متميز" to listOf("متميز" , "محترف"),
        "محترف" to listOf("محترف")
    )
    val projectsVM: ProjectsVM by viewModels()
    val commentsOnProjectVM: CommentsOnProjectVM by viewModels()

    val vm: CommentsOnProjectVM by viewModels()
    lateinit var userIV: ImageView
    lateinit var nameTV: TextView
    lateinit var ratingAvgTV: TextView
    lateinit var ratingBar: RatingBar
    lateinit var reviewNumberTV: TextView
    lateinit var projectTitleTV: TextView
    lateinit var pubDateTV: TextView
    lateinit var fileShow : Button

    //    lateinit var favorIB: ImageButton
    lateinit var backBtn: ImageView
    lateinit var expectedMilestoneTV: TextView
    lateinit var freelancerLevelTV: TextView
    lateinit var descriptionTV: TextView
    lateinit var offersCountTV: TextView
    lateinit var priceRangeTV: TextView
    lateinit var sendQuotationBtn: Button
    lateinit var commentsLblTV: TextView
    lateinit var sendEnquiryBtn: Button
    lateinit var commentsRV: RecyclerView
    lateinit var projectCommentsRVadapter: ProjectCommentsRVadapter
//    lateinit var serviceTypeTV: TextView
    lateinit var commentsContainerLL: LinearLayout

    private lateinit var domainsAdapter: DomainsProject_Adapter
    lateinit var specialityRV: RecyclerView
     var filesList: NewDeliverableFile? = null

    lateinit var project: NewProject
    var projectId: Int? = null
    val deliverablesVM: DeliverablesVM by viewModels()
    override fun onResume() {
        super.onResume()




                projectsVM.getAprojectById(mapOf("projectId" to project.id.toString()))
                    .observe(viewLifecycleOwner) {
                        // updateUI()
                        newHandleSuccessOrErrorResponse(it, { project ->
                            activityVM.newProjectMLD.value = project
                            this@ProjectDetailsFragment.project = project

                        })
                    }

        val params = mutableMapOf(
            "projectId" to project.id.toString()
        )
        Log.i(javaClass.simpleName, "updateUI: deliverablesVM.getAll($params)")
        deliverablesVM.getAll(params)
            .observe(viewLifecycleOwner) { res ->
                newHandleSuccessOrErrorResponse(res,
                    onSuccess = { file ->

                        filesList = file.firstOrNull()
                        if (filesList?.imageUrl?.endsWith(".tmp") == true){

                            fileShow.visibility = View.GONE
                        }else{
                            fileShow.visibility = View.VISIBLE
                        }
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




    }

    fun onDeliverableClicked(deliverable: NewDeliverableFile?) {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(deliverable?.imageUrl))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
//        activityVM.deliverable.postValue(deliverable)
//        findNavController()
//            .navigate(R.id.action_onGoingProjectForFreelancerFragment_to_fileViewerFragment)
    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.GONE

        initViews(createdView)
        ratingAvgTV.visibility = View.GONE



        backBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_projectDetailsFragment_to_exploreFragment)
            findNavController().popBackStack()

        }
        fileShow.setOnClickListener {

                onDeliverableClicked(filesList)


        }


        this@ProjectDetailsFragment.project = activityVM.newProjectMLD.value!!
//        if (project == null) {
//            projectId = arguments?.getInt(projectIdArgKey, 0)
//            if (projectId != 0)
//                projectsVM.getAprojectById(mapOf("projectId" to project.id.toString()))
//                    .observe(viewLifecycleOwner) {
//                        // updateUI()
//                        newHandleSuccessOrErrorResponse(it, { project ->
//                            activityVM.newProjectMLD.value = project
//                            this@ProjectDetailsFragment.project = project
//
//                        })
//                    }
//        }
//        }
//
//        else {
//            updateUI()
//        }


        updateUI()

    }




    fun showAlertDialog() {

        MaterialAlertDialogBuilder(requireContext())
            .apply {
                setMessage("بإمكانك الاستفسار عن تفاصيل المشروع الغير موضحة أعلاه، سيظهر إستفسارك للجميع، و بإمكان طالب الخدمة الرد على إستفسارك.")
                setPositiveButton(
                    "إرسال إستفسار"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
            }.create()
            .show()
    }

    private fun initViews(createdView: View) {
        userIV = createdView.findViewById(R.id.userIV)
        nameTV = createdView.findViewById(R.id.nameTV)
        ratingAvgTV = createdView.findViewById(R.id.ratingAvgTV)
        ratingBar = createdView.findViewById(R.id.ratingBar)
        reviewNumberTV = createdView.findViewById(R.id.reviewNumberTV)
        projectTitleTV = createdView.findViewById(R.id.projectTitleTV)
        pubDateTV = createdView.findViewById(R.id.pubDateTV)
//        favorIB = createdView.findViewById(R.id.favorIB)
        backBtn = createdView.findViewById(R.id.backBtn)
        expectedMilestoneTV = createdView.findViewById(R.id.expectedMilestoneTV)
        freelancerLevelTV = createdView.findViewById(R.id.freelancerLevelTV)
        descriptionTV = createdView.findViewById(R.id.descriptionTV)
        offersCountTV = createdView.findViewById(R.id.offersCountTV)
        priceRangeTV = createdView.findViewById(R.id.priceRangeTV)
        sendQuotationBtn = createdView.findViewById(R.id.sendQuotationBtn)
        commentsLblTV = createdView.findViewById(R.id.commentsLblTV)
        sendEnquiryBtn = createdView.findViewById(R.id.sendEnquiryBtn)
        commentsRV = createdView.findViewById(R.id.commentsRV)
//        serviceTypeTV = createdView.findViewById(R.id.serviceTypeTV)
        commentsContainerLL = createdView.findViewById(R.id.commentsContainerLL)
        specialityRV = createdView.findViewById(R.id.specialityRV1)
        fileShow = createdView.findViewById(R.id.fileAbout)



//        if (_user?.isClient() == true) {
////            favorIB.visibility = View.INVISIBLE
//            sendQuotationBtn.isEnabled = false
//
//
//        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun updateUI() {
        Log.w(javaClass.simpleName, "updateUI: newProject = $project")

        setProfileImage(
            project.image ?: "",
            userIV,
            NewUser.CLIENT_USER_TYPE,
            project.gender ?: _user?.gender,
            _user?.userType
        )

        userIV.setOnClickListener {
            findNavController()
                .navigate(
                    R.id.action_projectDetailsFragment_to_clientProfileFragment,
                    bundleOf(ClientProfileFragment.userIdKey to project.userId)
                )
        }

        Log.w(javaClass.simpleName, "updateUI: _user!!.getFullName() = ${_user?.getFullName()}")
        nameTV.text =
            project.clientFullName ?: (if (_user?.isClient() == true) _user!!.getFullName() else "")

        project.rating?.let {
            ratingAvgTV.text = numberFormat.format(it)
            ratingBar.rating = it.toFloat()
        }
        reviewNumberTV.text = numberFormat.format(project.reviewCount ?: 0)


        projectTitleTV.text = project.projectTitle

        project.createdDate?.let {
//            var date: Date
//            try {
//                date = Date(it.toLong())
//            } catch (e: NumberFormatException) {
//                date = SimpleDateFormat("dd-MM-yyyy")
//                    .parse(it) as Date
//            }
            pubDateTV.text = incommingSimpleDateFormatAr.format(it) ?: incommingSimpleDateFormatAr.format(java.util.Date())
//            pubDateTV.text = convertDateFormat(it.toString())
        }

//        project.proejctCategory?.let {
//            serviceTypeTV.text = it
//        }
//        project.listOfCategory?.let {
//            if (it.isNotEmpty()) {
//                serviceTypeTV.text = project.listOfCategory!![0].proejctCategory
//            }
//
//        }


        try {
            incommingSimpleDateFormat.applyPattern("dd-MM-yyyy")
            project.deliveryTime = project.deliveryTime?.let {
                incommingSimpleDateFormat.parse(it)
                    ?.let { dateFormatAr.format(it) }
            }
        } catch (e: Exception) {
        }
        expectedMilestoneTV.text = convertArabicNumbersToString(project.deliveryTime ?: "مكتمل")
            ?: project.expectedTime// numberFormat.format((project.deliveryTime?: Date).trim().toInt())
        descriptionTV.text = project.aboutProject ?: "الوصف"

        freelancerLevelTV.text =
            project.freelancerLevel ?: project.freelanceRating ?: project.ratingOfFreelancer
        val bidsCount = project.numberOfBidding ?: 0
        offersCountTV.text = numberFormat.format(bidsCount)



        val numberAm: Int = try {
            project.amount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
        } catch (e: NumberFormatException) {
            0 // Set to 0 if conversion fails
        }
        val formattedNumberAm = when (numberAm) {
            is Int, -> numberFormat.format(numberAm) // Format if it's a number
            // Use the string as is
            else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
        }


        priceRangeTV.text =
            formattedNumberAm

//        if (newProject.comments.isNullOrEmpty()){
//            commentsContainerLL.visibility = View.INVISIBLE
//        }

        if (_user == null) {
//            favorIB.isEnabled = false
            sendQuotationBtn.visibility = View.INVISIBLE
            sendEnquiryBtn.visibility = View.INVISIBLE
        } else if (_user!!.isClient()) {
//            favorIB.visibility = View.INVISIBLE
            if (_user?.userId == project.userId) {

                sendQuotationBtn.isEnabled = true

                sendQuotationBtn.text = "تمديد استقبال العروض"
                sendQuotationBtn.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4f, resources.displayMetrics)
                sendQuotationBtn.visibility = View.VISIBLE
                sendQuotationBtn.setOnClickListener {
                    // Deleting the Project
                    activityVM.newProjectMLD.postValue(project)
                    val action = ProjectDetailsFragmentDirections.actionProjectDetailsFragmentToExtendTimeForOffring()

                    findNavController().safeNavigateWithArgs(action,null)

                }

            } else {
                sendQuotationBtn.isEnabled = false
                var color = Color.GRAY
                sendQuotationBtn.setBackgroundColor(color)
            }

        } else if (_user!!.isFreelancer()) {
//            favorIB.visibility = View.VISIBLE
//            favorIB.setOnClickListener {
//
//            }
//            if (newProject.biddings?.any { bid -> bid.freelance_id == activityVM.freelancerMLD.value!!.id } == true)
//                sendQuotationBtn.visibility = View.INVISIBLE
//            else sendQuotationBtn.setOnClickListener {
//                showSendQuot(newProject.project_id!!)
//            }


                Log.w(
                    javaClass.simpleName,
                    "onBindViewHolder: freelancer is allowed to submit bit"
                )
                if (
                    allowedLevel[project.freelancerLevel?.trim()]
                        ?.contains(currentFreelancer!!.freelancerLevel?.trim())
                    == true
                ) {
                    Log.w(
                        javaClass.simpleName,
                        "onBindViewHolder: freelancer is allowed to submit bit"
                    )
                    if (project.numberOfBidding != null && project.numberOfOffer != null &&
                        project.numberOfBidding!! < project.numberOfOffer!!.toInt()
                    ) {
                        sendQuotationBtn.isEnabled = true


                        sendQuotationBtn.setOnClickListener {
                            val action = ProjectDetailsFragmentDirections.actionProjectDetailsFragmentToAddEditQuotationFragment()
                            findNavController().safeNavigateWithArgs(action,null)
                        }
                    }else{
                        sendQuotationBtn.isEnabled = false
                        var color = Color.GRAY
                        sendQuotationBtn.setBackgroundColor(color)
                    }
                } else {
                    Log.w(
                        javaClass.simpleName,
                        "onBindViewHolder: freelancer is not allowed to submit bit"
                    )
                    sendQuotationBtn.isEnabled = false
                    var color = Color.GRAY
                    sendQuotationBtn.setBackgroundColor(color)

                }



//            sendQuotationBtn.setOnClickListener {
//                activityVM.newProjectMLD.postValue(project)
//                showSendQuot(it.id!!)
//            }
            commentsContainerLL.visibility = View.VISIBLE

        }

        projectCommentsRVadapter = ProjectCommentsRVadapter(
            mutableListOf(),
            _user, project,this
        )
        commentsRV.adapter = projectCommentsRVadapter

        commentsOnProjectVM.getCommentsByProjectId(mapOf("projectId" to project.id.toString()))
            .observe(viewLifecycleOwner) { res ->
                newHandleSuccessOrErrorResponse(res, { comments ->
                    if (comments.isNotEmpty()) {
                        commentsLblTV.visibility = View.VISIBLE
                        projectCommentsRVadapter.setList(comments)
                    }
                })
            }
//        projectCommentsRVadapter.addList(project.comments)

//        Log.i(TAG, "updateUI: project.comments = ${newProject.comments}")

        if (_user == null || (_user!!.isClient() )) {
            sendEnquiryBtn.isEnabled = false
            var color = Color.GRAY
            sendEnquiryBtn.setBackgroundColor(color)
        } else {
            sendEnquiryBtn.setOnClickListener {
                findNavController().navigate(R.id.action_projectDetailsFragment_to_addEditProjectCommentFragment)
//                AddEditProjectCommentFragment(projectId!!, this, _user!!)
//                    .show(
//                        mainActivity.supportFragmentManager,
//                        AddEditProjectCommentFragment.TAG
//                    )

            }
        }
        domainsAdapter = DomainsProject_Adapter(mutableListOf())
        specialityRV.adapter = domainsAdapter

        val projectcat = project.listOfCategory

        if (projectcat != null) {
            domainsAdapter.setList(projectcat)
        }

    }
    fun showSendQuot(projectId: Int) {

        val action = ProjectDetailsFragmentDirections.actionProjectDetailsFragmentToAddEditQuotationFragment()
        findNavController().safeNavigateWithArgs(action,bundleOf(AddEditQuotationFragment.projectIdArgKey to projectId))

//        AddEditQuotationFragment(projectId, _user!!.freelancer!!, this)
//            .show(
//                mainActivity.supportFragmentManager,
//                AddEditQuotationFragment.TAG
//            )

    }



    override fun onCommentSubmitted(newComment: CommentOnProject) {
//        projectCommentsRVadapter.addComment(newComment)
    }

    override fun submitQuotation(qutation: Qutation) {
        sendQuotationBtn.isEnabled = false
        showAlertDialog(
            message = getString(R.string.your_quotation_has_been_submitted_successfully),
            positiveText = getString(R.string.ok),
            positiveOnClick = DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            }
        )
    }

    override fun onRCommentSubmitted(newRComment: ReplyTo_aComment) {

        vm.replayComment(mapOf("userId" to _user!!.userId.toString(),
            "commentId" to newRComment.commentId.toString(), "projectId" to newRComment.projectid.toString(),"comments" to newRComment.replyComment.toString()))
            .observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, { newComment ->
                    Snackbar.make(requireView(), "تم ارسال الاستفسار بنجاح", Snackbar.LENGTH_SHORT)
                        .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>(){

                        })
                        .show()
                }) {errors: Errors? ->
                    errors?.errorsList?.forEach { error ->
                        when (error?.path) {
                            Enquiry::message.name -> {

                            }
                            Enquiry::name.name, Enquiry::email.name, Enquiry::user_id.name -> {
                                Snackbar.make(requireView(), "الرجاء تسجيل الدخول و المحاولة مرة اخرى", Snackbar.LENGTH_SHORT).show()
                            }
                            else -> {
                                Snackbar.make(requireView(), R.string.something_went_wrong_please_try_again_later, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
    }

    override fun onSpamClicked(comment: NewCommentOnProject?, commentId: Int?,reportTxt:String?) {


        val action = ProjectDetailsFragmentDirections.actionProjectDetailsFragmentToSpamDialog()
        val postComment = SpamCommint()

          if (comment != null){
              postComment.id = comment.id
              postComment.report = "اخرى"
              activityVM.postReportCommint.postValue(postComment)
              SpamDialog().show(parentFragmentManager,SpamDialog.tag)
             }else{
              postComment.id = commentId
              postComment.report = reportTxt
              activityVM.postReportCommint.postValue(postComment)
              SpamDialog().show(parentFragmentManager,SpamDialog.tag)
          }

    }


}
