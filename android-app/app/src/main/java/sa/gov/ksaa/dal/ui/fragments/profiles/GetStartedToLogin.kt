package sa.gov.ksaa.dal.ui.fragments.profiles

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.ui.fragments.BaseFragment

class GetStartedToLogin: BaseFragment(R.layout.start_to_login_fragment) {
    lateinit var getStartBTN: Button
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        getStartBTN = createdView.findViewById(R.id.buttonStartLogin)
        getStartBTN.setOnClickListener{
            val action = GetStartedToLoginDirections.actionGetStartedToLoginToUserLoginFragment()
            Log.i(javaClass.simpleName, "updateUI: user = ----")
            findNavController().navigate(R.id.action_getStartedToLogin_to_userLoginFragment)
//            Intent(requireContext().applicationContext,UserLoginFragment::class.java)
        }
    }
    fun NavController.safeNavigateWithArgs(direction: NavDirections, bundle: Bundle?) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction.actionId, bundle)
        }
    }
    }
