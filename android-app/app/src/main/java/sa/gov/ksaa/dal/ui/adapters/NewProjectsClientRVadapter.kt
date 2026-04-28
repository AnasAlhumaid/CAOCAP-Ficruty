package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import kotlin.Int

class NewProjectsClientRVadapter(private var projects: MutableList<NewProject>,
                                 private val onClickListener: OnClickListener
) :
RecyclerView.Adapter<NewProjectsClientRVadapter.ViewHolder>() {

    val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
    val numberFormat = NumberFormat.getInstance(Locale("ar", "SA"))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var projectTitleTV: TextView
        var freelancerNameTV: TextView
        var projectStatusTV: TextView
        var costTV: TextView
        var projectStartDateTV: TextView
        var remainingDaysTV: TextView
        var detailsBtn: Button

        init {
            freelancerNameTV = itemView.findViewById(R.id.freelancerNameTV)
            projectStatusTV = itemView.findViewById(R.id.projectStatusTV)
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
            costTV = itemView.findViewById(R.id.costTV)
            projectStartDateTV = itemView.findViewById(R.id.projectStartDateTV)
            remainingDaysTV = itemView.findViewById(R.id.remainingDaysTV)
            detailsBtn = itemView.findViewById(R.id.detailsBtn)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_new_projects_client, parent, false)
        return ViewHolder(view)
    }

    lateinit var _user: NewUser
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.apply {
            freelancerNameTV.text = project.clientName ?: "عبد الله الغامدي"
            projectTitleTV.text = project.projectTitle ?: "مشروع"
            projectStatusTV.text = project.status.toString()
            costTV.text = project.amount ?: "100"
            projectStartDateTV.text = dateFormat.format(project.expectedTime?: Date())
            remainingDaysTV.text = project.durationOfProject ?: "20"

            detailsBtn.setOnClickListener {
                onClickListener.onClicked(project)
            }
        }
    }


    override fun getItemCount(): Int {
        return projects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<NewProject>): NewProjectsClientRVadapter {
        newBids.toMutableList().also { projects = it }
        notifyDataSetChanged()
        return this
    }


    interface OnClickListener {
        fun onClicked(project: NewProject)
    }
}
