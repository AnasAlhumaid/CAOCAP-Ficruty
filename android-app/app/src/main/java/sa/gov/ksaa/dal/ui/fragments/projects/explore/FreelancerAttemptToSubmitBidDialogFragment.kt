package sa.gov.ksaa.dal.ui.fragments.projects.explore

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

class FreelancerAttemptToSubmitBidDialogFragment :
    BaseMaterialDialogFragment(R.layout.fragment_dialog_not_acceptable_level) {

    companion object {
        const val tag = "BidAcceptanceDialogFragment"
    }

//    lateinit var bid: NewBid


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        val backOnClickListener = View.OnClickListener{ v: View->
            dismiss()
        }
        submitBtn.setOnClickListener(backOnClickListener)
        cancelBtn.setOnClickListener (backOnClickListener)

    }

    lateinit var cancelBtn: ImageView
    lateinit var submitBtn: MaterialButton
    override fun initViews(createdView: View) {
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
    }

}