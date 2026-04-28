package sa.gov.ksaa.dal.ui.fragments.freelancers

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.models.FreelancerFilter
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.ui.MainActivity
import sa.gov.ksaa.dal.ui.adapters.FreelancersRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.ExploreFragment
import sa.gov.ksaa.dal.ui.viewModels.FavouriteFreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM

class ExploreFreelancersFragment(): BaseFragment(R.layout.fragment_freelancers),
    FreelancersFilterBottomSheetModal.OnFilterSetListener, FreelancersRVadapter.OnClickListener,
    ExploreFragment.Searcher{

    constructor(outerFragment: ExploreFragment) : this() {
        this.outerFragment = outerFragment
    }
    lateinit var outerFragment: ExploreFragment
    val freelancersVM: FreelancersVM by viewModels()
    val favouriteFreelancersVM: FavouriteFreelancersVM by viewModels()

//    val menuProvider = object : MenuProvider {
//        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//            menuInflater.inflate(R.menu.projects_f0reelancers_menu, menu)
//            // Associate searchable configuration with the SearchView
//            val searchManager = mainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//            val menuItem = menu.findItem(R.id.search)
////        searchView = SearchView(mainActivity.supportActionBar!!.themedContext)
//            searchView = menuItem.actionView as SearchView
////        searchView.queryHint = if (recyclerView.adapter is ProjectsRVadapter) "Searching Projects" else "Searching Freelancers"
//
//            searchView.apply {
//                queryHint = "البحث عن مقدمي الخدمات"
//                setBackgroundResource(R.drawable.bg_5_curved_white_borders_transparent_filled)
//                setSearchableInfo(searchManager.getSearchableInfo(mainActivity.componentName))
//                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                    override fun onQueryTextSubmit(query: String): Boolean {
////                        vm.getAllFreeLancers(mapOf("user_full_name" to query.trim()) )
////                            .observe(viewLifecycleOwner, freelancersObserver)
//                        return true
//                    }
//                    override fun onQueryTextChange(newText: String): Boolean {
////                        vm.getAllFreeLancers(mapOf("user_full_name" to newText.trim()) )
////                            .observe(viewLifecycleOwner, freelancersObserver)
//                        return true
//                    }
//                })
////            setOnClickListener { clickedView -> }
//            }
//        }
//
//        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//            return when (menuItem.itemId) {
//                R.id.filter -> {
//                    showFilters()
//                    true
//                }
//                else -> false
//            }
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
////            val searchImgId = resources.getIdentifier("android:id/search_button", null, null)
//            val v = searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
//            v.setImageResource(R.drawable.magnifier)
//            searchView.isIconified = true
//
//            menu.performIdentifierAction(R.id.search, 0)
//        }
//    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        mainActivity.toolbar.title = null
//        (mainActivity as MenuHost).addMenuProvider(menuProvider, viewLifecycleOwner)

        initViews(createdView)


        recyclerView.layoutManager = GridLayoutManager(context, 2)
        Log.i(javaClass.simpleName, "onViewCreated: _user = $_user")
        freelancersRVadapter = FreelancersRVadapter(mutableListOf(), this, _user, requireContext())
        recyclerView.adapter = freelancersRVadapter


        freelancersVM.freelacersLD.removeObservers(viewLifecycleOwner)
        freelancersVM.getAllFreeLancers()
            .observe(viewLifecycleOwner, freelancersObserver)

    }

    var freelancersRVadapter: FreelancersRVadapter? =null
    val freelancersObserver = Observer<NewResource<List<NewFreelancer>>> {
        newHandleSuccessOrErrorResponse(it, { freelancersList->
            freelancersRVadapter?.setList(freelancersList)
        })
    }

    lateinit var searchView: SearchView

    fun navigateToFreelancerProfile(freelancer: NewFreelancer) {
        activityVM.freelancerMLD.postValue(freelancer)
        outerFragment.navigateToFreelancerProfile(freelancer)
    }

    private fun initViews(createdView: View) {
        recyclerView = createdView.findViewById(R.id.recyclerView)
//        prev_btn = createdView.findViewById(R.id.prev_btn)
//        next_btn = createdView.findViewById(R.id.next_btn)
    }

    lateinit var recyclerView: RecyclerView

