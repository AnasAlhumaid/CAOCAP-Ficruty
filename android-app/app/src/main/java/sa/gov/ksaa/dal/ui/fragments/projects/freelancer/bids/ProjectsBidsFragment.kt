package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.bids


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Bid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.ui.adapters.NewProjectBidsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.client.ClientProfileFragment
import sa.gov.ksaa.dal.ui.viewModels.BidsVM
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ProjectsBidsFragment : BaseFragment(R.layout.fragment_projects_bids),
    NewProjectBidsRVadapter.NewBidsOnClickListener {

    val bidsVM: BidsVM by viewModels()
    var bidList = mutableListOf<NewBid>()

    lateinit var newProjectBidsRVadapter: NewProjectBidsRVadapter
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE

        initViews(createdView)

        updateUI()
        newProjectBidsRVadapter = NewProjectBidsRVadapter(bidList, this, requireContext())
        freelancerBidsRV.adapter = newProjectBidsRVadapter

        bidsVM.getBidsByFreelancerId(mapOf("freelancerId" to (_user!!.userId?:0).toString()))
            .observe(viewLifecycleOwner){
                newHandleSuccessOrErrorResponse(it, { newBids ->
                    Log.w(javaClass.simpleName, "onViewCreated: bidList = $bidList")
                    bidList = newBids.toMutableList()

                    updateUI()
                })
            }
    }

    private fun updateUI() {
        noBidsTV.text = numberFormat.format(bidList.size)
        if (bidList.isEmpty()){
            noDataTV.visibility = View.VISIBLE
            showMoreBtn.visibility = View.GONE
            freelancerBidsRV.visibility = View.GONE
        } else {
            noDataTV.visibility = View.GONE
//            showMoreBtn.visibility = View.VISIBLE
            freelancerBidsRV.visibility = View.VISIBLE
            newProjectBidsRVadapter.setList(bidList)
        }
    }

    lateinit var noBidsTV: TextView
    lateinit var noDataTV: TextView
    lateinit var freelancerBidsRV: RecyclerView
    lateinit var showMoreBtn: Button


    lateinit var ongoingProjectsBtn: MaterialButton
    lateinit var completedProjectsBtn: MaterialButton
    lateinit var createdProjectsBtn: MaterialButton

    private fun initViews(createdView: View) {
        noBidsTV = createdView.findViewById(R.id.noBidsTV)
        noDataTV = createdView.findViewById(R.id.noDataTV)

        freelancerBidsRV = createdView.findViewById(R.id.bidsRV)
        showMoreBtn = createdView.findViewById(R.id.showMoreBtn)

        ongoingProjectsBtn = createdView.findViewById(R.id.ongoingProjectsBtn)
        ongoingProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsBidsFragment_to_projectsInProgressFragment)
        }
        completedProjectsBtn = createdView.findViewById(R.id.completedProjectsBtn)
        completedProjectsBtn.setOnClickListener {
                findNavController().navigate(R.id.action_projectsBidsFragment_to_completedProjectsFragment)
            }
        createdProjectsBtn = createdView.findViewById(R.id.createdProjectsBtn)
        createdProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsBidsFragment_to_projectInvitationsFragment)
        }

    }

    override fun onInvoiceClicked(bid: Bid) {

    }

    override fun onDetailsClicked(bid: NewBid) {
        activityVM.bidMLD.postValue(bid)
        findNavController().navigate(R.id.action_projectsBidsFragment_to_quotationFragment)
    }

    override fun onCientPhotoClicked(userId: Int) {
        findNavController()
            .navigate(R.id.action_projectsBidsFragment_to_clientProfileFragment,
                bundleOf(ClientProfileFragment.userIdKey to userId)
            )
    }
}