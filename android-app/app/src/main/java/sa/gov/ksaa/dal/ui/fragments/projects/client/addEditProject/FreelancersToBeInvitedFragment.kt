package sa.gov.ksaa.dal.ui.fragments.projects.client.addEditProject

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.models.FreelancerFilter
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.data.webservices.dal.requests.FreelancersInvitation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.ui.adapters.FreelancersSelectionRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.ExploreFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancersFilterBottomSheetModal
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM
import java.util.Date

class FreelancersToBeInvitedFragment : BaseFragment(R.layout.fragment_freelancers_to_be_invited),
    FreelancersSelectionRV_Adapter.OnClickListener,
    FreelancersFilterBottomSheetModal.OnFilterSetListener {
    val freelancersVM: FreelancersVM by viewModels()
    val projectsVM: ProjectsVM by viewModels()

//    val sendFreelancer : InvitedFreelancer = activity as InvitedFreelancer

    lateinit var searchView: SearchView
    lateinit var freelancerSearchET: EditText
    var projectId: Int = 0
    private val sharedViewModel: SharedViewModel by viewModels()



    val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.freelancers_to_be_invited_menu, menu)
            // Associate searchable configuration with the SearchView
            val searchManager =
                mainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val menuItem = menu.findItem(R.id.search)
//        searchView = SearchView(mainActivity.supportActionBar!!.themedContext)
            searchView = menuItem.actionView as SearchView
//        searchView.queryHint = if (recyclerView.adapter is ProjectsRVadapter) "Searching Projects" else "Searching Freelancers"

            searchView.apply {
                queryHint = "البحث عن مقدمي الخدمات"
                setBackgroundResource(R.drawable.bg_5_curved_white_borders_transparent_filled)
                setSearchableInfo(searchManager.getSearchableInfo(mainActivity.componentName))
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
//                        vm.getAllFreeLancers(mapOf("user_full_name" to query.trim()) )
//                            .observe(viewLifecycleOwner, freelancersObserver)
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
//                        vm.getAllFreeLancers(mapOf("user_full_name" to newText.trim()) )
//                            .observe(viewLifecycleOwner, freelancersObserver)
                        return true
                    }
                })