//    lateinit var prev_btn: ImageButton
//    lateinit var next_btn: ImageButton

    override fun showFilters() {
        val filterBottomSheetModal = FreelancersFilterBottomSheetModal(this, filter)
        val bottomSheetBehavior = BottomSheetBehavior.from((requireActivity() as MainActivity).bottomSheetModal)
//        val bottomSheetBehavior = (modalBottomSheet.dialog as BottomSheetDialog).behavior

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> Log.i(TAG, "onStateChanged: STATE_COLLAPSED")
                    BottomSheetBehavior.STATE_EXPANDED -> Log.i(TAG, "onStateChanged: STATE_EXPANDED")
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> Log.i(TAG, "onStateChanged: STATE_HALF_EXPANDED")
                    BottomSheetBehavior.STATE_HIDDEN -> Log.i(TAG, "onStateChanged: STATE_HIDDEN")
                    BottomSheetBehavior.STATE_DRAGGING -> Log.i(TAG, "onStateChanged: STATE_HIDDEN")
                    BottomSheetBehavior.STATE_SETTLING -> Log.i(TAG, "onStateChanged: STATE_SETTLING")
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset.
                Log.i(TAG, "onSlide: slideOffset = $slideOffset")
            }
        })

        filterBottomSheetModal.show(mainActivity.supportFragmentManager,
            FreelancersFilterBottomSheetModal.TAG
        )

    }



    fun favouriteAfreelancer(freelancer: NewFreelancer) {

    }

    override fun onProfileBtnClicked(freelancer: NewFreelancer) {
        activityVM.freelancerMLD.postValue(freelancer)
        if (freelancer.id != null) navigateToFreelancerProfile(freelancer)
    }

    override fun onFavoriteBtnClicked(freelancer: NewFreelancer, position: Int) {
        if (_user == null) {
            activitySnackbar.setText("يرجى تسجيل الدخول")
                .show()
//            Snackbar.make(
//                requireView(),
//                "يرجى تسجيل الدخول",
//                Snackbar.LENGTH_SHORT
//            )
//                .show()
        } else if (freelancer.favourite == true) {
            // clientId=6&isFavourite=true&freelancerId=2
            favouriteFreelancersVM.favouriteFreelancer(
                mapOf(
                    "clientId" to _user!!.userId.toString(),
                    "freelancerId" to (freelancer.userId).toString(), // freelancer.freelancerId?: freelancer.id?:
                    "isFavourite" to "false"))
                .observe(viewLifecycleOwner) {
                    newHandleSuccessOrErrorResponse(it, {
                        freelancersRVadapter?.setFavorite(position, false)
                        activitySnackbar.setText("تم حذف مقدم الخدمة من المفضلة")
                            .show()

                    })
                }
        } else {
            Log.i(TAG, "favouriteAfreelancer: freelancer = $freelancer, _user = $_user")
            // clientId=6&isFavourite=true&freelancerId=2
            favouriteFreelancersVM.favouriteFreelancer(
                mapOf(
                    "clientId" to _user!!.userId.toString(),
                    "isFavourite" to "true",
                    "freelancerId" to(freelancer.userId).toString())) // freelancer.userId?: freelancer.id?:
                .observe(viewLifecycleOwner) {
                    newHandleSuccessOrErrorResponse(it, {
                        freelancersRVadapter?.setFavorite(position, true)
                        activitySnackbar.setText("تم اضافة مقدم الخدمة للمفضلة")
                            .show()
                    })
                }
        }

    }

    override fun onFreelancerSelected(freelancer: NewFreelancer) {

    }

    override fun onFreelancerUnselected(freelancer: NewFreelancer) {

    }

    var searchQ: String? = null
    override fun doSearch(q: String) {
        searchQ = q.trim()
        freelancersRVadapter?.search(searchQ)


    }

    override fun reset() {
        freelancersRVadapter?.filter()
    }

    var filter: FreelancerFilter? = null


    override fun onResume() {
        super.onResume()
        val freelancer =

        if (_user != null &&_user!!.isClient()){
            freelancersVM.getAllFreeLancersFavorateForClient(mapOf("userId" to _user!!.userId.toString()))

        }else {

            freelancersVM.getAllFreeLancers()

        }

        freelancer.observe(viewLifecycleOwner, freelancersObserver)

    }

    override fun onFilterSubmitted(searchFilter: FreelancerFilter?) {
        filter = searchFilter
        freelancersRVadapter?.setFilter(filter)
    }

    override fun onSwitchClicked(name: String) {
//       val freelan =  freelancersVM.getFillterdFreelancers(mapOf("filter" to "\"${name}\"" ))
//
//
//            freelan.observe(viewLifecycleOwner,freelancersObserver)

        }

    }



