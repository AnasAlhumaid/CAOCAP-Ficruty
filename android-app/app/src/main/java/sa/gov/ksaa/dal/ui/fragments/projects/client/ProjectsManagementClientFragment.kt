package sa.gov.ksaa.dal.ui.fragments.projects.client

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.ui.adapters.NewProjectsClientRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM

//class ProjectsManagementClientFragment : BaseFragment(R.layout.fragment_projects_new_client)
//    , NewProjectsClientRVadapter.OnClickListener {
//    val vm: ProjectsVM by viewModels()
//    lateinit var newProjectsRVAdapter: NewProjectsClientRVadapter
//    var completedProjects: Int = 0
//    var ongoingProjects: Int = 0
//    var projectsHasBids: Int = 0
//    var canceledProjects: Int = 0
//    var waitingProjects:Int = 0
//
//    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(createdView, savedInstanceState)
//        appBarLayout.visibility = View.VISIBLE
//
//        initViews(createdView)
//
//        newProjectsRVAdapter = NewProjectsClientRVadapter(mutableListOf(), this)
//        newProjectsRV.adapter = newProjectsRVAdapter
//
//        vm.getProjectsByUserId(userId = _user!!.userId!!)
//            .observe(viewLifecycleOwner) {
//                newHandleSuccessOrErrorResponse(it, { newProjectsList ->
//                    projectsHasBids = 0
//                    ongoingProjects = 0
//                    completedProjects = 0
//                    canceledProjects = 0
//                    waitingProjects = 0
//
////                    for (project in newProjectsList) {
////                        when (project.status) {
////                            Project.projectHasBidsStatus -> projectsHasBids++
////                            Project.ongoingProjectStatus -> ongoingProjects++
////                            Project.completedProjectStatus -> completedProjects++
////                            Project.cancelledProjectStatus -> canceledProjects++
////                        }
////                    }
////                    updatedCardsFigures()
////                    newProjectsRVAdapter.setList(newProjectsList)
//                })
//
//            }
//    }
//
//    lateinit var noCompletedProTV: TextView
//    lateinit var noOngoingProTV: TextView
//    lateinit var noBidsTV: TextView
//    lateinit var canceledProTV: TextView
//
//    lateinit var completedProBtn: Button
//    lateinit var ongoingProBtn: Button
//    lateinit var bidsBtn: Button
//    lateinit var canceledProBtn: Button
//
//    lateinit var newProjectsRV: RecyclerView
//    lateinit var addNewProjectBtn: Button
//
//    private fun initViews(createdView: View) {
//        noCompletedProTV = createdView.findViewById(R.id.noCompletedProTV)
//        noOngoingProTV = createdView.findViewById(R.id.noOngoingProTV)
//        noBidsTV = createdView.findViewById(R.id.noBidsTV)
//        canceledProTV = createdView.findViewById(R.id.canceledProTV)
//
//        updatedCardsFigures()
//        completedProBtn = createdView.findViewById(R.id.completedProBtn)
//        completedProBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_projectsManagmentClientFragment_to_completedProjectsClientFragment)
//        }
//        ongoingProBtn = createdView.findViewById(R.id.ongoingProBtn)
//        ongoingProBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_projectsManagmentClientFragment_to_ongoingProjectsClientFragment)
//        }
//        bidsBtn = createdView.findViewById(R.id.bidsBtn)
//        bidsBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_projectsManagmentClientFragment_to_projectsBidsCientFragment)
//        }
//        canceledProBtn = createdView.findViewById(R.id.canceledProBtn)
//        canceledProBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_projectsManagmentClientFragment_to_canceledProjectsClientFragment)
//        }
//
//        newProjectsRV = createdView.findViewById(R.id.newProjectBidsRV)
//
//        addNewProjectBtn = createdView.findViewById(R.id.addNewProjectBtn)
//        addNewProjectBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_projectsManagmentClientFragment_to_addEditProjectFragment)
//        }
//    }
//
//    private fun updatedCardsFigures() {
//        noCompletedProTV.text = numberFormat.format(completedProjects)
//        noOngoingProTV.text = numberFormat.format(ongoingProjects)
//        noBidsTV.text = numberFormat.format(projectsHasBids)
//        canceledProTV.text = numberFormat.format(canceledProjects)
//
//    }
//
//    fun onProjectClicked(project: Project) {
////        activityVM.projectMLD.value = project
//        findNavController().navigate(
//            R.id.action_projectsManagmentClientFragment_to_projectDetailsFragment,
//            bundleOf(ProjectDetailsFragment.projectIdArgKey to project.project_id)
//        )
//    }
//
//    fun onDetailsBtnClicked(project: Project) {
////        activityVM.projectMLD.value = project
//        findNavController().navigate(
//            R.id.action_projectsManagmentClientFragment_to_projectDetailsFragment,
//            bundleOf(ProjectDetailsFragment.projectIdArgKey to project.project_id)
//        )
//    }
//
//    override fun onClicked(project: NewProject) {
//        TODO("Not yet implemented")
//    }
//}