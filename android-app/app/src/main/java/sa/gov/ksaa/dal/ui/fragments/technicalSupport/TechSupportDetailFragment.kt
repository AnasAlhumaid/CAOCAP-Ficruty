package sa.gov.ksaa.dal.ui.fragments.technicalSupport


import android.os.Bundle
import android.view.View
import android.widget.TextView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import sa.gov.ksaa.dal.ui.fragments.BaseFragment

class TechSupportDetailFragment : BaseFragment(R.layout.fragment_tech_support_detail) {

    private lateinit var tickets: TechnicalSupportRequest
    lateinit var idTV: TextView
    lateinit var ticketTypeTV: TextView
    lateinit var refranceNumberTV: TextView
    lateinit var ticketsDiscTV: TextView
    lateinit var ticketSateTV: TextView

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.VISIBLE

        this@TechSupportDetailFragment.tickets = activityVM.ticketMLD.value!!
        initViews(createdView)
        updateUI()


    }
    private fun initViews(createdView: View) {
        idTV = createdView.findViewById(R.id.idTV)
        ticketTypeTV = createdView.findViewById(R.id.ticketTypeTV)
        refranceNumberTV = createdView.findViewById(R.id.refranceNumberTV)
        ticketsDiscTV = createdView.findViewById(R.id.ticketsDiscTV)
        ticketSateTV =  createdView.findViewById(R.id.ticketSateTV)
    }

    fun updateUI(){


        idTV.text = convertArabicNumbersToString(tickets.ticketNumber.toString())


        refranceNumberTV.text = convertArabicNumbersToString(tickets.referenceCode.toString())
        ticketTypeTV.text = tickets.category.toString()

        ticketsDiscTV.text = tickets.description.toString()

        if (tickets.ticketStatus == "in-progress"){
            ticketSateTV.text = "قيد المعالجة"
        }else {
            ticketSateTV.text = tickets.ticketStatus.toString()
        }
    }



}