//            setOnClickListener { clickedView -> }
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.filter -> {
                    showFilters()
                    true
                }

                else -> false
            }
        }

        @SuppressLint("RestrictedApi")
        override fun onPrepareMenu(menu: Menu) {
            super.onPrepareMenu(menu)
            if (menu.javaClass.simpleName.equals("MenuBuilder")) {
                Log.i(javaClass.simpleName, "onPrepareMenu: ")
                val menuBuilder = menu as MenuBuilder
                try {
                    val m = menuBuilder.javaClass.getDeclaredMethod(
                        "setOptionalIconsVisible", java.lang.Boolean.TYPE
                    )
                    if (m.isAccessible)
                        menuBuilder.setOptionalIconsVisible(true)
                    else {
                        m.isAccessible = true
                        m.invoke(menu, true)
                    }
                } catch (e: NoSuchMethodException) {
                    Log.e(javaClass.simpleName, "onMenuOpened", e)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
//            val searchImgId = resources.getIdentifier("android:id/search_button", null, null)
            val v = searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
            v.setImageResource(R.drawable.magnifier)
            searchView.isIconified = true

            menu.performIdentifierAction(R.id.search, 0)
        }
    }

    var filter: FreelancerFilter? = null
    fun showFilters() {
        val filterBottomSheetModal = FreelancersFilterBottomSheetModal(this, filter)
        val bottomSheetBehavior = BottomSheetBehavior.from(mainActivity.bottomSheetModal)
//        val bottomSheetBehavior = (modalBottomSheet.dialog as BottomSheetDialog).behavior

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> Log.i(
                            TAG,
                            "onStateChanged: STATE_COLLAPSED"
                        )

                        BottomSheetBehavior.STATE_EXPANDED -> Log.i(
                            TAG,
                            "onStateChanged: STATE_EXPANDED"
                        )

                        BottomSheetBehavior.STATE_HALF_EXPANDED -> Log.i(
                            TAG,
                            "onStateChanged: STATE_HALF_EXPANDED"
                        )

                        BottomSheetBehavior.STATE_HIDDEN -> Log.i(
                            TAG,
                            "onStateChanged: STATE_HIDDEN"
                        )

                        BottomSheetBehavior.STATE_DRAGGING -> Log.i(
                            TAG,
                            "onStateChanged: STATE_HIDDEN"
                        )

                        BottomSheetBehavior.STATE_SETTLING -> Log.i(
                            TAG,
                            "onStateChanged: STATE_SETTLING"
                        )
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // Do something for slide offset.
                    Log.i(TAG, "onSlide: slideOffset = $slideOffset")

                }
            })

        filterBottomSheetModal.show(
            mainActivity.supportFragmentManager,
            FreelancersFilterBottomSheetModal.TAG
        )

    }


    lateinit var project: NewProject
    override fun onActivityCreated() {
        super.onActivityCreated()
        appBarLayout.visibility = View.VISIBLE
//        project = activityVM.projectMLD.value
    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)

        (mainActivity as MenuHost).addMenuProvider(menuProvider, viewLifecycleOwner)

        project = activityVM.newProjectMLD.value!!

        allFreelancersTV.setOnClickListener {
            it.isSelected = !it.isSelected
            completedProjectsFreelancersTV.isSelected = !completedProjectsFreelancersTV.isSelected
            freelancersVM.getAllFreeLancers().observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, { freelancersList ->
//                val freelancers = freelancersList.map { clientFavoriteFreelancer -> clientFavoriteFreelancer.freelancer }
                    updateUI(freelancersList)
                })
            }


            view?.setOnClickListener(sharedViewModel.onClick)

            freelancerSearchET.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    doSearch(p0.toString())
                }

            })

        }

        completedProjectsFreelancersTV.setOnClickListener {
            it.isSelected = !it.isSelected
            allFreelancersTV.isSelected = !it.isSelected

            freelancersVM.getAllFreeLancers() // mapOf("getAllFreeLancers" to _user!!.user_id.toString())
                .observe(viewLifecycleOwner) {
                    newHandleSuccessOrErrorResponse(it, { freelancersList ->
//                val freelancers = freelancersList.map { clientFavoriteFreelancer -> clientFavoriteFreelancer.freelancer }
                        updateUI(freelancersList)
                    })
                }
        }

        recyclerView.layoutManager = GridLayoutManager(context, 1)
        freelancersRVadapter = FreelancersSelectionRV_Adapter(mutableListOf(), this, _user, requireContext())
        recyclerView.adapter = freelancersRVadapter

        //            mapOf("user_id" to _user!!.user_id.toString())
        freelancersVM.getAllFreeLancers().observe(viewLifecycleOwner) {
            newHandleSuccessOrErrorResponse(it, { freelancersList ->
//                val freelancers = freelancersList.map { clientFavoriteFreelancer -> clientFavoriteFreelancer.freelancer }
                updateUI(freelancersList)
            })
        }

        sendInvitationsBtn.setOnClickListener {
            val freelancerSet = freelancersRVadapter.selectedFreelancers
            val userIDsSet = mutableSetOf<Int>()
            val iDsSet = mutableSetOf<Int>()
            freelancerSet.forEach { freelancer ->
                if (freelancer.userId != null) userIDsSet.add(
                    freelancer.userId!!
                )
                if (freelancer.id != null ) iDsSet.add(
                    freelancer.id!!
                )
            }

            if (freelancersRVadapter.selectedFreelancers.isNotEmpty()) {
                val freelancersInvitation = FreelancersInvitation(
                    freelancers_ids =iDsSet ,
                    freelancers_userIds = userIDsSet,
                    project_id = project.id ?: project.projectId
                )
//                sendFreelancer.InvitedFreelancer(freelancersInvitation)
                activityVM.invitedFreelancer.postValue(freelancersInvitation)
                findNavController().popBackStack()
                sharedViewModel.onClick = { view ->

                    // Handle click here
                }

                Log.i(
                    javaClass.simpleName,
                    "onViewCreated: freelancersInvitation = $freelancersInvitation"
                )

                //    addProject?user_id=1&projectTitle=مشروع ترجمة جديد&category=التدقيق&aboutProject=نبذة&
//    descriptionofProject=وصف&projectValue=555&numberOfOffers=7&expectedTime=أسبوع 1&freelancerLevel=نشط&
//    durationOfOffer=2023-10-14&request=general&freelancerIds=

//                val output = project.proejctCategory?.joinToString(separator = ",") { "\"$it\"" }
//                val params = mapOf(
//                    "user_id" to project.userId.toString(),
//                    "projectTitle" to project.projectTitle!!,
//                    "category" to "${output}",
//                    "aboutProject" to project.aboutProject!!,
//                    "descriptionofProject" to project.projectDescription!!,
//                    "projectValue" to project.projectValue!!,
//                    "numberOfOffers" to project.numberOfOffers.toString(),
//                    "expectedTime" to project.expectedTime.toString(),
//                    "freelancerLevel" to project.freelancerLevel!!,
//                    "durationOfOffer" to project.durationOfOffer.toString(),
//                    "request" to project.request!!,
//                    "freelancerIds" to freelancersInvitation.freelancers_ids.toString()
//                        .removeSurrounding("[", "]")
//                )
//                Log.w(TAG, "onViewCreated: params = $params")
//                projectsVM.addNewProject(params)
//                    .observe(viewLifecycleOwner) {
//                        newHandleSuccessOrErrorResponse(it,
//                            onSuccess = { newProject ->
//                                project.clientName = project.clientName ?: _user!!.getFullName()
//                                project.createdDate = project.createdDate ?: Date()
//
//                                project = newProject
//                                activityVM.newProjectMLD.postValue(project)
//                                Snackbar.make(
//                                    requireView(),
//                                    "تم إنشاء المشروع بنجاح",
//                                    Snackbar.LENGTH_SHORT
//                                )
//                                    .show()
//
//                                this@FreelancersToBeInvitedFragment
//                                    .findNavController()
//                                    .navigate(
//                                        R.id.action_freelancersToBeInvitedFragment_to_projectDetailsFragment,
//                                        bundleOf(
//                                            ProjectDetailsFragment.projectIdArgKey to (newProject.id
//                                                ?: newProject.projectId)
//                                        )
//                                    )
//
//                            },
//                            onError = {
//                                showAlertDialog(
//                                    message = it.message
//                                        ?: getString(R.string.something_went_wrong_please_try_again_later)
//                                )
//                                Log.i(
//                                    TAG,
//                                    "onViewCreated: vm.updateProjectById: onError : errorsList =  ${it.errorsList.toString()}"
//                                )
//                            })
//                    }

//                projectInvitatinsVM.inviteFreelancersTo_aProject(freelancersInvitation)
//                    .observe(viewLifecycleOwner) {
//                        handleSuccessOrErrorResponse(it, { data: List<FreelancerInvitedProject> ->
//
//                            findNavController().navigate(
//                                R.id.action_freelancersToBeInvitedFragment_to_projectDetailsFragment,
//                                bundleOf(ProjectDetailsFragment.projectIdArgKey to project!!.project_id)
//                            )
//                        })
//                    }
            }
        }
    }

    private fun doSearch(term: String) {
        if (term.trim().isEmpty())
            freelancersVM.getAllFreeLancers()
                .observe(this@FreelancersToBeInvitedFragment.viewLifecycleOwner) {
                    newHandleSuccessOrErrorResponse(it, { userList ->
                        updateUI(userList)
                    })
                }
    }

    private fun updateUI(freelancersList: List<NewFreelancer>) {
        val mutableFreelancers = freelancersList.toMutableList()

        if (mutableFreelancers.isEmpty()) {
            noDataTV.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            noDataTV.visibility = View.GONE
            freelancersRVadapter.setList(mutableFreelancers)
        }
    }

    lateinit var freelancersRVadapter: FreelancersSelectionRV_Adapter

    fun navigateToFreelancerProfile(freelancerId: Int) {
        findNavController().navigate(
            R.id.action_freelancersToBeInvitedFragment_to_freelancerProfileFragment,
            bundleOf("freelancerUserId" to freelancerId)
        )
    }

    private fun initViews(createdView: View) {
        completedProjectsFreelancersTV =
            createdView.findViewById(R.id.completedProjectsFreelancersTV)
        freelancerSearchET = createdView.findViewById(R.id.freelancerSearchET)
        completedProjectsFreelancersTV.isSelected = true
        allFreelancersTV = createdView.findViewById(R.id.allFreelancersTV)
        allFreelancersTV.isSelected = false
        recyclerView = createdView.findViewById(R.id.recyclerView)
        noDataTV = createdView.findViewById(R.id.noDataTV)
//        prev_btn = createdView.findViewById(R.id.prev_btn)
//        next_btn = createdView.findViewById(R.id.next_btn)
        sendInvitationsBtn = createdView.findViewById(R.id.sendInvitationsBtn)
    }

    lateinit var completedProjectsFreelancersTV: TextView
    lateinit var allFreelancersTV: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var noDataTV: TextView

    //    lateinit var prev_btn: ImageButton
