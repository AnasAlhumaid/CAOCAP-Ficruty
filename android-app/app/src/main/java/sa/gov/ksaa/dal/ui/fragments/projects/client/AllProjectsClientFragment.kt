package sa.gov.ksaa.dal.ui.fragments.projects.client

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.ui.adapters.AllProjectsClientRVadapter
import sa.gov.ksaa.dal.ui.adapters.NewProjectsClientRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM

class AllProjectsClientFragment : BaseFragment(R.layout.fragment_projects_new_client),
    AllProjectsClientRVadapter.OnClickListener {
    val projectsVM: ProjectsVM by viewModels()

    lateinit var newProjectsRVAdapter: AllProjectsClientRVadapter

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.VISIBLE


        initViews(createdView)

        newProjectsRVAdapter = AllProjectsClientRVadapter(mutableListOf(), this, _user!!, requireContext())
        newProjectsRV.adapter = newProjectsRVAdapter

        updateUI(listOf())
        projectsVM.getProjectsByUserID(userId = _user!!.userId!!)
            .observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, { newProjectsList ->
                    updateUI(newProjectsList)
                })

            }
    }

    private fun updateUI(newProjectsList: List<NewProject>) {

        newProjectsRVAdapter.setList(newProjectsList.sortedByDescending { it.createdDate })
        noOfProjectsTV.text = numberFormat.format(newProjectsList.size)

    }

    lateinit var newProjectsRV: RecyclerView

    private fun initViews(createdView: View) {
        newProjectsRV = createdView.findViewById(R.id.newProjectBidsRV)
        noOfProjectsTV = createdView.findViewById(R.id.noOfProjectsTV)
       

        bidsBtn = createdView.findViewById(R.id.bidsBtn)
        bidsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_allProjectsClientFragment_to_projectsBidsCientFragment)
        }
        ongoingProjectsBtn = createdView.findViewById(R.id.ongoingProjectsBtn)
        ongoingProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_allProjectsClientFragment_to_ongoingProjectsClientFragment)
        }

        completedProjectsBtn = createdView.findViewById(R.id.completedProjectsBtn)
        completedProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_allProjectsClientFragment_to_completedProjectsClientFragment)
        }
//        waitingProjectsBtn = createdView.findViewById(R.id.waitingProjectsBtn)
//        waitingProjectsBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_allProjectsClientFragment_to_waitingProjectsClientFragment)
//        }
    }

    lateinit var bidsBtn: MaterialButton
    lateinit var completedProjectsBtn: MaterialButton
    lateinit var ongoingProjectsBtn: MaterialButton
    lateinit var noOfProjectsTV: TextView
    lateinit var waitingProjectsBtn: TextView


    fun onProjectClicked(project: Project) {
//        activityVM.projectMLD.value = project
        findNavController().navigate(
            R.id.action_allProjectsClientFragment_to_DetailProjectManagmentFragment,
            bundleOf(ProjectDetailsFragment.projectIdArgKey to project.project_id)
        )
    }

    fun onDetailsBtnClicked(project: Project) {
//        activityVM.projectMLD.value = project
        findNavController().navigate(
            R.id.action_allProjectsClientFragment_to_DetailProjectManagmentFragment,
            bundleOf(ProjectDetailsFragment.projectIdArgKey to project.project_id)
        )
    }

    override fun onClicked(project: NewProject) {
        project.userId = _user!!.userId
        project.clientName = _user!!.getFullName()
        activityVM.newProjectMLD.value = project
        findNavController().navigate(
            R.id.action_allProjectsClientFragment_to_DetailProjectManagmentFragment,
//             bundleOf(ProjectDetailsFragment.projectIdArgKey to project.id)
        )
    }
}