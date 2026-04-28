package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.invitations

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.BiddingInvitation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.bids.AddEditQuotationFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancerProfileFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.fragments.projects.client.bids.ProjectQuotationDetailsForClientFragmentDirections
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class InvitationFragment : BaseFragment(R.layout.fragment_project_invitation) {
    companion object {
        const val projectInvitationIdArgKey = "projectInvitationId"
    }

    //    val vm: ProjectDetailsVM by viewModels()
    lateinit var backBtn: ImageView
    lateinit var userIV: ImageView
    lateinit var nameTV: TextView
    lateinit var avgRatingTV: TextView
    lateinit var avgRatingBar: RatingBar
    lateinit var ratingCountTV: TextView
    lateinit var projectTitleTV: TextView
    lateinit var pubDateTV: TextView

    //    lateinit var favorIB: ImageButton
    lateinit var expectedMilestoneTV: TextView
    lateinit var serviceDomainTV: TextView
    lateinit var freelancerLevelTV: TextView
    lateinit var descriptionTV: TextView
    lateinit var priceRangeTV: TextView
    lateinit var sendQuotationBtn: Button
    lateinit var rejectInvitationBtn: Button
    lateinit var invitation: BiddingInvitation



    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.GONE
        initViews(createdView)
        userIV.setOnClickListener {
            val  action = InvitationFragmentDirections.actionInvitationFragmentToClientProfileFragment()
            findNavController().safeNavigateWithArgs(action, bundleOf(FreelancerProfileFragment.freelancerUserIdKey to invitation.userId))

        }

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
//        projectId = arguments?.getInt(projectInvitationIdArgKey, 0)
//        if (projectId != 0)
//            vm.getAprojectById(projectId!!)
//                .observe(viewLifecycleOwner) {
//                    // updateUI()
//                    handleSuccessOrErrorResponse(it, { project ->
//                        activityVM.projectMLD.value = project
//                        this@InvitationFragment.project = project
//                        updateUI()
//                    })
//                }

        invitation = activityVM.projecInvitationtMLD.value!!

        updateUI()
    }

    fun showSendQuot(projectId: Int) {
        findNavController().navigate(
            R.id.action_invitationFragment_to_addEditQuotationFragment,
            bundleOf(AddEditQuotationFragment.projectIdArgKey to projectId)
        )

//        AddEditQuotationFragment(projectId, _user!!.freelancer!!, this)
//            .show(
//                mainActivity.supportFragmentManager,
//                AddEditQuotationFragment.TAG
//            )

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun updateUI() {
        invitation?.let {

            setProfileImage(it.image, userIV, NewUser.CLIENT_USER_TYPE, it.gender)
            nameTV.text = "${it.clientName} ${it.clientLastName}"
            projectTitleTV.text = it.projectTitle
            pubDateTV.text = it.createdDate?.let { dateFormatAr.format(it) }
            incommingSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            try {
                expectedMilestoneTV.text = dateFormatAr.format(incommingSimpleDateFormat.parse(it.durationOfProject))
            } catch (e: ParseException) {
                expectedMilestoneTV.text = it.durationOfProject
            }

            if (!it.listOfCategory.isNullOrEmpty() ){
                serviceDomainTV.text = it.listOfCategory.first().proejctCategory ?: ""
            }else{
                serviceDomainTV.text = ""
            }


            freelancerLevelTV.text = it.freelancerLevel ?: "محترف"
            descriptionTV.text = it.projectDescription
            priceRangeTV.setText(numberFormat.format(it.amount?.trim()?.toInt()))

//            if (it.biddings?.any { bid -> bid.freelance_id == activityVM.freelancerMLD.value!!.id } == true)
//                sendQuotationBtn.isEnabled = false
//            else sendQuotationBtn.setOnClickListener {view ->
//                activityVM.newProjectMLD.value = it
//                showSendQuot(it.project_id!!)
//            }

            sendQuotationBtn.setOnClickListener { view ->
                activityVM.newProjectMLD.postValue(it.toProject())
                showSendQuot(it.id!!)
            }

        }
        if (_user == null) {
            rejectInvitationBtn.isEnabled = false
        } else {
            rejectInvitationBtn.setOnClickListener { view ->
                activityVM.projecInvitationtMLD.value = invitation
                findNavController().navigate(R.id.action_invitationFragment_to_invitationRejectionDialogFragment)
//                AddEditProjectCommentFragment(projectId!!, this, _user!!)
//                    .show(
//                        mainActivity.supportFragmentManager,
//                        AddEditProjectCommentFragment.TAG
//                    )

            }
        }


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
        backBtn = createdView.findViewById(R.id.backBtn)
        userIV = createdView.findViewById(R.id.userIV)
        nameTV = createdView.findViewById(R.id.nameTV)
        avgRatingTV = createdView.findViewById(R.id.ratingAvgTV)
        avgRatingBar = createdView.findViewById(R.id.ratingBar)
        ratingCountTV = createdView.findViewById(R.id.reviewNumberTV)
        projectTitleTV = createdView.findViewById(R.id.projectTitleTV)
        pubDateTV = createdView.findViewById(R.id.pubDateTV)
//        favorIB = createdView.findViewById(R.id.favorIB)
        serviceDomainTV = createdView.findViewById(R.id.serviceDomainTV)
        expectedMilestoneTV = createdView.findViewById(R.id.expectedMilestoneTV)
        freelancerLevelTV = createdView.findViewById(R.id.freelancerLevelTV)
        descriptionTV = createdView.findViewById(R.id.descriptionTV)
        priceRangeTV = createdView.findViewById(R.id.priceRangeTV)
        sendQuotationBtn = createdView.findViewById(R.id.sendQuotationBtn)
        rejectInvitationBtn = createdView.findViewById(R.id.rejectInvitationBtn)

        if (_user!!.isClient()) {
//            favorIB.visibility = View.INVISIBLE
            sendQuotationBtn.visibility = View.INVISIBLE
        }
    }

//    override fun onCommentSubmitted(newComment: CommentOnProject) {
////        projectCommentsRVadapter.addComment(newComment)
//    }
//
//    override fun submitQuotation(qutation: Qutation) {
//        sendQuotationBtn.isEnabled = false
//        showAlertDialog(
//            message = getString(R.string.your_quotation_has_been_submitted_successfully),
//            positiveText = getString(R.string.ok),
//            positiveOnClick = DialogInterface.OnClickListener { dialogInterface, i ->
//                dialogInterface.dismiss()
//            }
//        )
//    }


}