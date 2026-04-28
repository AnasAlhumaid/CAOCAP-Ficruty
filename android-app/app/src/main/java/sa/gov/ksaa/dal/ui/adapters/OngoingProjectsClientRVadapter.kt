package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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

class OngoingProjectsClientRVadapter(
    private var workingProjects: MutableList<ProjectUnderway>,
    private val onClickListener: OnClickListener, override var context: Context
) : RecyclerView.Adapter<OngoingProjectsClientRVadapter.ViewHolder>(), MyRecyclerViewAdapter{



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var aboutTV: TextView
        var userIV: ImageView
        var projectTitleTV: TextView
        var freelancerNameTV: TextView
        var costTV: TextView
        var pubDateLblTV: TextView
        var projectStartDateTV: TextView
        var remainingDaysTV: TextView
        var detailsBtn: Button

        init {

            userIV = itemView.findViewById(R.id.userIV)
            freelancerNameTV = itemView.findViewById(R.id.freelancerNameTV)
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
//            aboutTV = itemView.findViewById(R.id.aboutTV)
            costTV = itemView.findViewById(R.id.costTV)
            pubDateLblTV = itemView.findViewById(R.id.pubDateLblTV)
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
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_projects_ongoing_client, parent, false)
        return ViewHolder(view)
    }

    lateinit var _user: NewUser
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val projectUnderway = workingProjects[position]
        holder.apply {

            setUserImage(projectUnderway.image, userIV,NewUser.FREELANCER_USER_TYPE, projectUnderway.gender!!)

            userIV.setOnClickListener {
                Log.i(javaClass.simpleName, "updateUI: deliverablesVM.getAll(${projectUnderway.listOfServices!![0].freelancerId!!})")
                projectUnderway.freelancerUserId!!.let {  onClickListener.onPhotoClicked(it)}
            }

            freelancerNameTV.text = "${projectUnderway.freelancerName} ${projectUnderway.freelancerLastName}"
            projectTitleTV.text = projectUnderway.projectTitle ?: "مشروع"
//        holder.aboutTV.text = workingProjects[position].project?.about ?: "نبذة عن المشروع"

            val numberAm: Int = try {
                projectUnderway.freelancerBidAmount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
            } catch (e: NumberFormatException) {
                0 // Set to 0 if conversion fails
            }
            val formattedNumberAm = when (numberAm) {
                is Int, -> numberFormat.format(numberAm) // Format if it's a number
                // Use the string as is
                else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
            }
            costTV.text = formattedNumberAm
//            pubDateLblTV.visibility = when(projectUnderway.)
            projectStartDateTV.text = dateFormat.format(projectUnderway.startDate?:Date())

            remainingDaysTV.text = convertArabicNumbersToString(projectUnderway.durationOfProject)
            detailsBtn.setOnClickListener {
                onClickListener.onClicked(workingProjects[position])
            }
        }
    }


    override fun getItemCount(): Int {
        return workingProjects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<ProjectUnderway>): OngoingProjectsClientRVadapter {
        newBids.toMutableList().also { workingProjects = it }
        notifyDataSetChanged()
        return this
    }

    fun getList(): List<ProjectUnderway>{
        return workingProjects
    }


    interface OnClickListener {
        fun onClicked(project: ProjectUnderway)
        fun onPhotoClicked(freelancerUserId: Int)
    }
}