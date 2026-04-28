package sa.gov.ksaa.dal.ui.fragments.notifcation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Notifcation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import sa.gov.ksaa.dal.ui.adapters.NotifcationAdapter
import sa.gov.ksaa.dal.ui.adapters.TechSupportTicketsRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancerProfileFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.UserProfileFragmentDirections
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.viewModels.NotifcationVM
import sa.gov.ksaa.dal.ui.viewModels.TechnicalSupportVM

class NotifcationFragment: BaseFragment(R.layout.fragment_notifcation),NotifcationAdapter.OnClickListener {

    lateinit var notifcation: MutableList<Notifcation>
    lateinit var notifcationAdapter: NotifcationAdapter

    lateinit var notifcationRV: RecyclerView


    val vm: NotifcationVM by viewModels()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)



            notifcationRV = createdView.findViewById(R.id.notifcationRV)

            appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.VISIBLE


            notifcationAdapter = NotifcationAdapter(mutableListOf(),this)
            notifcationRV.adapter = notifcationAdapter





    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        if (_user == null) {
            appBarLayout.visibility = View.GONE
            val action = NotifcationFragmentDirections.actionNotifcationToGetStartedToLogin()

            findNavController().safeNavigateWithArgs(action,null)

        } else {
            vm.getNotifcation(mapOf("userId" to _user!!.userId.toString()))
                .observe(viewLifecycleOwner) {
                    newHandleSuccessOrErrorResponse(it, {

                        Log.w(javaClass.simpleName, "onViewCreated: it = $it")
                        notifcation = it.toMutableList()
                        notifcationAdapter.setList(notifcation)

                    })
                }
        }


    }

    override fun onClicked() {

        if (_user!!.isClient()) {
            val action =
                NotifcationFragmentDirections.actionNotifcationToProjectsBidsCientFragment()

            findNavController().safeNavigateWithArgs(action, null)
        }
        else {
            val action =
                NotifcationFragmentDirections.actionNotifcationToProjectsBidsFragment()

            findNavController().safeNavigateWithArgs(action, null)
        }


    }
}