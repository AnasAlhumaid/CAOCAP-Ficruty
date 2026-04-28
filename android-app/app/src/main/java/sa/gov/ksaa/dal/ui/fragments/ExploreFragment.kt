package sa.gov.ksaa.dal.ui.fragments

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.ui.fragments.freelancers.ExploreFreelancersFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.fragments.projects.explore.ProjectsFragment

class ExploreFragment : BaseFragment(R.layout.fragment_explore){


    private var q = ""
    private lateinit var freelancers_tab: TextView
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ExploreFragment
    private lateinit var projects_tab: TextView
//    private lateinit var scrollView: ScrollView
    private lateinit var linearLayout: LinearLayout
    private lateinit var topLinearLayout: LinearLayout
    private lateinit var viewPager: ViewPager2
     lateinit var addP_btn : Button

    val projectsFragment = ProjectsFragment(this)
    val freelancersFragment = ExploreFreelancersFragment(this)
    var currentSearcher: Searcher = freelancersFragment

    fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment?, frameId: Int) {
        val transaction: FragmentTransaction = manager.beginTransaction()
        if (fragment != null) {
            transaction.add(frameId, fragment)
        }
        transaction.commit()
    }

    fun View.getLocationOnScreen(): Point {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return Point(location[0],location[1])
    }



    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.VISIBLE
bottomNavigationView.visibility = View.VISIBLE

        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        linearLayout = createdView.findViewById(R.id.browsingItems)
        topLinearLayout = createdView.findViewById(R.id.constraintLayout)
        mainActivity.replace(freelancersFragment,R.id.browsingItems)
        addP_btn = createdView.findViewById(R.id.AddButton)

//        viewPager = createdView.findViewById(R.id.viewPager2)
//        scrollView = createdView.findViewById(R.id.srcoolView)



//        if (oldScrollY > 0) {
//            linearLayout.visibility = View.GONE
//        } else {
//            linearLayout.visibility = View.VISIBLE
//        }
//        val viewPagerAdapter =
//            ViewPagerAdapter2(arrayListOf(freelancersFragment, projectsFragment),
//                requireActivity().supportFragmentManager, lifecycle)
//
//        viewPager.adapter = viewPagerAdapter

        addP_btn.setOnClickListener {
            val action = ExploreFragmentDirections.actionExploreFragmentToAddEditProjectFragment()

            findNavController().safeNavigateWithArgs(action,null)
        }

        freelancers_tab = createdView.findViewById(R.id.freelancers_tab)

        freelancers_tab.setOnClickListener {
            // action_exploreFragment_to_exploreMainFragment
//            findNavController().navigate(R.id.action_exploreFragment_to_exploreFreelancersFragment)
            //  bundleOf("show" to ExploreMainFragment.showFreelancers)

            freelancers_tab.isSelected = true
            projects_tab.isSelected = false
//            viewPager.currentItem = 0
            currentSearcher = freelancersFragment
            searchView.queryHint = "البحث عن مقدمي الخدمات"

            mainActivity.toolbar.navigationIcon = null
            mainActivity.replace(freelancersFragment,R.id.browsingItems)
            if (_user != null && _user!!.isClient()){
                addP_btn.visibility = View.GONE
            }else{
                addP_btn.visibility = View.GONE
            }



            freelancersFragment.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    // onScrollStateChanged will be fired every time you scroll
                    // Perform your operation here


                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.d("db","scrolling up")
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = (recyclerView.adapter?.itemCount ?: 0) - 1
                    if (lastVisibleItemPosition == itemTotalCount && !recyclerView.canScrollVertically(1) ) {
                  topLinearLayout.visibility = View.INVISIBLE
                    }
                }
            })


        }
        freelancers_tab.isSelected = true
        if ( _user != null && _user!!.isClient()){

                addP_btn.visibility = View.GONE

        }else{
            addP_btn.visibility = View.GONE
        }


        projects_tab = createdView.findViewById(R.id.projects_tab)
        projects_tab.setOnClickListener{
//            findNavController().navigate(R.id.action_exploreFragment_to_exploreProjectsFragment)
            projects_tab.isSelected = true
            freelancers_tab.isSelected = false
//            viewPager.currentItem = 1
            currentSearcher = projectsFragment
            searchView.queryHint = "البحث عن الخدمات"

            if (_user != null && _user!!.isClient() ){
                addP_btn.visibility = View.VISIBLE
            }else{
                addP_btn.visibility = View.GONE
            }


            mainActivity.replace(projectsFragment,R.id.browsingItems)
        }

