package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Bid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewProjectBidsRVadapter(
    private var projectBids: MutableList<NewBid>,
    val newBidsOnClickListener: NewBidsOnClickListener,
    override var context: Context)
    : RecyclerView.Adapter<NewProjectBidsRVadapter.ViewHolder>(), MyRecyclerViewAdapter{


    val dateFormatAr = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
    var simpleDateFormatEn = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIV: ImageView
        var projectTitleTV: TextView
        var clientNameTV: TextView
        var projectStatusTV: TextView
//        var paymentStatusTV: TextView
        var pubDateTV: TextView
        // var invoiceBtn: Button
        var detailsBtn: Button
        val arabicLocale = Locale("ar", "SA")
        var dateFormatAr = DateFormat.getDateInstance(DateFormat.DEFAULT, arabicLocale)
        init {
            userIV = itemView.findViewById(R.id.userIV)
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
            clientNameTV = itemView.findViewById(R.id.clientNameTV)
            projectStatusTV = itemView.findViewById(R.id.projectStatusTV)
//            paymentStatusTV = itemView.findViewById(R.id.paymentStatusTV)
            pubDateTV = itemView.findViewById(R.id.pubDateTV)
            // invoiceBtn = itemView.findViewById(R.id.invoiceBtn)
            detailsBtn = itemView.findViewById(R.id.detailsBtn)
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_new_projects_freelancer, parent, false)
        return ViewHolder(view)
    }

    var client: NewUser? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val project = projectBids[position].project
        val bid = projectBids[position]
        holder.apply {

            setUserImage(bid.clientImageUrl, userIV, NewUser.CLIENT_USER_TYPE, bid.gender)
            userIV.setOnClickListener {
//                newBidsOnClickListener.onCientPhotoClicked(bid.userId)
            }
            projectTitleTV.text = bid.projectTitle
//            client = projectBids[position].user
            clientNameTV.text = "${bid.clientFirstName} ${bid.clientLastName}"

//            paymentStatusTV.text = project?.working_project?.pay_status
            pubDateTV.text =  simpleDateFormatEn.parse(bid.createdDate?: simpleDateFormatEn.format(Date()))
                ?.let { dateFormatAr.format(it) }


//            invoiceBtn.setOnClickListener{
//                newBidsOnClickListener.onInvoiceClicked(projectBids[position])
//            }
            detailsBtn.setOnClickListener{
                newBidsOnClickListener.onDetailsClicked(bid)
            }
        }

    }


    override fun getItemCount(): Int {
        return projectBids.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newProjectBids: List<NewBid>?): NewProjectBidsRVadapter {
        if (newProjectBids != null) {
            projectBids.addAll(newProjectBids)
            notifyDataSetChanged()
        }
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newProjectBids: List<NewBid>?): NewProjectBidsRVadapter {
        if (newProjectBids != null) {
            newProjectBids.toMutableList().also { projectBids = it }
            notifyDataSetChanged()
        }
        return this
    }


    interface NewBidsOnClickListener {
        fun  onInvoiceClicked(bid: Bid)
        fun  onDetailsClicked(bid: NewBid)
        fun  onCientPhotoClicked(userId: Int)
    }

}