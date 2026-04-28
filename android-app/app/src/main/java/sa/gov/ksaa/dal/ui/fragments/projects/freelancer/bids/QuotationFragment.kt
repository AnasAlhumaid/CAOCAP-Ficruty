package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.bids

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.adapters.DomainsProject_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.BidsVM
import java.util.Date

class QuotationFragment : BaseFragment(R.layout.fragment_quotation) {
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

    private fun updateUI(bid: NewBid) {
        freelancerLevelTV.text = currentFreelancer?.freelancerLevel
//        nameTV.text = bid.freelancerName?: "مقدم خدمة"
        titleTV.text = bid.projectTitle
        pubDateTV.text = bid.createdDate?.let { simpleDateFormatEn.parse(it) }
            ?.let { dateFormatAr.format(it) }

        expectedMilestoneTV.text = bid.expectedTime?.trim()?.toInt()?.let { numberFormat.format(it) }
        costTV.text = bid.freelancerBiddingAmount?.trim()?.toInt()?.let { numberFormat.format(it) }
        outputsTV.text = bid.outputExpected
        aboutTV.text = bid.aboutProject
        reviewNumberTV.text = numberFormat.format(currentFreelancer!!.reviewCount)
        setProfileImage(currentFreelancer?.image, userIV , NewUser.FREELANCER_USER_TYPE, bid.gender)
        nameTV.text = "${bid.freelancerName} ${bid.freelancerLastName}"
        ratingBar.rating = currentFreelancer!!.rating!!
//        bid.listOfCategory?.let {
//            if (it.isNotEmpty()) {
//                domainTV.text = bid.listOfCategory!![0].proejctCategory
//            }else{
//                domainTV.text = bid.proejctCategory
//            }
//
//        }

        if (bid.fileUrl == null){
            attachmentsBtn.visibility = View.GONE
        } else {
            attachmentsBtn.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(bid.fileUrl))
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
            }
        }

        rejectBtn.setOnClickListener {

                showAlertDialog(message = "هل ترغب حقا في حذف العرض",
                    "تأكيد حذف العرض",
                    "نعم", { dialogInterface, i ->
                       vm.deleteBidForFreelancer(mapOf("biddingId" to bid.biddingId.toString() )) .observe(viewLifecycleOwner) {
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

        domainsAdapter = DomainsProject_Adapter(mutableListOf())
        specialityRV.adapter = domainsAdapter

        val projectcat = bid.listOfCategory!!

        domainsAdapter.setList(projectcat)
    }



    private fun initViews(createdView: View) {
        freelancerLevelTV = createdView.findViewById(R.id.freelancerLevelTV)
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
        attachmentsBtn = createdView.findViewById(R.id.attachmentsBtn)
        rejectBtn = createdView.findViewById(R.id.rejectBtn)
        backBtn = createdView.findViewById(R.id.backBtn)
        specialityRV = createdView.findViewById(R.id.specialityRV1)
        userIV = createdView.findViewById(R.id.userIV)
    }
    lateinit var userIV: ImageView
    lateinit var freelancerLevelTV: TextView
    lateinit var nameTV: TextView
    lateinit var ratingAvgTV: TextView
    lateinit var ratingBar: RatingBar
    lateinit var reviewNumberTV: TextView
    lateinit var titleTV: TextView
    lateinit var pubDateTV: TextView
//    lateinit var domainTV: TextView
    lateinit var expectedMilestoneTV: TextView
    lateinit var outputsTV: TextView
    lateinit var aboutTV: TextView
    lateinit var costTV: TextView
    lateinit var attachmentsBtn: MaterialButton
    lateinit var rejectBtn: Button
    lateinit var backBtn: ImageView
private lateinit var domainsAdapter: DomainsProject_Adapter
    lateinit var specialityRV: RecyclerView
}