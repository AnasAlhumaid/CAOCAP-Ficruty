package sa.gov.ksaa.dal.ui.fragments.projects.client.witing

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
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM

class witingProjectsFragment : BaseFragment(R.layout.fragment_projects_waiting_cient),AllProjectsClientRVadapter.OnClickListener{

    val projectsVM: ProjectsVM by viewModels()

    lateinit var newProjectsRVAdapter: AllProjectsClientRVadapter
    lateinit var projectsList: List<NewProject>
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

                    projectsList = newProjectsList.filter {


                        it.projectStatus == "noBid"




                    }.sortedByDescending { newProject -> newProject.id }
                    updateUI(projectsList)
                })

            }
    }

    private fun updateUI(newProjectsList: List<NewProject>) {


        newProjectsRVAdapter.setList(newProjectsList)
        waitingProjectsTV.text = numberFormat.format(newProjectsList.size)

    }

    lateinit var newProjectsRV: RecyclerView
    lateinit var waitingProjectsTV: TextView

    private fun initViews(createdView: View) {
        newProjectsRV = createdView.findViewById(R.id.newProjectBidsRV)
        waitingProjectsTV = createdView.findViewById(R.id.waitingProjectsTV)


        bidsBtn = createdView.findViewById(R.id.bidsBtn)
        bidsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_waitingProjectsClientFragment_to_projectsBidsCientFragment)
        }
        ongoingProjectsBtn = createdView.findViewById(R.id.ongoingProjectsBtn)
        ongoingProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_waitingProjectsClientFragment_to_ongoingProjectsClientFragment)
        }

        completedProjectsBtn = createdView.findViewById(R.id.completedProjectsBtn)
        completedProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_waitingProjectsClientFragment_to_completedProjectsClientFragment)
        }
        createdProjectsBtn = createdView.findViewById(R.id.createdProjectsBtn)
        createdProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_waitingProjectsClientFragment_to_allProjectsClientFragment)
        }
    }

    lateinit var bidsBtn: MaterialButton
    lateinit var completedProjectsBtn: MaterialButton
    lateinit var ongoingProjectsBtn: MaterialButton
    lateinit var createdProjectsBtn: TextView


    fun onProjectClicked(project: Project) {
//        activityVM.projectMLD.value = project
        findNavController().navigate(
            R.id.action_waitingProjectsClientFragment_to_DetailProjectManagmentFragment,
            bundleOf(ProjectDetailsFragment.projectIdArgKey to project.project_id)
        )
    }



    override fun onClicked(project: NewProject) {
        project.userId = _user!!.userId
        project.clientName = _user!!.getFullName()
        activityVM.newProjectMLD.value = project
        findNavController().navigate(
            R.id.action_waitingProjectsClientFragment_to_DetailProjectManagmentFragment,
//             bundleOf(ProjectDetailsFragment.projectIdArgKey to project.id)
        )
    }
}