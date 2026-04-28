package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import kotlin.Int

class TechSupportTicketsRV_Adapter(
    private var tickets: MutableList<TechnicalSupportRequest>,
    private val onClickListener: OnClickListener, override var context: Context
) :
    RecyclerView.Adapter<TechSupportTicketsRV_Adapter.ViewHolder>(),MyRecyclerViewAdapter{



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var ticketSateTV: TextView
        var dateTV: TextView
        var ticketNoTV: TextView
        var idTV: TextView
        var detailsBtn: Button

        init {
            ticketSateTV = itemView.findViewById(R.id.ticketSateTV)
            dateTV = itemView.findViewById(R.id.dateTV)
            ticketNoTV = itemView.findViewById(R.id.refranceNumber)
            idTV = itemView.findViewById(R.id.idTV)
            detailsBtn = itemView.findViewById(R.id.detailsBtn)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_technical_support, parent, false)
        return ViewHolder(view)
    }

    lateinit var _user: NewUser
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.let {
            it.ticketSateTV.text = ticket.category
            it.dateTV.text = dateFormat.format(ticket.createdDate?: Date())

            it.idTV.text = convertArabicNumbersToString(ticket.referenceCode.toString())
        }
        holder.detailsBtn.setOnClickListener {
            onClickListener.onClicked(ticket)
        }
    }


    override fun getItemCount(): Int {
        return tickets.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newTickets: List<TechnicalSupportRequest>): TechSupportTicketsRV_Adapter {
        newTickets.toMutableList().also { tickets = it }
        notifyDataSetChanged()
        return this
    }


    interface OnClickListener {
        fun onClicked(ticket: TechnicalSupportRequest)
    }
}