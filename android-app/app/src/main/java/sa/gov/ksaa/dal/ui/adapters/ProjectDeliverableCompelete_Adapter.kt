package sa.gov.ksaa.dal.ui.adapters



import android.annotation.SuppressLint
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.Int

class ProjectDeliverableCompelete_Adapter(
    private var deliverables: MutableList<NewDeliverableFile>,
    val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<ProjectDeliverableCompelete_Adapter.ViewHolder>(){

    val dateFormatAr = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
    var simpleDateFormatEn = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateTV: TextView
        var descriptionTV: TextView
        var fileNameTV: TextView


        init {
            dateTV = itemView.findViewById(R.id.dateTV)
            descriptionTV = itemView.findViewById(R.id.descriptionTV)
            fileNameTV = itemView.findViewById(R.id.fileNameTV)

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_project_complete, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val deliverable = deliverables[position]

        holder.apply {
            dateTV.text = simpleDateFormatEn.parse(deliverable.createdDate?: simpleDateFormatEn.format(Date()))
                ?.let { dateFormatAr.format(it) }
            descriptionTV.text = deliverable.fileDescription
            fileNameTV.text = deliverable.fileName

        }


        holder.itemView.setOnClickListener {
            onClickListener.onDeliverableClicked(deliverable)
        }
    }


    override fun getItemCount(): Int {
        return deliverables.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<NewDeliverableFile>): ProjectDeliverableCompelete_Adapter {
        newBids.toMutableList().also { deliverables = it }
        notifyDataSetChanged()
        return this
    }

    fun addDeliverable(projectDeliverable: NewDeliverableFile): ProjectDeliverableCompelete_Adapter {
        if (deliverables.add(projectDeliverable)) notifyItemInserted(deliverables.size-1)
        return this
    }

    interface OnClickListener {
        fun onDeliverableClicked(deliverable: NewDeliverableFile)
        fun onDeleteDeliverabl(deliverable: NewDeliverableFile)
    }
}