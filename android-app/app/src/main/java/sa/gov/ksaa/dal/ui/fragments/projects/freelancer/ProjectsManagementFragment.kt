package sa.gov.ksaa.dal.ui.fragments.projects.freelancer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.models.Bid
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.ui.adapters.NewProjectBidsRVadapter
import sa.gov.ksaa.dal.ui.adapters.NewProjectsForFreelancerRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM

class ProjectsManagementFragment: BaseFragment(R.layout.fragment_projects_management_freelancer),
    NewProjectsForFreelancerRV_Adapter.OnClickListener {

    val vm: ProjectsVM by viewModels()
//    lateinit var newProjectsRVadapter: NewProjectsRVadapter
    lateinit var newProjectsForFreelancerRV_Adapter: NewProjectsForFreelancerRV_Adapter
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        initViews(createdView)

        updateUI()

//        newProjectsRVadapter = NewProjectsRVadapter(mutableListOf())
        newProjectsForFreelancerRV_Adapter = NewProjectsForFreelancerRV_Adapter(mutableListOf(), this)
        newProjectBidsRV.adapter = newProjectsForFreelancerRV_Adapter

//        vm.getNewBids(mutableMapOf("freelance_id" to _user!!.freelancer!!.freelance_id.toString()))
//            .observe(viewLifecycleOwner){
//                getResponseData(it, { data ->
//                    Log.i(TAG, "onViewCreated: data = $data")
//                    // newProjectsRVadapter.setList(data)
//                }) { errors ->
//                    Log.i(TAG, "onViewCreated: errors = $errors")
//                }
//            }
        vm.getAllProjects()
            .observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, { newProjects ->
                    Log.i(TAG, "onViewCreated: newProjects = $newProjects")
                    noCompletedPro = 0
                    noOngoingPro = 0
                    noBids = 0
                    /*
                    val projects =  newProjects.filter { pro -> {}
                        if (pro.biddings == null)
                            return@filter false
                        else return@filter pro.biddings!!.any { bid ->
                            bid.freelance_id == _user?.freelancer?.freelance_id
                        }
                    }
                    projects.forEach { project ->
                        when(project.status){
                            Project.projectHasBidsStatus -> noBids++
                            Project.ongoingProjectStatus -> noOngoingPro++
                            Project.completedProjectStatus -> noCompletedPro++
                        }
                    }*/
//                    newProjectsForFreelancerRV_Adapter.setList(projects)
                    updateUI()


                }) { errors ->

                }
            }
    }

    var noCompletedPro = 0
    var noOngoingPro = 0
    var noBids = 0
    var canceledProjects = 0

    private fun updateUI() {
        noCompletedProTV.text = numberFormat.format(noCompletedPro)
        completedProBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsManagementFragment_to_completedProjectsFragment)
        }
        noOngoingProTV.text = numberFormat.format(noOngoingPro)
        ongoingProBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsManagementFragment_to_projectsInProgressFragment)
        }
        noBidsTV.text = numberFormat.format(noBids)
        bidsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsManagementFragment_to_projectsBidsFragment)
        }

        canceledProTV.text = numberFormat.format(canceledProjects)
        canceledProBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsManagementFragment_to_canceledProjectsFreelancerFragment)
        }
    }

    private fun initViews(createdView: View) {
        noCompletedProTV = createdView.findViewById(R.id.noCompletedProTV)
        completedProBtn = createdView.findViewById(R.id.completedProBtn)
        noOngoingProTV = createdView.findViewById(R.id.noOngoingProTV)
        ongoingProBtn = createdView.findViewById(R.id.ongoingProBtn)
        noBidsTV = createdView.findViewById(R.id.noBidsTV)
        bidsBtn = createdView.findViewById(R.id.bidsBtn)
        canceledProBtn = createdView.findViewById(R.id.canceledProBtn)
        canceledProTV = createdView.findViewById(R.id.canceledProTV)
        newProjectBidsRV = createdView.findViewById(R.id.newProjectBidsRV)
    }
    lateinit var noCompletedProTV: TextView
    lateinit var completedProBtn: Button
    lateinit var noOngoingProTV: TextView
    lateinit var ongoingProBtn: Button
    lateinit var noBidsTV: TextView
    lateinit var bidsBtn: Button
    lateinit var canceledProBtn: Button
    lateinit var canceledProTV: TextView
    lateinit var newProjectBidsRV: RecyclerView

    fun onProjectClicked(project: Project) {
        findNavController().navigate(
            R.id.action_projectsManagementFragment_to_projectDetailsFragment,
            bundleOf(ProjectDetailsFragment.projectIdArgKey to project.project_id)
        )
    }

    override fun onInvoiceClicked(project: Project) {

    }

    override fun onDetailsClicked(project: Project) {
        findNavController().navigate(R.id.action_projectsManagementFragment_to_projectDetailsFragment,
            bundleOf(ProjectDetailsFragment.projectIdArgKey to project.project_id))
    }
}