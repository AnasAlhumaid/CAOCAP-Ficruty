package sa.gov.ksaa.dal.ui.fragments.projects.explore

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.ProjectsFilter
import sa.gov.ksaa.dal.data.models.Qutation
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.ui.MainActivity
import sa.gov.ksaa.dal.ui.adapters.ProjectsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.ExploreFragment
import sa.gov.ksaa.dal.ui.fragments.ExploreFragmentDirections
import sa.gov.ksaa.dal.ui.fragments.bids.AddEditQuotationFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.viewModels.FavouriteProjectsVM
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM


class ProjectsFragment() : BaseFragment(R.layout.fragment_projects),
    ProjectsRVadapter.OnClickListener, ProjectsFilterBottomSheetModal.OnFilterSetListener,
    ExploreFragment.Searcher, AddEditQuotationFragment.OnSendQuotationListener {

    constructor(outerFragment: ExploreFragment) : this() {
        this.outerFragment = outerFragment
    }

    private lateinit var outerFragment: ExploreFragment

    val projectsVM: ProjectsVM by viewModels()

    val favouriteProjectsVM: FavouriteProjectsVM by viewModels()

    lateinit var projectsRVadapter: ProjectsRVadapter
    lateinit var projectsList: List<NewProject>
    val requestObserver = { newResource: NewResource<List<NewProject>> ->
        newHandleSuccessOrErrorResponse(newResource,
            onSuccess = { projects ->

                projectsList = projects.filter {


                         it.projectStatus == "noBid" && it.inUnderway  == false && it.request == "general"




                }.sortedByDescending { newProject -> newProject.id }

                projectsRVadapter.setList(projectsList)
                pagingCtrlLL.visibility = View.VISIBLE
            }){

        }
    }


    lateinit var searchView: SearchView

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.VISIBLE

//        (mainActivity as MenuHost).addMenuProvider(menuProvider, viewLifecycleOwner)


        initViews(createdView)
        projectsRVadapter =
            ProjectsRVadapter(mutableListOf(), this, _user, currentFreelancer, requireContext())
        recyclerView.adapter = projectsRVadapter
//        add_btn.setOnClickListener {
//
//            val action =  ExploreFragmentDirections.actionExploreFragmentToAddEditProjectFragment()
//           outerFragment.navigateTo(action,null)
//
//        }


//        queryMap = mutableMapOf()
//        queryMap.put("is_public", true.toString().trim())
//        queryMap.put("status", Project.newProjectStatus.toString())
    }

