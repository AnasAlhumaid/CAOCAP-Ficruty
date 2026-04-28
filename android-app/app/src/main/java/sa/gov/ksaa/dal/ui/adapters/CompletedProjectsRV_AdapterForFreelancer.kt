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

class CompletedProjectsRV_AdapterForFreelancer(
    private var closedProjects: MutableList<ClosedProject>,
    private val onClickListener: OnClickListener, override var context: Context
) :
    RecyclerView.Adapter<CompletedProjectsRV_AdapterForFreelancer.ViewHolder>(), MyRecyclerViewAdapter{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userIV: ImageView
        var clientNameTV: TextView
        var projectTitleTV: TextView
        var paymentStatusTV: TextView
        var pubDateTV: TextView

        var detailsBtn: MaterialButton
        var reviewBtn: MaterialButton

        init {
            userIV = itemView.findViewById(R.id.userIV)
            clientNameTV = itemView.findViewById(R.id.clientNameTV)
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
            paymentStatusTV = itemView.findViewById(R.id.paymentStatusTV)
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
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_projects_closed_4_freelancer, parent, false)
        return ViewHolder(view)
    }

    var _user: NewUser? = null
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val project = completedProjects[position].working_project?.project
        val completedProject = closedProjects[position]
        holder.apply {

            setUserImage(completedProject.image, userIV, NewUser.CLIENT_USER_TYPE, completedProject.gender)

            clientNameTV.text = "${completedProject.clientName} ${completedProject.clientLastName}"
            projectTitleTV.text =completedProject.projectTitle ?: "مشروع"
            pubDateTV.text = dateFormat.format(completedProject.startDate?: Date())

            detailsBtn.setOnClickListener {
                onClickListener.onClicked(completedProject)
            }

            if (completedProject.isReviewed){
                reviewBtn.visibility = View.INVISIBLE
            } else {
                reviewBtn.setOnClickListener {
                    onClickListener.onReviewBtnClicked(completedProject, position)
                }
            }

        }
    }


    override fun getItemCount(): Int {
        return closedProjects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<ClosedProject>): CompletedProjectsRV_AdapterForFreelancer {
        newBids.toMutableList().also { closedProjects = it }
        notifyDataSetChanged()
        return this
    }

    fun getList(): List<ClosedProject>{
        return closedProjects
    }


    interface OnClickListener {
        fun onClicked(project: ClosedProject)
        fun onReviewBtnClicked(closedProject: ClosedProject, position: Int)
    }
}