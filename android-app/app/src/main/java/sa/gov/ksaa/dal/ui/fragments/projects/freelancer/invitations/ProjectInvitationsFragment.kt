package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.invitations

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.BiddingInvitation
import sa.gov.ksaa.dal.ui.adapters.ProjectInvitationsRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.FavouriteProjectsVM
import sa.gov.ksaa.dal.ui.viewModels.ProjectInvitatinsVM

class ProjectInvitationsFragment : BaseFragment(R.layout.fragment_project_invitations),
    ProjectInvitationsRV_Adapter.OnClickListener {

    val projectInvitatinsVM: ProjectInvitatinsVM by viewModels()
    val favouriteProjectsVM: FavouriteProjectsVM by viewModels()

    lateinit var projectsRVadapter: ProjectInvitationsRV_Adapter
//    val requestObserver =


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE

        initViews(createdView)
        projectsRVadapter = ProjectInvitationsRV_Adapter(mutableListOf(), this, requireContext())
        recyclerView.adapter = projectsRVadapter

        updateUI(listOf())

        //user_id=1
        projectInvitatinsVM.getBiddingInvitationsByUserId(
            mapOf("user_id" to _user!!.userId.toString())
        ).observe(viewLifecycleOwner) { resource ->
            newHandleSuccessOrErrorResponse(resource,
                onSuccess = { projects ->
                    Log.w(javaClass.simpleName, "onViewCreated: projects $projects")
                    updateUI(projects)
//                    pagingCtrlLL.visibility = View.VISIBLE
                })
        }
    }

    private fun updateUI(projects: List<BiddingInvitation>) {
        projectsRVadapter.setList(projects)
        noInvitationsTV.text = numberFormat.format(projects.size)
    }

    lateinit var bidsBtn: MaterialButton
    lateinit var ongoingProjectsBtn: MaterialButton
    lateinit var completedProjectsBtn: MaterialButton
    lateinit var createdProjectsBtn: MaterialButton

    private fun initViews(createdView: View) {
        recyclerView = createdView.findViewById(R.id.recyclerView)
        noInvitationsTV = createdView.findViewById(R.id.noInvitationsTV)
//        prev_btn = createdView.findViewById(R.id.prev_btn)
//        next_btn = createdView.findViewById(R.id.next_btn)
//        pagingCtrlLL = createdView.findViewById(R.id.pagingCtrlLL)


        bidsBtn = createdView.findViewById(R.id.bidsBtn)
        bidsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectInvitationsFragment_to_projectsBidsFragment)
        }
        ongoingProjectsBtn = createdView.findViewById(R.id.ongoingProjectsBtn)
        ongoingProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectInvitationsFragment_to_projectsInProgressFragment)
        }
        completedProjectsBtn = createdView.findViewById(R.id.completedProjectsBtn)
        completedProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectInvitationsFragment_to_completedProjectsFragment)
        }
    }


    lateinit var recyclerView: RecyclerView
    lateinit var noInvitationsTV: TextView
//    lateinit var prev_btn: ImageButton
//    lateinit var next_btn: ImageButton
//    lateinit var pagingCtrlLL: LinearLayout

    override fun onProjectClicked(biddingInvitation: BiddingInvitation) {
        // action_exploreMainFragment_to_projectDetailsFragment
        // action_exploreProjectsFragment_to_projectDetailsFragment

        activityVM.projecInvitationtMLD.value = biddingInvitation
        findNavController().navigate(
            R.id.action_projectInvitationsFragment_to_invitationFragment,
//            bundleOf(ProjectDetailsFragment.projectIdArgKey to biddingInvitation.id)
        )
    }

    override fun onDetailsClicked(project: BiddingInvitation) {
        activityVM.projecInvitationtMLD.value = project
        findNavController().navigate(
            R.id.action_projectInvitationsFragment_to_invitationFragment,
//            bundleOf(ProjectDetailsFragment.projectIdArgKey to project.id)
        )
    }
    override fun onFavoriteClicked(biddingInvitation: BiddingInvitation, position: Int) {
        val isFavourite = biddingInvitation.favourite == true
        val msg = if (isFavourite) {
            "تم حذف المشروع من المفضلة"
        } else "تم إضافة المشروع الى المفضلة"

        // favouriteProject?freelancerId=7&isFavourite=true&projectId=4
        favouriteProjectsVM.favouriteProject(
            mapOf(
                "freelancerId" to  currentFreelancer!!.id.toString(),
                "isFavourite" to (!isFavourite).toString(),
                "projectId" to biddingInvitation.id.toString(),
            )
        ).observe(viewLifecycleOwner) {
            newHandleSuccessOrErrorResponse(
                it,
                onSuccess = { favourite_aProjectRes ->
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT)
                        .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>(){
                            override fun onDismissed(
                                transientBottomBar: Snackbar?,
                                event: Int
                            ) {
                                super.onDismissed(transientBottomBar, event)
                                biddingInvitation.favourite = favourite_aProjectRes.favourite
                                projectsRVadapter.notifyItemChanged(position)
                            }
                        }).show()


                })
        }


    }
}