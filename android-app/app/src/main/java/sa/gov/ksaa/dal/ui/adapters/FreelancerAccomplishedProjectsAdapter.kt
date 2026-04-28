package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.adapters.RV_Adapter.Companion.numberFormat
import java.text.DateFormat
import java.util.Locale

class FreelancerAccomplishedProjectsAdapter(
    private var closedProjects: MutableList<sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject>,
    private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<FreelancerAccomplishedProjectsAdapter.ViewHolder>() {

    val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var projectTitleTV: TextView
        var clientIV: ImageView
        var serviceTypeTV: TextView
        var durationTV: TextView
        var detailsBtn: Button

        var ratingBar: RatingBar
        var reviewNumberTV: TextView

        init {
            clientIV = itemView.findViewById(R.id.clientIV)
            projectTitleTV = itemView.findViewById(R.id.projectTitleTV)
            serviceTypeTV = itemView.findViewById(R.id.serviceTypeTV)
            durationTV = itemView.findViewById(R.id.durationTV)
            detailsBtn = itemView.findViewById(R.id.detailsBtn)

            ratingBar = itemView.findViewById(R.id.ratingBar)
            reviewNumberTV = itemView.findViewById(R.id.reviewNumberTV)

        }
    }

    lateinit var parentContext: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        parentContext = parent.context
        val view =
            LayoutInflater.from(parentContext).inflate(R.layout.list_item_freelancer_projects, parent, false)
        return ViewHolder(view)
    }

    var _user: NewUser? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val completedProject = closedProjects[position]
//        val workingProject = completedProjects[position].working_project
//        val project = workingProject?.project

        holder.apply {
//            if (userType == User.CLIENT_ROLE){
//                clientIV.setImageResource(R.drawable.freelancer_png)
//                clientIV.setOnClickListener {
//
//                    completedProject.freelanceId?.let{
//                        onClickListener.onFreelancerIvClicked(it)
//                    }
//                }
//            } else if (userType == User.FREELANCER_ROLE){
//                clientIV.setImageResource(R.drawable.client_png)
//                clientIV.setOnClickListener {
//                    completedProject.userId?.let{
//                        onClickListener.onClientIvClicked(it)
//                    }
//                }
//            }

            projectTitleTV.text = completedProject.projectTitle
            serviceTypeTV.text = completedProject.listOfCategory?.firstOrNull()?.proejctCategory
            durationTV.text =  convertArabicNumbersToString(completedProject.durationOfProject ?: "20")

            ratingBar.rating = (completedProject.rating ?: 0.0).toFloat()
            reviewNumberTV.text = numberFormat.format(completedProject.reviewCount ?: 0.0f)
            detailsBtn.setOnClickListener {
                onClickListener.onClicked(completedProject)
            }
//            ratingAvgTV
//            ratingBar
//            reviewNumberTV
        }

    }

    fun convertArabicNumbersToString(text: String?): String {
        val arabicToWesternMap = mapOf(
            '0' to '٠' , '1' to '١'   , '2' to  '٢','3'  to '٣', '4' to  '٤',
            '5' to  '٥', '6'  to '٦', '7' to  '٧', '8' to  '٨','9' to  '٩'
        )
        val builder = text?.let { StringBuilder(it.length) }
        if (text != null) {
            for (char in text) {
                if (builder != null) {
                    builder.append(arabicToWesternMap[char] ?: char)
                }
            }
        }
        return builder.toString()
    }
    override fun getItemCount(): Int {
        return closedProjects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject>): FreelancerAccomplishedProjectsAdapter {
        newBids.toMutableList().also { closedProjects = it }
        notifyDataSetChanged()
        return this
    }


    interface OnClickListener {
        fun onClicked(project: sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject)
//        fun onReviewBtnClicked(completedProject: CompletedProject)
        fun onClientIvClicked(user: Int)
        fun onFreelancerIvClicked(freelancer: Int)
    }
}