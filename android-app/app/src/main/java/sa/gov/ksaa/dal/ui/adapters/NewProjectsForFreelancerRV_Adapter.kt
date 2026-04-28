package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Bid
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser

class NewProjectsForFreelancerRV_Adapter(private var projects: MutableList<Project>, val onClickListener: OnClickListener) : RecyclerView.Adapter<NewProjectsForFreelancerRV_Adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var projectTitleTV: TextView
        var clientNameTV: TextView
        var projectStatusTV: TextView
//        var paymentStatusTV: TextView
        var pubDateTV: TextView
//        var invoiceBtn: Button
        var detailsBtn: Button
        init {
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
            clientNameTV = itemView.findViewById(R.id.clientNameTV)
            projectStatusTV = itemView.findViewById(R.id.projectStatusTV)
            // paymentStatusTV = itemView.findViewById(R.id.paymentStatusTV)
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
        val project = projects[position]
        holder.apply {
            projectTitleTV.text = project?.project_name
            client = projects[position].user
            clientNameTV.text = "project?.user?.getFullName()"
            projectStatusTV.text = project?.getStatus()
            pubDateTV.text = project?.getShortCreatedDate()
//            invoiceBtn.setOnClickListener{
//                onClickListener.onInvoiceClicked(project)
//            }
            detailsBtn.setOnClickListener{
                onClickListener.onDetailsClicked(project)
            }
        }

    }


    override fun getItemCount(): Int {
        return projects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newProjects: List<Project>): NewProjectsForFreelancerRV_Adapter {
        newProjects.toMutableList().also { projects = it }
        notifyDataSetChanged()
        return this
    }


    interface OnClickListener {
        fun  onInvoiceClicked(project: Project)
        fun  onDetailsClicked(project: Project)
    }

}