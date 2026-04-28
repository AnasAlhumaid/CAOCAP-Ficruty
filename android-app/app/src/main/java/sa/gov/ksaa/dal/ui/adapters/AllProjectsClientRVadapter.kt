package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AllProjectsClientRVadapter(
    private var workingProjects: MutableList<NewProject>,
    private val onClickListener: OnClickListener,
    private val user: NewUser, override var context: Context,
) :
    RecyclerView.Adapter<AllProjectsClientRVadapter.ViewHolder>(), MyRecyclerViewAdapter{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        var aboutTV: TextView
        var projectStatusTV: TextView
        var projectTitleTV: TextView
        var freelancerNameTV: TextView
        var userIV: ImageView
        var costTV: TextView
        var pubDateLblTV: TextView
        var projectStartDateTV: TextView
        var remainingDaysTV: TextView
        var detailsBtn: Button
        var detailsLL: LinearLayout
        var divider: View
        var dateRemainingFix: TextView
        var dateRemainingFix2: TextView

        init {
            projectStatusTV = itemView.findViewById(R.id.projectStatusTV)
            freelancerNameTV = itemView.findViewById(R.id.freelancerNameTV)
            userIV = itemView.findViewById(R.id.userIV)
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
//            aboutTV = itemView.findViewById(R.id.aboutTV)
            costTV = itemView.findViewById(R.id.costTV)
            pubDateLblTV = itemView.findViewById(R.id.pubDateLblTV)
            projectStartDateTV = itemView.findViewById(R.id.projectStartDateTV)
            remainingDaysTV = itemView.findViewById(R.id.remainingDaysTV)
            detailsBtn = itemView.findViewById(R.id.detailsBtn)
            detailsLL = itemView.findViewById(R.id.detailsLL)
            divider = itemView.findViewById(R.id.divider)
            dateRemainingFix = itemView.findViewById(R.id.textView104)
            dateRemainingFix2 = itemView.findViewById(R.id.textView106)


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

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = workingProjects[position]
        holder.apply {

            if (project.status == "waiting for admin accept"){
                projectStatusTV.text = "جارِ مراجعة الخدمة من قبل مدير النظام"
            }else {
                if (project.projectStatus == "noBid") {
                    projectStatusTV.text = "جارِ استقبال العروض"
                } else if (project.projectStatus == "complete") {
                    projectStatusTV.text = "مكتمل"
                }else{
                    projectStatusTV.text = "قيد التنفيذ"
                }
            }

            freelancerNameTV.text = user.getFullName()

            setUserImage(project.image, userIV, NewUser.CLIENT_USER_TYPE, project.gender)

            projectTitleTV.text = project.projectTitle

            remainingDaysTV.visibility = View.GONE
            dateRemainingFix.visibility = View.GONE
            dateRemainingFix2.visibility = View.GONE





//        holder.aboutTV.text = workingProjects[position].project?.about ?: "نبذة عن المشروع"
            costTV.text = numberFormat.format((project.amount ?: "100").toFloat())
//            pubDateLblTV.visibility = when(projectUnderway.)
//            projectUnderway.startDate?.let {
//                holder.projectStartDateTV.text = dateFormat.format(Date(it))
//            }

projectStartDateTV.text = convertDateFormat(project.durationOfProject.toString())

            detailsBtn.setOnClickListener {
                onClickListener.onClicked(workingProjects[position])
            }
        }
    }


    override fun getItemCount(): Int {
        return workingProjects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<NewProject>): AllProjectsClientRVadapter {
        newBids.toMutableList().also { workingProjects = it }
        notifyDataSetChanged()
        return this
    }

    fun getList(): List<NewProject> {
        return workingProjects
    }


    interface OnClickListener {
        fun onClicked(project: NewProject)
    }

    fun convertDateFormat(inputDate: String): String? {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormatter = SimpleDateFormat("d MMM yyyy", Locale("ar"))

        try {
            val date = inputFormatter.parse(inputDate)
            return outputFormatter.format(date)
        } catch (e: Exception) {
            // Handle parsing errors if needed
            return null
        }
    }
}
