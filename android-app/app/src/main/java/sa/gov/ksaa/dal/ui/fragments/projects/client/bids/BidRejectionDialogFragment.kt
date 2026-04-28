package sa.gov.ksaa.dal.ui.fragments.projects.client.bids

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.BidsVM

class BidRejectionDialogFragment : BaseMaterialDialogFragment(R.layout.fragment_dialog_bid_rejection_confirmation){

    companion object {
        const val tag = "BidRejectionDialogFragment"
    }

    val vm: BidsVM by viewModels()

    var bid: NewBid? = null

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        bid = activityVM.bidMLD.value

        cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        submitBtn.setOnClickListener {
            bid?.let {
//                it.is_rejected = true
                reject_theBid(it)
            }
        }
    }
    lateinit var cancelBtn: ImageView
    lateinit var submitBtn: MaterialButton
    override fun initViews(createdView: View) {
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
    }

    private fun reject_theBid(bid: NewBid){
        // bidId=16
        vm.reject_aBid(mapOf<String, String>("bidId" to bid.bidId.toString()))
            .observe(viewLifecycleOwner){
                newHandleSuccessOrErrorResponse(it, {
                    Snackbar.make(requireView(), "تم رفض العرض", Snackbar.LENGTH_SHORT)
                        .show()

                    findNavController().popBackStack(R.id.projectsBidsCientFragment, false)
                })
            }
    }

}