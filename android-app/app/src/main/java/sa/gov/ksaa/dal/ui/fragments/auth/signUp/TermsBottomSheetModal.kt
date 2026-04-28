package sa.gov.ksaa.dal.ui.fragments.auth.signUp

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import sa.gov.ksaa.dal.R

class TermsBottomSheetModal(val listener: OnTermsConfirmedListener) : BottomSheetDialogFragment(R.layout.modal_bottom_sheet_terms_and_conditions){

    lateinit var termsNconditionsCB2: CheckBox
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        termsNconditionsCB2 = createdView.findViewById(R.id.termsNconditionsCB2)

        termsNconditionsCB2.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            run {
                listener.setConfirmed(termsNconditionsCB2.isChecked)
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        const val TAG = "TermsAndConditions"
    }

    interface OnTermsConfirmedListener{
        fun setConfirmed(b: Boolean)
    }
}