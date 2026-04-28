package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.closed

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.ui.adapters.CompletedProjectsRV_AdapterForFreelancer
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.CompletedProjectsVM

class CompletedProjectsForFreelancerFragment :
    BaseFragment(R.layout.fragment_projects_completed_freelancer),
    CompletedProjectsRV_AdapterForFreelancer.OnClickListener {
//    var completedProjs: List<Project>? = null

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)

        completedProjectsRVAdapter = CompletedProjectsRV_AdapterForFreelancer(mutableListOf(), this, requireContext())
        completedProjectsRV.adapter = completedProjectsRVAdapter

        updateUI()

        // user_type=freelancer&user_id=1
        completedProjectsVM.getAllByUserTypeAndUserId(
            mapOf(
                "user_id" to _user!!.userId.toString(),
                "user_type" to "freelancer"
            )
        )
            .observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, { completedProjects ->
                    Log.w(
                        javaClass.simpleName,
                        "onViewCreated: completedProjects = $completedProjects"
                    )
                    completedProjectsRVAdapter.setList(completedProjects)
                    updateUI()
                })
            }
    }

    lateinit var noCompletedProTV: TextView
    lateinit var completedProjectsRV: RecyclerView
    lateinit var showMoreBtn: Button

    lateinit var bidsBtn: MaterialButton
    lateinit var ongoingProjectsBtn: MaterialButton
    lateinit var completedProjectsBtn: MaterialButton
    lateinit var createdProjectsBtn: MaterialButton

    private fun initViews(createdView: View) {
        noCompletedProTV = createdView.findViewById(R.id.noCompletedProTV)
        completedProjectsRV = createdView.findViewById(R.id.completedProjectsRV)
        showMoreBtn = createdView.findViewById(R.id.showMoreBtn)


        bidsBtn = createdView.findViewById(R.id.bidsBtn)
        bidsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_completedProjectsFragment_to_projectsBidsFragment)
        }
        ongoingProjectsBtn = createdView.findViewById(R.id.ongoingProjectsBtn)
        ongoingProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_completedProjectsFragment_to_projectsInProgressFragment)
        }

        createdProjectsBtn = createdView.findViewById(R.id.createdProjectsBtn)
        createdProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_completedProjectsFragment_to_projectInvitationsFragment)
        }
    }

    val completedProjectsVM: CompletedProjectsVM by viewModels()

    lateinit var completedProjectsRVAdapter: CompletedProjectsRV_AdapterForFreelancer

    private fun updateUI() {
        if (completedProjectsRVAdapter.getList().isEmpty()) {
            showMoreBtn.visibility = View.GONE
        }
        noCompletedProTV.text = numberFormat.format(completedProjectsRVAdapter.getList().size)
    }

    override fun onClicked(closedProject: ClosedProject) { // closedProjectId
        activityVM.closedProject.value = closedProject
        findNavController().navigate(
            R.id.action_completedProjectsFragment_to_completedProjectForFreelancerFragment,
            bundleOf(CompletedProjectForFreelancerFragment.closedProjectIdKey to closedProject.id)
        )
    }

    override fun onReviewBtnClicked(closedProject: ClosedProject, position: Int) {

        activityVM.closedProject.value = closedProject
        activityVM.closedProject.observe(viewLifecycleOwner, object: Observer<ClosedProject?>{

            override fun onChanged(value: ClosedProject?) {
                closedProject.isReviewed = value?.isReviewed == true
                completedProjectsRVAdapter.notifyItemChanged(position)
                activityVM.closedProject.removeObserver(this)
            }
        })

//        val freelancer = completedProject.working_project?.freelancer
//        activityVM.closedProject.postValue(closedProject)
        val postRatingAndReview = RatingAndReview()

        postRatingAndReview.projectId = closedProject.projectId
        postRatingAndReview.clientUserId = closedProject.userId
        postRatingAndReview.freelancerUserId = closedProject.listOfServices?.firstOrNull()?.userId

        activityVM.postRatingAndReview.postValue(postRatingAndReview)
        findNavController().navigate(
            R.id.action_completedProjectsFragment_to_addEditReviewDialogFragment,
//            bundleOf(
//                AddEditReviewDialogFragment.revieweeIdKey to (freelancer?.user_id ?: freelancer?.user?.user_id),
//                AddEditReviewDialogFragment.completedProjectIdKey to completedProject.id)
        )


    }
}