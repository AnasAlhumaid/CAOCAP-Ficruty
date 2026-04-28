package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date
import kotlin.Int

class CompletedProjectsRV_Adapter4Client(
    private var closedProjects: MutableList<ClosedProject>,
    private val onClickListener: OnClickListener, override var context: Context
) :
    RecyclerView.Adapter<CompletedProjectsRV_Adapter4Client.ViewHolder>(), MyRecyclerViewAdapter{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var freelancerIV: ImageView
        var nameTV: TextView
        var projectTitleTV: TextView
        var pubDateTV: TextView
        var detailsBtn: MaterialButton
        var reviewBtn: MaterialButton


        init {
            freelancerIV = itemView.findViewById(R.id.freelancerIV)
            nameTV = itemView.findViewById(R.id.nameTV)
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
            pubDateTV = itemView.findViewById(R.id.pubDateTV)
            detailsBtn = itemView.findViewById(R.id.detailsBtn)
            reviewBtn = itemView.findViewById(R.id.reviewBtn)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_projects_closed_4_client, parent, false)
        return ViewHolder(view)
    }

    var _user: NewUser? = null
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val completedProject = closedProjects[position]
        holder.apply {
            setUserImage(completedProject.image, freelancerIV, NewUser.FREELANCER_USER_TYPE, completedProject.gender)
            nameTV.text = "${completedProject.freelancerName} ${completedProject.freelancerLastName}"
            projectTitleTV.text =completedProject.projectTitle
            pubDateTV.text = dateFormat.format(completedProject.startDate?: Date())
            detailsBtn.setOnClickListener {
                onClickListener.onClicked(completedProject)
            }

            reviewBtn.setOnClickListener {
                onClickListener.onReviewBtnClicked(completedProject)
                it.isEnabled = false
            }
        }
    }


    override fun getItemCount(): Int {
        return closedProjects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newClosedProjects: List<ClosedProject>): CompletedProjectsRV_Adapter4Client {
        newClosedProjects.toMutableList().also { closedProjects = it }
        notifyDataSetChanged()
        return this
    }

    fun getList(): List<ClosedProject>{
        return closedProjects
    }


    interface OnClickListener {
        fun onClicked(project: ClosedProject)
        fun onReviewBtnClicked(closedProject: ClosedProject)
        fun onFreelancerPhotoClicked(freelancerUserId: Int)
    }
}