package sa.gov.ksaa.dal.ui.fragments.projects.client.bids

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.adapters.DomainsProject_Adapter
import sa.gov.ksaa.dal.ui.adapters.RV_Adapter.Companion.dateFormat
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancerProfileFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.viewModels.BidsVM
import java.util.Date

class ProjectQuotationDetailsForClientFragment : BaseFragment(R.layout.fragment_project_quotation_details_for_client) {
    companion object {
        const val bidIdArgKey = "bidId"
    }

    val vm: BidsVM by viewModels()

    lateinit var bid: NewBid

//    var projectId: Int? = null

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.GONE

        initViews(createdView)

//        projectId = activityVM.projectMLD.value!!.project_id
//        val bidId = arguments?.getInt(bidIdArgKey, 0)

        //        if ( bidId != null && bidId!= 0)
//            vm.getBidById(bidId!!)
//                .observe(viewLifecycleOwner) {
//                    handleSuccessOrErrorResponse(it, { bid ->
////                        _project = bid.project
//                        updateUI(bid)
//                    })
//                }
        bid = activityVM.bidMLD.value!!
        updateUI(bid)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(bid: NewBid) {
        freelancerLevelTV.text = bid.freelancerLevel?: "متمرس"

        setProfileImage(bid.image, userIV , NewUser.FREELANCER_USER_TYPE, bid.gender)

        nameTV.text = "${bid.freelancerName} ${bid.freelancerLastName}"
        titleTV.text = bid.projectTitle
        pubDateTV.text =     simpleDateFormatEn.parse(bid.createdDate?: simpleDateFormatEn.format(Date()))
            ?.let { dateFormatAr.format(it) }


//        domainTV.text = bid.listOfServices!![0].typeOfServices
        expectedMilestoneTV.text = numberFormat.format(bid.freelancerExpectedTime?.trim()?.toInt() ?: 0)

        reviewNumberTV.text = numberFormat.format(0)
        bid.rating?.let {

            ratingBar.rating = it.toFloat()
        }
        val numberAm: Int = try {
            bid.freelancerBiddingAmount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
        } catch (e: NumberFormatException) {
            0 // Set to 0 if conversion fails
        }
        val formattedNumberAm = when (numberAm) {
            is Int, -> numberFormat.format(numberAm) // Format if it's a number
            // Use the string as is
            else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
        }
        costTV.text = formattedNumberAm
        outputsTV.text = bid.freelancerOutputExpected
        aboutTV.text = bid.freelancerProjectDesc

        if (bid.attachmentUrl!!.contains(".tmp") && bid.attachmentUrl.isEmpty()){
            attachmentsBtn.visibility  = View.GONE
        }else{
            attachmentsBtn.setOnClickListener {

                val browserIntent = Intent(Intent.ACTION_VIEW,Uri.parse(bid.attachmentUrl))
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(browserIntent)
            }
        }


        userIV.setOnClickListener {
            val  action = ProjectQuotationDetailsForClientFragmentDirections.actionProjectQuotationDetailsForClientFragmentToFreelancerProfileFragment()
            findNavController().safeNavigateWithArgs(action, bundleOf(FreelancerProfileFragment.freelancerUserIdKey to bid.freelancerUserId))

        }

        acceptBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectQuotationDetailsForClientFragment_to_bidAcceptanceDialogFragment)
//                showAlertDialog(message = "تأكيد قبول العرض المقدم من "+(bid.freelancerName?:"مقدم الخدمة"),
//                    "تأكيد قبول العرض",
//                    "قبول العرض", { dialogInterface, i ->
//
////                        bid.is_accepted = true
//                        accept_theBid()
//                        dialogInterface.dismiss()
////                        navigateToOngoingProjects()
//                    }, "إلغاء", { dialogInterface, i ->
//                        dialogInterface.dismiss()
//                    })
        }
        rejectBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectQuotationDetailsForClientFragment_to_bidRejectionDialogFragment)

