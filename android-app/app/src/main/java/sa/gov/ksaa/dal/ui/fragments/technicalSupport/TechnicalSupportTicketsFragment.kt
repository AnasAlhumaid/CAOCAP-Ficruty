package sa.gov.ksaa.dal.ui.fragments.technicalSupport

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.TechSupportTicket
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import sa.gov.ksaa.dal.ui.adapters.TechSupportTicketsRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.technicalSupport.addEditTechnicalSupportTicket.AddEditTechnicalSupportTicket
import sa.gov.ksaa.dal.ui.viewModels.ContactUsVM
import sa.gov.ksaa.dal.ui.viewModels.TechnicalSupportVM
import java.util.Date

class TechnicalSupportTicketsFragment : BaseFragment(R.layout.fragment_technical_support_tickets),
    TechSupportTicketsRV_Adapter.OnClickListener {

    lateinit var tickets: MutableList<TechnicalSupportRequest>
    lateinit var techSupportTicketsRV_Adapter: TechSupportTicketsRV_Adapter
    lateinit var closedTicketsCountTV: TextView
    lateinit var openTicketsCountTV: TextView

    val vm: TechnicalSupportVM by viewModels()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE
        initViews(createdView)



//        tickets = mutableListOf(
//            TechSupportTicket(status = TechSupportTicket.REPLIED_STATE, created_date = Date(), sequence_number = 123456),
//            TechSupportTicket(status = TechSupportTicket.REPLIED_STATE, created_date = Date(), sequence_number = 123456),
//            TechSupportTicket(status = TechSupportTicket.REPLIED_STATE, created_date = Date(), sequence_number = 123456),
//            TechSupportTicket(status = TechSupportTicket.REPLIED_STATE, created_date = Date(), sequence_number = 123456))

        techSupportTicketsRV_Adapter = TechSupportTicketsRV_Adapter(mutableListOf(), this,requireContext())
        ticketsRV.adapter = techSupportTicketsRV_Adapter

        newTicketBtn.setOnClickListener {
            AddEditTechnicalSupportTicket().show(parentFragmentManager, AddEditTechnicalSupportTicket.tag)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        vm.getTechSupportRequests(mapOf("userId" to _user!!.userId.toString()))
            .observe(viewLifecycleOwner){
                newHandleSuccessOrErrorResponse(it, {

                    Log.w(javaClass.simpleName, "onViewCreated: it = $it")
                    tickets = it.sortedByDescending { it.createdDate }.toMutableList()



                    techSupportTicketsRV_Adapter.setList(tickets)
                    ticketsCountTV.text = numberFormat.format(tickets.size)
                    closedTicketsCountTV.text = numberFormat.format(tickets.filter {
                        it.isClosed()
                    }.size)

                    openTicketsCountTV.text = numberFormat.format(tickets.filter {
                        !it.isClosed()
                    }.size

                    )
                })
            }


    }

    lateinit var newTicketBtn: Button
    lateinit var ticketsCountTV: TextView
    lateinit var ticketsRV: RecyclerView
    private fun initViews(createdView: View) {
        newTicketBtn = createdView.findViewById(R.id.newTicketBtn)
        ticketsCountTV = createdView.findViewById(R.id.ticketsCountTV)
        closedTicketsCountTV = createdView.findViewById(R.id.closedTicketsCountTV)
        openTicketsCountTV = createdView.findViewById(R.id.openTicketsCountTV)
        ticketsRV = createdView.findViewById(R.id.ticketsRV)
    }

    override fun onClicked(ticket: TechnicalSupportRequest) {

        activityVM.ticketMLD.postValue(ticket)

        findNavController().navigate(R.id.action_technicalSupportTicketsFragment_to_techSupportDetailsFragment

        )
    }
}