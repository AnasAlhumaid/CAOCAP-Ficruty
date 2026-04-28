package sa.gov.ksaa.dal.ui.fragments.favourite

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Qutation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.ui.adapters.FavouriteProjectsRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment
import sa.gov.ksaa.dal.ui.fragments.bids.AddEditQuotationFragment
import sa.gov.ksaa.dal.ui.viewModels.FavouriteProjectsVM

class FavouriteProjectsFragment : BaseFragment(R.layout.fragment_favourite_projects),
    AddEditQuotationFragment.OnSendQuotationListener,
    FavouriteProjectsRV_Adapter.OnClickListener {
    val favouriteProjectsVM: FavouriteProjectsVM by viewModels()

    lateinit var projectsRVadapter: FavouriteProjectsRV_Adapter
    val freelancerFavoriteProjectList = mutableListOf<NewProject>()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE
        initViews(createdView)

        projectsRVadapter = FavouriteProjectsRV_Adapter(freelancerFavoriteProjectList, this,requireContext())
        recyclerView.adapter = projectsRVadapter
        // freelancerId=4
        favouriteProjectsVM.getFavoriteProjects(freelancerId = currentFreelancer!!.userId!!) // _user!!.userId!!
            .observe(viewLifecycleOwner) { resource ->
                newHandleSuccessOrErrorResponse(resource, { projects ->
                    Log.i(javaClass.simpleName, "onViewCreated: projects = $projects")
                    projects.forEach { project ->
                        project.favourite = true
                    }
                    updateUI(projects)
                })
            }

    }

    private fun updateUI(projects: List<NewProject>) {
        Log.i(javaClass.simpleName, "updateUI: projects = $projects")
        if (projects.isEmpty()) {
            recyclerView.visibility = View.GONE
            nodateTV.visibility = View.VISIBLE
        } else {
            nodateTV.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            projectsRVadapter.setList(projects)
        }
    }

    private fun initViews(createdView: View) {
        recyclerView = createdView.findViewById(R.id.recyclerView)
        nodateTV = createdView.findViewById(R.id.nodateTV)
    }
    lateinit var recyclerView: RecyclerView
    lateinit var nodateTV: TextView

    override fun onProjectClicked(project: NewProject) {

        activityVM.newProjectMLD.postValue(project)

        findNavController().navigate(
            R.id.action_favouriteProjectsFragment_to_projectDetailsFragment,
            bundleOf(ProjectDetailsFragment.projectIdArgKey to (project.id?:project.projectId!!.toInt()))
        )
    }

    override fun onSendQuotClicked(project: NewProject) {
        activityVM.newProjectMLD.postValue(project)
        showSendQuot(project.projectId!!)
    }

    @SuppressLint("ShowToast")
    override fun onFavoriteClicked(
        project: NewProject,
        position: Int
    ) {

        // favouriteProject?freelancerId=7&isFavourite=true&projectId=4
        favouriteProjectsVM.favouriteProject(
            mutableMapOf(
            "projectId" to (project.id?:project.projectId!!.toInt()).toString(),
            "isFavourite" to false.toString(),
            "freelancerId" to _user!!.userId.toString()
        )
        ).observe(viewLifecycleOwner) {
            newHandleSuccessOrErrorResponse(
                it, { data ->
                    projectsRVadapter.delete_aProject(position)
                    Snackbar.make(requireView(), "تم حذف المشروع من المفضلة", Snackbar.LENGTH_SHORT)
                        .show()
                })
        }
    }

    fun showSendQuot(projectId: Int) {
        findNavController().navigate(R.id.action_favouriteProjectsFragment_to_addEditQuotationFragment,
            bundleOf(AddEditQuotationFragment.projectIdArgKey to projectId)
        )

//        AddEditQuotationFragment(projectId, _user!!.freelancer!!, this)
//            .show(
//                mainActivity.supportFragmentManager,
//                AddEditQuotationFragment.TAG
//            )

    }

    override fun submitQuotation(qutation: Qutation) {
        showAlertDialog(
            message = getString(R.string.your_quotation_has_been_submitted_successfully),
            positiveText = getString(R.string.ok),
            positiveOnClick = DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            }
        )
    }
}