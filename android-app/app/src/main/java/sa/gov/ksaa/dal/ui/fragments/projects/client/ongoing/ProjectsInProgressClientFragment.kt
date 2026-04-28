package sa.gov.ksaa.dal.ui.fragments.projects.client.ongoing

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.ui.adapters.OngoingProjectsClientRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancerProfileFragment
import sa.gov.ksaa.dal.ui.viewModels.OngoingProjectsVM

class ProjectsInProgressClientFragment : BaseFragment(R.layout.fragment_projects_in_progress_client),
    OngoingProjectsClientRVadapter.OnClickListener {

    val ongoingProjectsVM: OngoingProjectsVM by viewModels()

    lateinit var ongoingProjectsClientRVadapter: OngoingProjectsClientRVadapter
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE

        initViews(createdView)

        ongoingProjectsClientRVadapter = OngoingProjectsClientRVadapter(mutableListOf(), this, requireContext())
        ongoingProjectsRV.adapter = ongoingProjectsClientRVadapter
        updateUI()

        // user_type=&user_id=6
        ongoingProjectsVM.getAllByUserTypeAndUserId(mapOf("user_type" to _user!!.userType!!.lowercase(),
            "user_id" to _user!!.userId.toString()
        ))
            .observe(viewLifecycleOwner){
                newHandleSuccessOrErrorResponse(it, {ongoingProject ->
                    ongoingProjectsClientRVadapter.setList(ongoingProject)
                    updateUI()
                })
            }
//        ongoingProjectsRV.setOnClickListener {
////            findNavController().navigate(R.id.)
//        }
    }

    private fun updateUI() {
        if (ongoingProjectsClientRVadapter.getList().isEmpty()){
            showMoreBtn.visibility = View.GONE
        }
        ongoingProjectsCountTV.text = numberFormat.format(ongoingProjectsClientRVadapter.getList().size)
    }

    lateinit var ongoingProjectsCountTV: TextView
    lateinit var ongoingProjectsRV: RecyclerView
    lateinit var showMoreBtn: Button

    lateinit var bidsBtn: MaterialButton
    lateinit var completedProjectsBtn: MaterialButton
    lateinit var createdProjectsBtn: MaterialButton
    lateinit var waitingProjectsBtn: MaterialButton



    private fun initViews(createdView: View) {
        ongoingProjectsCountTV = createdView.findViewById(R.id.ongoingProCountTV)
        ongoingProjectsRV = createdView.findViewById(R.id.ongoingProjectsRV)
        showMoreBtn = createdView.findViewById(R.id.showMoreBtn)

        bidsBtn = createdView.findViewById(R.id.bidsBtn)
        bidsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_ongoingProjectsClientFragment_to_projectsBidsCientFragment)
        }
        completedProjectsBtn = createdView.findViewById(R.id.completedProjectsBtn)
        completedProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_ongoingProjectsClientFragment_to_completedProjectsClientFragment)
        }

        createdProjectsBtn = createdView.findViewById(R.id.createdProjectsBtn)
        createdProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_ongoingProjectsClientFragment_to_allProjectsClientFragment)
        }
//        waitingProjectsBtn = createdView.findViewById(R.id.waitingProjectsBtn)
//        waitingProjectsBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_ongoingProjectsClientFragment_to_waitingProjectsClientFragment)
//        }
    }

    override fun onClicked(ongoingProject: ProjectUnderway) {
        activityVM.ongoingProjectLD.postValue(ongoingProject)
        findNavController().navigate(R.id.action_ongoingProjectsClientFragment_to_onGoingProjectForClientFragment,
            bundleOf(
            OngoingProjectForClientFragment.onGgoinProjectIdKey to ongoingProject.id!!))
    }

    override fun onPhotoClicked(freelancerUserId: Int) {
        activityVM.freelancerMLD.postValue(null)
        findNavController()
            .navigate(R.id.action_ongoingProjectsClientFragment_to_freelancerProfileFragment,
                bundleOf(FreelancerProfileFragment.freelancerUserIdKey to freelancerUserId)
            )
    }
}