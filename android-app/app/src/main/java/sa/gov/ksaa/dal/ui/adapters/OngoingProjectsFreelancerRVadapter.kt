package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import java.util.Date
import kotlin.Int

class OngoingProjectsFreelancerRVadapter(
    private var workingProjects: MutableList<ProjectUnderway>,
    private val onClickListener: OnClickListener, override var context: Context
) :
    RecyclerView.Adapter<OngoingProjectsFreelancerRVadapter.ViewHolder>(), MyRecyclerViewAdapter{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIV: ImageView
        var clientNameTV: TextView
        var projectTitleTV: TextView
//        var pubDateTV: TextView
//        var aboutTV: TextView
        var costTV: TextView
        var projectStartDateTV: TextView
        var remainingDaysTV: TextView
        var detailsBtn: Button

        init {
            userIV = itemView.findViewById(R.id.userIV)
            clientNameTV = itemView.findViewById(R.id.clientNameTV)
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
//            aboutTV = itemView.findViewById(R.id.aboutTV)
//            pubDateTV = itemView.findViewById(R.id.pubDateTV)
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
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_projects_ongoing_freelancer, parent, false)
        return ViewHolder(view)
    }

    lateinit var _user: NewUser
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val projectUnderway = workingProjects[position]
        holder.apply {
            setUserImage(projectUnderway.image, userIV, NewUser.CLIENT_USER_TYPE, NewUser.MALE_USER_GENDER)
            clientNameTV.text = "${projectUnderway.clientName} ${projectUnderway.clientLastName}"
            projectTitleTV.text = projectUnderway.projectTitle ?: ""
//        holder.aboutTV.text = workingProjects[position].project?.project_name

            val cost: Int = try {
                projectUnderway.freelancerBidAmount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
            } catch (e: NumberFormatException) {
                0 // Set to 0 if conversion fails
            }
            val formattedCost = when (cost) {
                is Int, -> numberFormat.format(cost) // Format if it's a number
                // Use the string as is
                else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
            }
            costTV.text = formattedCost ?: ""
//        workingProjects[position].start_date?.let { holder.pubDateTV.text = dateFormat.format(it)

            val remainingDays: Int = try {
                projectUnderway.durationOfProject?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
            } catch (e: NumberFormatException) {
                0 // Set to 0 if conversion fails
            }
            val formattedremainingDaysTV = when (remainingDays) {
                is Int, -> numberFormat.format(remainingDays) // Format if it's a number
                // Use the string as is
                else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
            }
            remainingDaysTV.text = formattedremainingDaysTV ?: ""
            projectStartDateTV.text = dateFormat.format(projectUnderway.startDate ?: Date())
            detailsBtn.setOnClickListener {
                onClickListener.onClicked(workingProjects[position])
            }
        }

    }


    override fun getItemCount(): Int {
        return workingProjects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<ProjectUnderway>): OngoingProjectsFreelancerRVadapter {
        newBids.toMutableList().also { workingProjects = it }
        notifyDataSetChanged()
        return this
    }

    fun getList(): List<ProjectUnderway>{
        return workingProjects
    }


    interface OnClickListener {
        fun onClicked(project: ProjectUnderway)
    }
}