//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                Log.i(TAG, "onPageSelected: position = $position")
//                when (viewPager.currentItem) {
//                    0 -> {
//                        freelancers_tab.callOnClick()
//
//                    }
//                    1-> {
//                        projects_tab.callOnClick()
//                    }
//                }
//
//            }
//        })


        (mainActivity as MenuHost).addMenuProvider(menuProvider, viewLifecycleOwner)


        // This callback is only called when MyFragment is at least started
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner,onBackPressedCallback)
    }

    lateinit var searchView: SearchView

    val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.projects_freelancers_menu, menu)
            // Associate searchable configuration with the SearchView
            val searchManager =
                mainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

            val menuItem = menu.findItem(R.id.search)


//        searchView = SearchView(mainActivity.supportActionBar!!.themedContext)
            searchView = menuItem.actionView as SearchView

menuItem.expandActionView()
            searchView.doOnLayout {
                searchView.clearFocus()
            }

//        searchView.queryHint = if (recyclerView.adapter is ProjectsRVadapter) "Searching Projects" else "Searching Freelancers"

            searchView.apply {
                queryHint = "البحث عن مقدمي الخدمات"
                setBackgroundResource(R.drawable.bg_10_curved_white_borders_transparent_filled)
                setSearchableInfo(searchManager.getSearchableInfo(mainActivity.componentName))

                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {

                        q = query.trim()
                        if (q.isNotEmpty()){
                            doSearch()
                        }

                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        q = newText.trim()
                        Log.w(TAG, "onQueryTextChange: q = $q")
                        if (q.isNotEmpty())
                            doSearch()
                        else
                            reset()
                        return true
                    }
                })
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
//            val v = searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
//            v.setImageResource(R.drawable.magnifier)
//            searchView.isIconified = true

            menu.performIdentifierAction(R.id.search, 0)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.filter -> {
                    currentSearcher.showFilters()
                    true
                }

                else -> false
            }
        }
    }

    private fun reset() {
        currentSearcher.reset()
    }

    private fun doSearch() {
        Log.i(javaClass.simpleName, "doSearch: q = $q")
        currentSearcher.doSearch(q)
    }

    fun navigateTo(id: NavDirections, bundle: Bundle?){
        findNavController().safeNavigateWithArgs(id,bundle)
    }

    fun navigateToAddBid(bundleOf: Bundle) {
        lifecycleScope.launchWhenResumed {
            findNavController().
            navigate(R.id.action_exploreFragment_to_addEditQuotationFragment, bundleOf)
        }
    }

    fun navigateToFreelancerProfile(freelancer: NewFreelancer) {
        lifecycleScope.launchWhenResumed {
            findNavController()
                .navigate(
                R.id.action_exploreFragment_to_freelancerProfileFragment,
                bundleOf("freelancerId" to freelancer.id)
            )
        }


//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
//
//            }
//        }
    }


    fun navigateToProjectDetails(newProject: NewProject) {
//        lifecycleScope.launchWhenResumed {
//        val action = ExploreFragmentDirections.actionExploreFragmentToProjectDetailsFragment(newProject.id!!)
//        val action1 = ExploreFragmentDirections.actionExploreFragmentToProjectDetailsFragment(newProject.id!!)
//findNavController().safeNavigateWithArgs(action,null)
//
////            findNavController().navigate(
////                R.id.action_exploreFragment_to_projectDetailsFragment,
////
////            )



        if (view != null){
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED){


                    val action = ExploreFragmentDirections.actionExploreFragmentToProjectDetailsFragment(newProject.id!!)
        val action1 = ExploreFragmentDirections.actionExploreFragmentToProjectDetailsFragment(newProject.id!!)
findNavController().safeNavigateWithArgs(action,null)
                }
            }
        }

    }

    interface Searcher{
        fun doSearch(q: String)

        fun reset()
        fun showFilters()
    }
}


