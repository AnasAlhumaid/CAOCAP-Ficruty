package sa.gov.ksaa.dal.ui.fragments.projects.client.completed

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
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.ui.adapters.CompletedProjectsRV_Adapter4Client
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancerProfileFragment
import sa.gov.ksaa.dal.ui.viewModels.CompletedProjectsVM

class CompletedProjectsForClientFragment : BaseFragment(R.layout.fragment_projects_completed_client),
    CompletedProjectsRV_Adapter4Client.OnClickListener {

    var completedProjs: List<Project>? = null
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)


        completedProjectsRVAdapter = CompletedProjectsRV_Adapter4Client(mutableListOf(), this, requireContext())
        completedProjectsRV.adapter = completedProjectsRVAdapter

        updateUI()
        // user_type=freelancer&user_id=1
        completedProjectsVM.getAllByUserTypeAndUserId(mapOf("user_id" to _user?.userId.toString(), "user_type" to "client"))
            .observe(viewLifecycleOwner){
                newHandleSuccessOrErrorResponse(it, {ongoingProject ->
                    completedProjectsRVAdapter.setList(ongoingProject)
                    updateUI()
                })
            }
    }

    lateinit var noCompletedProTV: TextView
    lateinit var completedProjectsRV: RecyclerView
    lateinit var showMoreBtn: Button

    lateinit var bidsBtn: MaterialButton
    lateinit var ongoingProjectsBtn: MaterialButton
    lateinit var createdProjectsBtn: MaterialButton
    lateinit var waitingProjectsBtn: MaterialButton


    private fun initViews(createdView: View) {
        noCompletedProTV = createdView.findViewById(R.id.noCompletedProTV)
        completedProjectsRV = createdView.findViewById(R.id.completedProjectsRV)
        showMoreBtn = createdView.findViewById(R.id.showMoreBtn)
        if (completedProjs == null)
            showMoreBtn.visibility = View.GONE

        bidsBtn = createdView.findViewById(R.id.bidsBtn)
        bidsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_completedProjectsClientFragment_to_projectsBidsCientFragment)
        }
        ongoingProjectsBtn = createdView.findViewById(R.id.ongoingProjectsBtn)
        ongoingProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_completedProjectsClientFragment_to_ongoingProjectsClientFragment)
        }

        createdProjectsBtn = createdView.findViewById(R.id.createdProjectsBtn)
        createdProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_completedProjectsClientFragment_to_allProjectsClientFragment)
        }
//        waitingProjectsBtn = createdView.findViewById(R.id.waitingProjectsBtn)
//        waitingProjectsBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_completedProjectsClientFragment_to_waitingProjectsClientFragment)
//        }
    }

    val completedProjectsVM: CompletedProjectsVM by viewModels()

    lateinit var completedProjectsRVAdapter: CompletedProjectsRV_Adapter4Client

    private fun updateUI() {
        if (completedProjectsRVAdapter.getList().isEmpty()){
            showMoreBtn.visibility = View.GONE
        }
        noCompletedProTV.text = numberFormat.format(completedProjectsRVAdapter.getList().size)
    }

    override fun onClicked(closedProject: ClosedProject) {
        activityVM.closedProject.value = closedProject
        findNavController().navigate(R.id.action_completedProjectsClientFragment_to_completedProjectForClientFragment,
        bundleOf(CompletedProjectForClientFragment.closedProjectIdKey to closedProject.id)
        )

    }

    override fun onReviewBtnClicked(closedProject: ClosedProject) {
        activityVM.closedProject.postValue(closedProject)
        findNavController().navigate(R.id.action_completedProjectsClientFragment_to_addEditReviewDialogFragment,
//        bundleOf(AddEditReviewDialogFragment.revieweeIdKey to (freelancer?.user_id ?: freelancer?.user?.user_id),
//            AddEditReviewDialogFragment.completedProjectIdKey to completedProject.id)
        )
    }

    override fun onFreelancerPhotoClicked(freelancerUserId: Int) {
        activityVM.freelancerMLD.postValue(null)
        findNavController()
            .navigate(R.id.action_completedProjectsClientFragment_to_freelancerProfileFragment,
                bundleOf(FreelancerProfileFragment.freelancerUserIdKey to freelancerUserId)
            )
    }
}