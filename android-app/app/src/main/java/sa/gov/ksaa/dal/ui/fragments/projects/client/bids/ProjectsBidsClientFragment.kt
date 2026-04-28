package sa.gov.ksaa.dal.ui.fragments.projects.client.bids


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.GroupedBids
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.ui.adapters.BidsClientRVadapter
import sa.gov.ksaa.dal.ui.adapters.ProjectsBidsClientRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.BidsVM

class ProjectsBidsClientFragment : BaseFragment(R.layout.fragment_projects_bids_cient),
    ProjectsBidsClientRVadapter.OnClickListener, BidsClientRVadapter.OnClickListener  {
    val bidsVM: BidsVM by viewModels()

//    lateinit var projectsBidsClientRVadapter: ProjectsBidsClientRVadapter
//    lateinit var bidsList: MutableList<Pair<Int?, List<NewBid>>>
    lateinit var bidsList: MutableList<GroupedBids>


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE

        initViews(createdView)



//        projectsBidsClientRVadapter = ProjectsBidsClientRVadapter(bidsList, this, requireContext())
//        projectsBidsRV.adapter = projectsBidsClientRVadapter
        bottomNavigationView.visibility = View.GONE


        // user_id=5

//        bidsVM.getBidsClientByUserId(mapOf("user_id" to _user!!.userId.toString())).observe(viewLifecycleOwner) {
//            newHandleSuccessOrErrorResponse(it, { groupedBids ->
////                val newBids = bids.filter { bid ->
////                    bid.projectId != 0
////                }
//             val list =  groupedBids
//
//                bidsClientRVadapter = BidsClientRVadapter(list, this, requireContext())
//                projectsBidsRV.adapter = bidsClientRVadapter
//            })
//        }
        bidsVM.getBidsByUserId(
            mapOf("user_id" to _user!!.userId.toString())
        ).observe(viewLifecycleOwner) {
            newHandleSuccessOrErrorResponse(it, { groupedBids ->
//                val newBids = bids.filter { bid ->
//                    bid.projectId != 0
//                }


                val list =  groupedBids.toMutableList()
                    bidsList = list

                noBidsTV.text = numberFormat.format(list.size)
                    bidsClientRVadapter = BidsClientRVadapter(list, this, requireContext())
                    projectsBidsRV.adapter = bidsClientRVadapter




//                var numberOfBids = 0
//                groupedBids.forEach {
//                    it
//                    numberOfBids += it.listOfTenderRequestDto.size
//                }
//                noBidsTV.text = numberFormat.format(numberOfBids) // newBids.size
//
////                projectsBidsClientRVadapter.setMap(
////                    newBids.groupBy { bid ->
////                            bid.projectId
////                        })
//
////                projectsBidsClientRVadapter.setList(groupedBids)
            })
        }

//        projectsVM.getAllProjects(mapOf("user_id" to _user!!.user_id.toString().trim())) //
//            .observe(viewLifecycleOwner){
//                handleSuccessOrErrorResponse(it, {projects ->
//                    for (project in projects){
//                        if (project.biddings != null)for(bid in project.biddings!!){
//                            bidsList.add(bid)
//                        }
//                    }
//                    updateUI()
//
////                    bidsClientRVadapter.setList(bids)
//                })
//            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUI() {
        noBidsTV.text = numberFormat.format(bidsList.size)
    }

    lateinit var noBidsTV: TextView
    lateinit var noDataTV: TextView
    lateinit var projectsBidsRV: RecyclerView
    lateinit var showMoreBtn: Button

    lateinit var ongoingProjectsBtn: MaterialButton
    lateinit var completedProjectsBtn: MaterialButton
    lateinit var createdProjectsBtn: MaterialButton
    lateinit var waitingProjectsBtn: MaterialButton
    lateinit var bidsClientRVadapter: BidsClientRVadapter


    private fun initViews(createdView: View) {
        noBidsTV = createdView.findViewById(R.id.noBidsTV)
        noDataTV = createdView.findViewById(R.id.noDataTV)
        projectsBidsRV = createdView.findViewById(R.id.projectsBidsRV)
        showMoreBtn = createdView.findViewById(R.id.showMoreBtn)


        ongoingProjectsBtn = createdView.findViewById(R.id.ongoingProjectsBtn)
        ongoingProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsBidsCientFragment_to_ongoingProjectsClientFragment)
        }
        completedProjectsBtn = createdView.findViewById(R.id.completedProjectsBtn)
        completedProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsBidsCientFragment_to_completedProjectsClientFragment)
        }

        createdProjectsBtn = createdView.findViewById(R.id.createdProjectsBtn)
        createdProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsBidsCientFragment_to_allProjectsClientFragment)
        }
//        waitingProjectsBtn = createdView.findViewById(R.id.waitingProjectsBtn)
//        waitingProjectsBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_projectsBidsCientFragmen_to_waitingProjectsClientFragment)
//        }

    }

    override fun onBidClicked(bid: NewBid) {
        activityVM.bidMLD.postValue(bid)
        findNavController().navigate(
            R.id.action_projectsBidsCientFragment_to_projectQuotationDetailsForClientFragment,
            bundleOf(
                ProjectQuotationDetailsForClientFragment.bidIdArgKey to ((bid.id ?: bid.bidId) ?: 0)
            )
        )
    }
}