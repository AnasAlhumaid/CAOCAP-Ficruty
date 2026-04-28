package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.ongoing

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.ui.adapters.OngoingProjectsFreelancerRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.OngoingProjectsVM

class ProjectsInProgressFragment : BaseFragment(R.layout.fragment_projects_in_progress),
    OngoingProjectsFreelancerRVadapter.OnClickListener {
    var ongoingProjects: List<Project>? = null
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)
        ongoingProjectsCountTV.text = numberFormat.format(0)

        ongoingProjectsClientRVadapter = OngoingProjectsFreelancerRVadapter(mutableListOf(), this, requireContext())
        ongoingProjectsRV.adapter = ongoingProjectsClientRVadapter
        updateUI()

        // user_type=&user_id=6
        ongoingProjectsVM.getAllByUserTypeAndUserId(mapOf("user_type" to _user!!.userType!!.lowercase(),
            "user_id" to _user!!.userId.toString()
        ))
            .observe(viewLifecycleOwner){
                newHandleSuccessOrErrorResponse(it, {ongoingProjects ->
                    Log.w(javaClass.simpleName, "onViewCreated: ongoingProjects = $ongoingProjects")
                    ongoingProjectsClientRVadapter.setList(ongoingProjects)
                    updateUI()
                })
            }
    }

    lateinit var ongoingProjectsCountTV: TextView
    lateinit var ongoingProjectsRV: RecyclerView
    lateinit var showMoreBtn: Button

    lateinit var bidsBtn: MaterialButton
    lateinit var ongoingProjectsBtn: MaterialButton
    lateinit var completedProjectsBtn: MaterialButton
    lateinit var createdProjectsBtn: MaterialButton

    private fun initViews(createdView: View) {
        ongoingProjectsCountTV = createdView.findViewById(R.id.ongoingProCountTV)
        ongoingProjectsRV = createdView.findViewById(R.id.ongoingProjectsRV)
        showMoreBtn = createdView.findViewById(R.id.showMoreBtn)
        if (ongoingProjects == null || ongoingProjects!!.isEmpty())
            showMoreBtn.visibility = View.GONE


        bidsBtn = createdView.findViewById(R.id.bidsBtn)
        bidsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsInProgressFragment_to_projectsBidsFragment)
        }

        completedProjectsBtn = createdView.findViewById(R.id.completedProjectsBtn)
        completedProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsInProgressFragment_to_completedProjectsFragment)
        }
        createdProjectsBtn = createdView.findViewById(R.id.createdProjectsBtn)
        createdProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsInProgressFragment_to_projectInvitationsFragment)
        }

    }

    val ongoingProjectsVM: OngoingProjectsVM by viewModels()

    lateinit var ongoingProjectsClientRVadapter: OngoingProjectsFreelancerRVadapter

    private fun updateUI() {
        if (ongoingProjectsClientRVadapter.getList().isEmpty()){
            showMoreBtn.visibility = View.GONE
        }
        ongoingProjectsCountTV.text = numberFormat.format(ongoingProjectsClientRVadapter.getList().size)
    }

    override fun onClicked(ongoingProject: ProjectUnderway) {
        activityVM.ongoingProjectLD.postValue(ongoingProject)
        findNavController().navigate(R.id.action_projectsInProgressFragment_to_onGoingProjectForFreelancerFragment,
            bundleOf(
                OnGoingProjectForFreelancerFragment.onGgoinProjectIdKey to ongoingProject.id!!)
        )
    }
}