//    val menuProvider = object : MenuProvider {
//        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//            menuInflater.inflate(R.menu.projects_freelancers_menu, menu)
//            // Associate searchable configuration with the SearchView
//            val searchManager =
//                mainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//
//            val menuItem = menu.findItem(R.id.search)
////        searchView = SearchView(mainActivity.supportActionBar!!.themedContext)
//            searchView = menuItem.actionView as SearchView
//
////        searchView.queryHint = if (recyclerView.adapter is ProjectsRVadapter) "Searching Projects" else "Searching Freelancers"
//
//            searchView.apply {
//                queryHint = "البحث عن المشاريع"
//
//                setBackgroundResource(R.drawable.bg_10_curved_white_borders_transparent_filled)
//                setSearchableInfo(searchManager.getSearchableInfo(mainActivity.componentName))
//                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                    override fun onQueryTextSubmit(query: String): Boolean {
//                        projectNameLike = query.trim()
//                        // Project(project_name = query)
//                        doSearch()
//                        searchView.clearFocus()
//                        return true
//                    }
//
//                    override fun onQueryTextChange(newText: String): Boolean {
//                        projectNameLike = newText.trim()
//                        doSearch()
//                        return true
//                    }
//                })
//            }
//
//
//        }
//
//        @SuppressLint("RestrictedApi")
//        override fun onPrepareMenu(menu: Menu) {
//            super.onPrepareMenu(menu)
//            if (menu.javaClass.simpleName.equals("MenuBuilder")) {
//                Log.i(javaClass.simpleName, "onPrepareMenu: ")
//                val menuBuilder = menu as MenuBuilder
//                try {
//                    val m = menuBuilder.javaClass.getDeclaredMethod(
//                        "setOptionalIconsVisible", java.lang.Boolean.TYPE
//                    )
//                    if (m.isAccessible)
//                        menuBuilder.setOptionalIconsVisible(true)
//                    else {
//                        m.isAccessible = true
//                        m.invoke(menu, true)
//                    }
//                } catch (e: NoSuchMethodException) {
//                    Log.e(javaClass.simpleName, "onMenuOpened", e)
//                } catch (e: Exception) {
//                    throw RuntimeException(e)
//                }
//            }
//
////            val searchImgId = resources.getIdentifier("android:id/search_button", null, null)
//            val v = searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
//            v.setImageResource(R.drawable.magnifier)
//            searchView.isIconified = true
//
//            menu.performIdentifierAction(R.id.search, 0)
//        }
//
//        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//            return when (menuItem.itemId) {
//                R.id.filter -> {
//                    showFilters()
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }

    var projectNameLike = ""

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.projects_freelancers_menu, menu)
//        // Associate searchable configuration with the SearchView
//        val searchManager = mainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val menuItem = menu.findItem(R.id.search)
////        searchView = SearchView(mainActivity.supportActionBar!!.themedContext)
//        searchView = menuItem.actionView as SearchView
////        searchView.queryHint = if (recyclerView.adapter is ProjectsRVadapter) "Searching Projects" else "Searching Freelancers"
//        searchView.apply {
//            queryHint = "البحث عن المشاريع"
//            setBackgroundResource(R.drawable.bg_5_curved_white_borders_transparent_filled)
//            setSearchableInfo(searchManager.getSearchableInfo(mainActivity.componentName))
//            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String): Boolean {
//                    projectNameLike = query.trim()
//                    // Project(project_name = query)
//                    doSearch()
//                    searchView.clearFocus()
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String): Boolean {
//                    projectNameLike = newText.trim()
//                    doSearch()
//                    return true
//                }
//            })
////            setOnClickListener { clickedView -> }
//        }
//    }


//    private fun doSearch() {
//        if (projectNameLike.isNotEmpty())
//            queryMap.put("project_name", projectNameLike)
//
//        projectsVM.getAllProjects()
//            .observe(viewLifecycleOwner, requestObserver)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.filter -> {
//                showFilters()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


    private fun initViews(createdView: View) {
        recyclerView = createdView.findViewById(R.id.recyclerView)
        prev_btn = createdView.findViewById(R.id.prev_btn)
        next_btn = createdView.findViewById(R.id.next_btn)
        pagingCtrlLL = createdView.findViewById(R.id.pagingCtrlLL)
//        add_btn = createdView.findViewById(R.id.AddButton)

    }


    lateinit var recyclerView: RecyclerView
    lateinit var prev_btn: ImageButton
//    lateinit var add_btn: Button
    lateinit var next_btn: ImageButton
    lateinit var pagingCtrlLL: LinearLayout

    override fun onProjectClicked(newProject: NewProject) {
        // action_exploreMainFragment_to_projectDetailsFragment
        // action_exploreProjectsFragment_to_projectDetailsFragment

        activityVM.newProjectMLD.postValue(newProject)
        if (view != null) outerFragment.navigateToProjectDetails(newProject)
//        findNavController().navigate(
//            R.id.action_exploreProjectsFragment_to_projectDetailsFragment,
//            bundleOf(ProjectDetailsFragment.projectIdArgKey to newProject.id)
//        )
    }

    override fun onSendQuotClicked(project: NewProject) {

        if (project.numberOfBidding != null && project.numberOfOffer != null &&
            project.numberOfBidding < project.numberOfOffer.toInt()
        ) {
            activityVM.newProjectMLD.postValue(project)
            outerFragment.navigateToAddBid(bundleOf(AddEditQuotationFragment.projectIdArgKey to project.id))

//        AddEditQuotationFragment(projectId, _user!!.freelancer!!, this)
//            .show(
//                mainActivity.supportFragmentManager,
//                AddEditQuotationFragment.TAG
//            )
        } else {
            showAlertDialog(
                title = "عدد العروض مكتمل",
                message = "لا يمكن تقديم عرض سعر جديد",
                positiveText = getString(R.string.ok),
                positiveOnClick = { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
            )
        }

    }

    override fun onFavoriteClicked(project: NewProject, position: Int) {
        var msg = ""
        val isFav = project.favourite == true
        if (isFav) {
            msg = "تم حذف المشروع من المفضلة"
        } else {
            msg = "تم إضافة المشروع إلى المفضلة"
        }

        // favouriteProject?freelancerId=7&isFavourite=true&projectId=4
        favouriteProjectsVM.favouriteProject(
            mapOf(
                "freelancerId" to _user!!.userId.toString(),//_user!!.userId.toString(),
                "isFavourite" to (!isFav).toString(),
                "projectId" to project.id!!.toString()
            )
        ).observe(viewLifecycleOwner) { res ->
            newHandleSuccessOrErrorResponse(res, { favouriteProject ->
                project.favourite = favouriteProject.favourite
                mainActivity.snackbar
//                    .addCallback(object :
//                        BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                        override fun onDismissed(
//                            transientBottomBar: Snackbar?,
//                            event: Int
//                        ) {
//                            super.onDismissed(transientBottomBar, event)
//                        }
//                    })
                    .setText(msg)
                    .show()
                projectsRVadapter.updateFavState(position)

            }) {
                mainActivity.snackbar
                    .setText(R.string.something_went_wrong_please_try_again_later)
                    .show()
            }
        }

    }

    override fun showInsufficientLevel() {
        FreelancerAttemptToSubmitBidDialogFragment().show(
            childFragmentManager,
            FreelancerAttemptToSubmitBidDialogFragment.tag
        )
    }

    override fun showFilters() {
        val projectsFilterBottomSheetModal = ProjectsFilterBottomSheetModal(this, filter)
        mainActivity = requireActivity() as MainActivity
        val bottomSheetBehavior =
            BottomSheetBehavior.from(mainActivity.bottomSheetModal)
//        val bottomSheetBehavior = (modalBottomSheet.dialog as BottomSheetDialog).behavior
//        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
//                    BottomSheetBehavior.STATE_COLLAPSED -> Log.i(
//                        sa.gov.ksaa.dal.TAG,
//                        "onStateChanged: STATE_COLLAPSED"
//                    )
//
//                    BottomSheetBehavior.STATE_EXPANDED -> Log.i(
//                        sa.gov.ksaa.dal.TAG,
//                        "onStateChanged: STATE_EXPANDED"
//                    )
//
//                    BottomSheetBehavior.STATE_HALF_EXPANDED -> Log.i(
//                        sa.gov.ksaa.dal.TAG,
//                        "onStateChanged: STATE_HALF_EXPANDED"
//                    )
//
//                    BottomSheetBehavior.STATE_HIDDEN -> Log.i(
//                        sa.gov.ksaa.dal.TAG,
//                        "onStateChanged: STATE_HIDDEN"
//                    )
//
//                    BottomSheetBehavior.STATE_DRAGGING -> Log.i(
//                        sa.gov.ksaa.dal.TAG,
//                        "onStateChanged: STATE_HIDDEN"
//                    )
//
//                    BottomSheetBehavior.STATE_SETTLING -> Log.i(
//                        sa.gov.ksaa.dal.TAG,
//                        "onStateChanged: STATE_SETTLING"
//                    )
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                // Do something for slide offset.
//            }
//        }
//
//        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        projectsFilterBottomSheetModal.show(
            mainActivity.supportFragmentManager,
            ProjectsFilterBottomSheetModal.TAG
        )

    }

    fun showSendQuot(projectId: Int) {


    }

    var filter: ProjectsFilter? = null

    override fun onFilterSubmitted(searchfilter: ProjectsFilter?) {
        filter = searchfilter
        projectsRVadapter.setFilter(filter)
    }

    override fun submitQuotation(qutation: Qutation) {
        showAlertDialog(
            message = getString(R.string.your_quotation_has_been_submitted_successfully),
            positiveText = getString(R.string.ok),
            positiveOnClick = { dialogInterface, i ->
                dialogInterface.dismiss()
            }
        )
    }


    var searchQ: String? = null
    override fun doSearch(q: String) {
        searchQ = q
        val projectsLD =
     projectsVM.getSearchProject(mapOf("search" to searchQ.toString()))
//        projectsRVadapter.search(projectfilter)


        projectsLD.observe(viewLifecycleOwner, requestObserver)

    }

    override fun reset() {
        projectsRVadapter.filter()
    }

    override fun onResume() {
        super.onResume()
        val projectsLD =
            if (_user == null)
                projectsVM.getAllProjects()
            else
               if (_user!!.isFreelancer())
                projectsVM.getAllProjectsByRequesterUserId(mapOf("userId"  to _user!!.userId.toString()))
                else projectsVM.getAllProjects()



        projectsLD.observe(viewLifecycleOwner, requestObserver)


    }
}