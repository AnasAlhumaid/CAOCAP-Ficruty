package sa.gov.ksaa.dal.ui.fragments.projects.client

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.CommentOnProject
import sa.gov.ksaa.dal.data.models.Qutation
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.adapters.ProjectCommentsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.bids.AddEditQuotationFragment
import sa.gov.ksaa.dal.ui.fragments.client.ClientProfileFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragmentDirections
import sa.gov.ksaa.dal.ui.viewModels.CommentsOnProjectVM
import sa.gov.ksaa.dal.ui.viewModels.DeliverablesVM
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM

class DetailProjectManagmentFragment: BaseFragment(R.layout.fragment_project_managment_details) {

    companion object {
        const val projectIdArgKey = "projectId"
    }

    val projectsVM: ProjectsVM by viewModels()
    val commentsOnProjectVM: CommentsOnProjectVM by viewModels()
    lateinit var userIV: ImageView
    lateinit var nameTV: TextView
    lateinit var ratingAvgTV: TextView
    lateinit var ratingBar: RatingBar
    lateinit var reviewNumberTV: TextView
    lateinit var projectTitleTV: TextView
    lateinit var pubDateTV: TextView

    //    lateinit var favorIB: ImageButton
    lateinit var backBtn: ImageView
    lateinit var expectedMilestoneTV: TextView
    lateinit var freelancerLevelTV: TextView
    lateinit var descriptionTV: TextView
    lateinit var offersCountTV: TextView
    lateinit var priceRangeTV: TextView
    lateinit var sendQuotationBtn: Button
    lateinit var commentsLblTV: TextView
    lateinit var deleteProjectBTN: Button
    lateinit var commentsRV: RecyclerView
    lateinit var projectCommentsRVadapter: ProjectCommentsRVadapter
    lateinit var serviceTypeTV: TextView
    lateinit var commentsContainerLL: LinearLayout
    lateinit var fileShow : Button
    var filesList: NewDeliverableFile? = null
    lateinit var project: NewProject
    var projectId: Int? = null


    val deliverablesVM: DeliverablesVM by viewModels()

    override fun onResume() {
        super.onResume()
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





        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        fileShow.setOnClickListener {

            onDeliverableClicked(filesList)


        }

        this@DetailProjectManagmentFragment.project = activityVM.newProjectMLD.value!!
//        if (project == null) {
//            projectId = arguments?.getInt(projectIdArgKey, 0)
//            if (projectId != 0)
//                vm.getAprojectById(projectId!!)
//                    .observe(viewLifecycleOwner) {
//                        // updateUI()
//                        handleSuccessOrErrorResponse(it, { project ->
//                            activityVM.projectMLD.value = project
//                            this@ProjectDetailsFragment.project = project
//                            updateUI()
//                        })
//                    }
//        } else {
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
        deleteProjectBTN = createdView.findViewById(R.id.deleteProjectBTN)
        commentsRV = createdView.findViewById(R.id.commentsRV)
        serviceTypeTV = createdView.findViewById(R.id.serviceTypeTV)
        commentsContainerLL = createdView.findViewById(R.id.commentsContainerLL)
        fileShow = createdView.findViewById(R.id.fileAbout)

        if (_user?.isClient() == true) {
//            favorIB.visibility = View.INVISIBLE
            sendQuotationBtn.visibility = View.GONE
        }
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
                    R.id.action_DetailProjectManagmentFragment_to_clientProfileFragment,
                    bundleOf(ClientProfileFragment.userIdKey to project.userId)
                )
        }

        Log.w(javaClass.simpleName, "updateUI: _user!!.getFullName() = ${_user?.getFullName()}")
        nameTV.text =
            _user?.getFullName()

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
            pubDateTV.text = dateFormatAr.format(it) ?: dateFormatAr.format(java.util.Date())
        }

        sendQuotationBtn.text = "تمديد عروض السعر"
        sendQuotationBtn.visibility = View.VISIBLE
        sendQuotationBtn.setOnClickListener {
            // Deleting the Project
            activityVM.newProjectMLD.postValue(project)
            val action = DetailProjectManagmentFragmentDirections.actionDetailProjectManagmentFragmentToExtendTimeForOffring()

            findNavController().safeNavigateWithArgs(action,null)

        }

        deleteProjectBTN.setOnClickListener {




            showAlertDialog(message = "هل ترغب حقا في حذف الخدمة",
                "تأكيد حذف الخدمة",
                "نعم", { dialogInterface, i ->
                    projectsVM.deleteProjectForClient(mapOf("projectId" to project.id.toString()) ).observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { objectAndComplaint ->
//                            showAlertDialog(getString(R.string.your_enquiry_is_submitted_successfully))

                            showCustomAlert("تم حذف العرض")


                        }) { errors: Errors? ->
//                            showAlertDialog(getString(R.string.your_enquiry_is_submitted_successfully))

                        }
                    }
                }, "لا", { dialogInterface, i ->
                    dialogInterface.dismiss()

                })
        }


        project.listOfCategory?.let {
            if (it.isNotEmpty()) {
                serviceTypeTV.text = project.listOfCategory!![0].proejctCategory
            }

        }


        try {
            incommingSimpleDateFormat.applyPattern("dd-MM-yyyy")
            project.deliveryTime = project.deliveryTime?.let {
                incommingSimpleDateFormat.parse(it)
                    ?.let { dateFormatAr.format(it) }
            }
        } catch (e: Exception) {
        }
        expectedMilestoneTV.text = convertArabicNumbersToString(project.deliveryTime)
            ?: project.expectedTime// numberFormat.format((project.deliveryTime?: Date).trim().toInt())
        descriptionTV.text = project.projectDescription ?: "الوصف"

        freelancerLevelTV.text =
            project.freelancerLevel ?: project.freelanceRating ?: project.ratingOfFreelancer
        val bidsCount = project.numberOfOffers ?: 0
        offersCountTV.text = numberFormat.format(bidsCount)
        priceRangeTV.text =
            numberFormat.format(((project.projectValue ?: project.amount)?.trim() ?: "0").toInt())

//        if (newProject.comments.isNullOrEmpty()){
//            commentsContainerLL.visibility = View.INVISIBLE
//        }






//        projectCommentsRVadapter.addList(project.comments)

//        Log.i(TAG, "updateUI: project.comments = ${newProject.comments}")




    }





}