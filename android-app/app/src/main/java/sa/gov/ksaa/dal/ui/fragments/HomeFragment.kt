package sa.gov.ksaa.dal.ui.fragments

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.ui.viewModels.BaseVM
import kotlin.system.exitProcess

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

//        isFirstRun = mainActivity.getPreferences(Context.MODE_PRIVATE)
//            ?.getBoolean("isFirstRun", true)
//        Log.i(TAG, "onViewCreated: isFirstRun = $isFirstRun")
//        if (isFirstRun == null || isFirstRun == true )
//            findNavController().navigate(R.id.action_homeFragment_to_viewPagerFragment2)
//        else {
//            activityVM.userMLD.observe(viewLifecycleOwner) {
//                mainActivity.bottomNavigationView.visibility = View.VISIBLE
//                if (it == null) {
//                    mainActivity.bottomNavigationView.visibility = View.GONE
//                    findNavController().navigate(R.id.action_homeFragment_to_userLoginFragment)
//                }
//            }
//        }

        // This callback is only called when MyFragment is at least started
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner,onBackPressedCallback)
    }

    override fun onActivityCreated() {
        super.onActivityCreated()
        bottomNavigationView.visibility = View.VISIBLE
        appBarLayout.visibility = View.VISIBLE
    }

}