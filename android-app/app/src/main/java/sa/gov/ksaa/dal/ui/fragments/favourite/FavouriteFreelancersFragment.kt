package sa.gov.ksaa.dal.ui.fragments.favourite

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FavouriteFreelancer
import sa.gov.ksaa.dal.ui.adapters.FavouriteFreelancersRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.FavouriteFreelancersVM

class FavouriteFreelancersFragment : BaseFragment(R.layout.fragment_favourite_freelancers),
    FavouriteFreelancersRV_Adapter.OnClickListener {
    val favouriteFreelancersVM: FavouriteFreelancersVM by viewModels()


    override fun onActivityCreated() {
        super.onActivityCreated()

    }
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)
        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        freelancersRVadapter = FavouriteFreelancersRV_Adapter(mutableListOf(), this)
        recyclerView.adapter = freelancersRVadapter


        Log.i(javaClass.simpleName, "onViewCreated: _user = $_user")
        favouriteFreelancersVM.getFavoriteFreelancers(userId = _user!!.userId!!)
            .observe(viewLifecycleOwner) {
            newHandleSuccessOrErrorResponse(it, { freelancersList ->
                 updateUI(freelancersList)
            })
        }
    }

    private fun updateUI(freelancersList: List<FavouriteFreelancer>) {
        if (freelancersList.isEmpty()){
            noDataTV.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            noDataTV.visibility = View.GONE

            freelancersRVadapter.setList(freelancersList)
        }
    }

    lateinit var freelancersRVadapter: FavouriteFreelancersRV_Adapter

    fun navigateToFreelancerProfile(freelancerId: FavouriteFreelancer) {

    }

    private fun initViews(createdView: View) {
        recyclerView = createdView.findViewById(R.id.recyclerView)
        noDataTV = createdView.findViewById(R.id.noDataTV)
        prev_btn = createdView.findViewById(R.id.prev_btn)
        next_btn = createdView.findViewById(R.id.next_btn)
    }

    lateinit var recyclerView: RecyclerView
    lateinit var noDataTV: TextView
    lateinit var prev_btn: ImageButton
    lateinit var next_btn: ImageButton
    override fun onProfileBtnClicked(favouriteFreelancer: FavouriteFreelancer) {
        activityVM.favouriteFreelancerMLD.postValue(favouriteFreelancer)
        findNavController().navigate(
            R.id.action_favouriteFreelancersFragment_to_freelancerProfileFragment,
            bundleOf("freelancerUserId" to favouriteFreelancer.userId)
        )
    }

    override fun onUnFavoriteBtnClicked(favouriteFreelancer: FavouriteFreelancer, position: Int) {
        // clientId=6&isFavourite=true&freelancerId=2
        favouriteFreelancersVM.favouriteFreelancer(
            mapOf(
                "clientId" to _user!!.userId.toString().trim(),
                "isFavourite" to "false",
                "freelancerId" to (favouriteFreelancer.userId).toString().trim())
        ).observe(viewLifecycleOwner) {res ->
            newHandleSuccessOrErrorResponse(
                res, { fav ->
                    freelancersRVadapter.deleteFreelancerAt(position)
                    Snackbar.make(requireView(), "تم حذف مقدم الخدمة من المفضلة", Snackbar.LENGTH_SHORT)
                        .show()
                })
        }
    }
}