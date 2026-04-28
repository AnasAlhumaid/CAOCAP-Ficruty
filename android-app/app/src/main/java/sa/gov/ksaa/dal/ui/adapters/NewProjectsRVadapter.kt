package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.text.SimpleDateFormat
import java.util.Locale

class NewProjectsRVadapter(private var projects: MutableList<Project>
) :
    RecyclerView.Adapter<NewProjectsRVadapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var projectTitleTV: TextView
        var clientNameTV: TextView
        var projectStatusTV: TextView
        var paymentStatusTV: TextView
        var pubDateTV: TextView
        var detailsBtn: Button
        init {
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
            clientNameTV = itemView.findViewById(R.id.clientNameTV)
            projectStatusTV = itemView.findViewById(R.id.projectStatusTV)
            paymentStatusTV = itemView.findViewById(R.id.paymentStatusTV)
            pubDateTV = itemView.findViewById(R.id.pubDateTV)
            detailsBtn = itemView.findViewById(R.id.detailsBtn)
        }
    }

    lateinit var simpleDateFormat: SimpleDateFormat
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_new_projects_freelancer, parent, false)
        simpleDateFormat = SimpleDateFormat("yy/M/dd", Locale.getDefault())
        return ViewHolder(view)
    }

    var client: NewUser? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            projectTitleTV.text = projects[position].project_name
            client = projects[position].client
            clientNameTV.text = client?.getFullName()
            projectStatusTV.text = projects[position].getStatus()
            paymentStatusTV.text = projects[position].working_project?.pay_status
            pubDateTV.text = simpleDateFormat.format(projects[position].created_date)
            SimpleDateFormat("dd/mm/yy", Locale.getDefault())
            detailsBtn.setOnClickListener{

            }
        }

    }


    override fun getItemCount(): Int {
        return projects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newProjects: List<Project>?): NewProjectsRVadapter {
        if (newProjects != null) {
            projects.addAll(newProjects)
            notifyDataSetChanged()
        }
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newProjects: List<Project>?): NewProjectsRVadapter {
        if (newProjects != null) {
            newProjects.toMutableList().also { projects = it }
            notifyDataSetChanged()
        }
        return this
    }

}