package sa.gov.ksaa.dal.ui.fragments.projects.client.bids

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.BidsVM

class BidAcceptanceDialogFragment :
    BaseMaterialDialogFragment(R.layout.fragment_dialog_bid_acceptance_confirmation) {

    companion object {
        const val tag = "BidAcceptanceDialogFragment"
    }

    val vm: BidsVM by viewModels()

    lateinit var bid: NewBid

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        bid = activityVM.bidMLD.value!!
        Log.w(javaClass.simpleName, "onViewCreated: bid = $bid")
        freelancerNameTV.text = bid.freelancerName
        submitBtn.setOnClickListener {
//                newBid.is_accepted = true
            accept_theBid(bid)
        }

        cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    lateinit var cancelBtn: ImageView
    lateinit var submitBtn: MaterialButton
    lateinit var freelancerNameTV: TextView
    override fun initViews(createdView: View) {
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
        freelancerNameTV = createdView.findViewById(R.id.freelancerNameTV)
    }

    private fun accept_theBid(bid: NewBid) {
        // acceptFreeancerBidByClient?projectId=9&bidId=13
        vm.accepting_aBid(
            mapOf(
                "projectId" to bid.projectId!!.toString(),
            "bidId" to (bid.id?:bid.bidId!!).toString()
            )
        ).observe(viewLifecycleOwner) {
            newHandleSuccessOrErrorResponse(it, {
                var message = "تم قبول العرض بنجاح"

//                    if (it.is_accepted == true){
//                        message = "تم قبول العرض بنجاح"
//                    } else {
//                        message = "تم رفض العرض"
//                    }

                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            findNavController().popBackStack(R.id.projectsBidsCientFragment, false)
                        }
                    })
                    .show()
            })
        }
    }

}