//                showAlertDialog(message = "تأكيد رفض العرض المقدم من "+bid.freelancer?.getFullName(),
//                    "تأكيد رفض العرض",
//                    "رفض العرض", { dialogInterface, i ->
//                        bid.is_accepted = false
//                        sendUpdateRequest(bid)
//                        dialogInterface.dismiss()
//                        navigateBack()
//                    }, "إلغاء", { dialogInterface, i ->
//                        dialogInterface.dismiss()
//
//                    })
        }

//        if (bid.is_accepted == null){
//
//        } else {
//            acceptBtn.isEnabled = false
//            rejectBtn.isEnabled = false
//        }


        domainsAdapter = DomainsProject_Adapter(mutableListOf())
        specialityRV.adapter = domainsAdapter

        if (bid.listOfServices?.isEmpty() == false){
            val projectcat = bid.listOfServices
            domainsAdapter.setList(projectcat)
        }



    }

//    private fun navigateToOngoingProjects() {
//        findNavController().navigate(R.id.action_projectQuotationDetailsForClientFragment_to_ongoingProjectsClientFragment)
//    }

    private fun accept_theBid(){


        // projectId=88&bidId=31
        vm.accepting_aBid(mapOf("projectId" to bid.projectId.toString(),
            "bidId" to (bid.id?: bid.bidId).toString()))
            .observe(viewLifecycleOwner){
                newHandleSuccessOrErrorResponse(it, {
                    updateUI(bid)
                    Snackbar.make(requireView(), "تم قبول العرض بنجاح", Snackbar.LENGTH_SHORT)
                        .show()
                })
            }
    }

    private fun initViews(createdView: View) {
        freelancerLevelTV = createdView.findViewById(R.id.freelancerLevelTV)
//        abshirCrad = createdView.findViewById(R.id.projects_LL)
        userIV = createdView.findViewById(R.id.userIV)
        nameTV = createdView.findViewById(R.id.nameTV)
//        ratingAvgTV = createdView.findViewById(R.id.ratingAvgTV)
        ratingBar = createdView.findViewById(R.id.ratingBar)
        titleTV = createdView.findViewById(R.id.titleTV)
        pubDateTV = createdView.findViewById(R.id.pubDateTV)
        reviewNumberTV = createdView.findViewById(R.id.reviewNumberTV)
//        domainTV = createdView.findViewById(R.id.domainTV)
        expectedMilestoneTV = createdView.findViewById(R.id.expectedMilestoneTV)
        outputsTV = createdView.findViewById(R.id.outputsTV)
        aboutTV = createdView.findViewById(R.id.aboutTV)
        costTV = createdView.findViewById(R.id.costTV)
        acceptBtn = createdView.findViewById(R.id.acceptBtn)
        rejectBtn = createdView.findViewById(R.id.rejectBtn)
        backBtn = createdView.findViewById(R.id.backBtn)
        attachmentsBtn = createdView.findViewById(R.id.attachmentsBtn)
        specialityRV = createdView.findViewById(R.id.specialityRV1)
    }

    lateinit var freelancerLevelTV: TextView
//    lateinit var abshirCrad: LinearLayout
    lateinit var userIV: ImageView
    lateinit var nameTV: TextView
//    lateinit var ratingAvgTV: TextView
    lateinit var ratingBar: RatingBar
    lateinit var reviewNumberTV: TextView
    lateinit var titleTV: TextView
    lateinit var pubDateTV: TextView
//    lateinit var domainTV: TextView
    lateinit var expectedMilestoneTV: TextView
    lateinit var outputsTV: TextView
    lateinit var aboutTV: TextView
    lateinit var costTV: TextView
    lateinit var acceptBtn: Button
    lateinit var rejectBtn: Button
    lateinit var backBtn: ImageView
    lateinit var attachmentsBtn: Button
    private lateinit var domainsAdapter: DomainsProject_Adapter
    lateinit var specialityRV: RecyclerView
}