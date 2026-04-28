package sa.gov.ksaa.dal.ui.fragments.auth.signUp


import android.os.Bundle
import android.view.View
import com.google.android.material.checkbox.MaterialCheckBox
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
class NewTermsAndConditionFragment : BaseFragment(R.layout.fragment_new_terms_and_condition) {

    lateinit var termsAndConditionsCB: MaterialCheckBox
    override fun onActivityCreated() {
        super.onActivityCreated()
        appBarLayout.visibility = View.VISIBLE
    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        termsAndConditionsCB = createdView.findViewById(R.id.termsAndConditionsCB)
        activityVM.termsAndConditionAcceptanceLD.observe(viewLifecycleOwner){
            termsAndConditionsCB.isChecked = it
        }
        termsAndConditionsCB.setOnCheckedChangeListener { compoundButton, b ->
            activityVM.termsAndConditionAcceptanceLD.value = true
        }
    }
}