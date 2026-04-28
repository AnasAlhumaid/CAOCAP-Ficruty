package sa.gov.ksaa.dal.ui.fragments.auth.signUp

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.UserVM

class SelectingAccountTypeRegistrationFragment : BaseFragment(R.layout.fragment_sign_up_2_account_type), OnClickListener{
    val vm: UserVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<CardView>(R.id.beneficiaryCard).setOnClickListener(this)
        view.findViewById<CardView>(R.id.freelanceCard).setOnClickListener(this)
    }

    override fun onClick(clickedView: View?) {
        when(clickedView!!.id){
            R.id.beneficiaryCard -> {
                val user = activityVM.userMLD.value
                user!!.userType = NewUser.INDIVIDUAL_CLIENT_USER_TYPE
                activityVM.userMLD.value = user
                findNavController().navigate(R.id.action_selectingAccountTypeFragment_to_clientRegistrationFragment)
            }
            R.id.freelanceCard -> {
                val user = activityVM.userMLD.value
                user!!.userType = NewUser.FREELANCER_USER_TYPE
                activityVM.userMLD.value = user
                findNavController().navigate(R.id.action_selectingAccountTypeFragment_to_freelancerRegistrationFragment1)
            }
        }
    }
}