//    lateinit var next_btn: ImageButton
    lateinit var sendInvitationsBtn: ExtendedFloatingActionButton
    override fun onProfileBtnClicked(freelancer: NewFreelancer) {
        activityVM.freelancerMLD.postValue(freelancer)
        navigateToFreelancerProfile(freelancer.id!!)
    }

    override fun onFavoriteBtnClicked(freelancer: NewFreelancer, position: Int) {

    }

    override fun onFreelancerSelected(freelancer: NewFreelancer) {
        Log.i(
            javaClass.simpleName,
            "onFreelancerSelected: selected = ${freelancersRVadapter.selectedFreelancers}"
        )
    }

    override fun onFreelancerUnselected(freelancer: NewFreelancer) {
        Log.i(
            javaClass.simpleName,
            "onFreelancerSelected: selected = ${freelancersRVadapter.selectedFreelancers}"
        )
    }

    override fun onFilterSubmitted(searchFilter: FreelancerFilter?) {
        freelancersRVadapter.filter(searchFilter)
    }

    override fun onSwitchClicked(name: String) {
        TODO("Not yet implemented")
    }

//    fun onUnFavoriteBtnClicked(freelancer: ClientFavoriteFreelancer, position: Int) {
//        vm.unFavourite_aFreelancer(
//            mapOf("user_id" to _user!!.user_id.toString().trim(),
//                "freelance_id" to freelancer.freelance_id.toString().trim())
//        ).observe(viewLifecycleOwner) {
//            handleSuccessOrErrorResponse(
//                it, { data ->
//                    Snackbar.make(requireView(), "تم حذف مقدم الخدمة من المفضلة", Snackbar.LENGTH_SHORT)
//                        .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>(){
//                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
//                                super.onDismissed(transientBottomBar, event)
//                                freelancersRVadapter.deleteFreelancerAt(position)
//                            }
//                        })
//                })
//        }
//    }

    interface OnFreelancerSetListener{
        fun onFreelancerSubmitted(idFreelancers: FreelancersInvitation)

    }
}
class SharedViewModel : ViewModel() {
    var onClick: (View) -> Unit = {}
}
