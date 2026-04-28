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
import sa.gov.ksaa.dal.data.webservices.newDal.responses.BiddingInvitation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.Int

class ProjectInvitationsRV_Adapter(
    private var freelancersInvitedProjects: MutableList<BiddingInvitation>,
    private val onClickListener: OnClickListener, override var context: Context
) :
    RecyclerView.Adapter<ProjectInvitationsRV_Adapter.ViewHolder>(), MyRecyclerViewAdapter{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIV: ImageView
        var project_name_TV: TextView
        var clientNameTV: TextView
        var freelancerLevelTV: TextView
        var durationTV: TextView

        var favorite_project_IB: ImageView
        var budget_TV: TextView
        var serviceTypeTV: TextView
        var displayDetailsBtn: Button

        var incommingSimpleDateFormatAr = SimpleDateFormat("d MMM yyyy", Locale("ar", "SA"))
        var incommingSimpleDateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.ENGLISH)
        var dateFormatAr = DateFormat.getDateInstance(DateFormat.DEFAULT,  Locale("ar", "SA"))

        init {
            userIV = itemView.findViewById(R.id.userIV)
            project_name_TV = itemView.findViewById(R.id.project_name_TV)
            clientNameTV = itemView.findViewById(R.id.clientNameTV)
            freelancerLevelTV = itemView.findViewById(R.id.freelancerLevelTV)
            durationTV = itemView.findViewById(R.id.durationTV)
            favorite_project_IB = itemView.findViewById(R.id.favoriteBtn)
            budget_TV = itemView.findViewById(R.id.budget_TV)
            serviceTypeTV = itemView.findViewById(R.id.serviceTypeTV)
            displayDetailsBtn = itemView.findViewById(R.id.sendQuotationBtn)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_project_invitation, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val biddingInvitation = freelancersInvitedProjects[position]
        holder.apply {
            setUserImage(biddingInvitation.image, userIV, NewUser.CLIENT_USER_TYPE, biddingInvitation.gender)
            project_name_TV.text = biddingInvitation.projectTitle
            clientNameTV.text = "${biddingInvitation.clientName} ${biddingInvitation.clientLastName}"
            freelancerLevelTV.text = biddingInvitation.freelancerLevel
//            biddingInvitation.durationOfProject?.let {
////            var date: Date
////            try {
////                date = Date(it.toLong())
////            } catch (e: NumberFormatException) {
////                date = SimpleDateFormat("dd-MM-yyyy")
////                    .parse(it) as Date
////            }
//                durationTV.text = incommingSimpleDateFormatAr.format(it) ?: incommingSimpleDateFormatAr.format(java.util.Date())
////            pubDateTV.text = convertDateFormat(it.toString())
//            }


            durationTV.text = convertArabicNumbersToString(biddingInvitation.durationOfProject)

            incommingSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            try {
                durationTV.text = dateFormatAr.format(incommingSimpleDateFormat.parse(biddingInvitation.durationOfProject))
            } catch (e: ParseException) {
                durationTV.text = biddingInvitation.durationOfProject
            }



            val cost: Int = try {
                biddingInvitation.amount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
            } catch (e: NumberFormatException) {
                0 // Set to 0 if conversion fails
            }
            val formattedCost = when (cost) {
                is Int, -> numberFormat.format(cost) // Format if it's a number
                // Use the string as is
                else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
            }
            budget_TV.text = formattedCost

            biddingInvitation.listOfCategory?.let {
            if (it.isNotEmpty()) {
                serviceTypeTV.text = biddingInvitation.listOfCategory!![0].proejctCategory
            }

        }

            displayDetailsBtn.setOnClickListener {
                onClickListener.onDetailsClicked(biddingInvitation)
            }

            favorite_project_IB.isSelected = biddingInvitation.favourite == true
            favorite_project_IB.setOnClickListener {
                it.isSelected = !it.isSelected
                onClickListener.onFavoriteClicked(biddingInvitation, position)
            }

            itemView.setOnClickListener {
                onClickListener.onProjectClicked(freelancersInvitedProjects[position])
            }
        }


//        holder.quotations_num_TV.text = projects[position].
    }


    override fun getItemCount(): Int {
        return freelancersInvitedProjects.size
    }

    fun setList(newProjects: List<BiddingInvitation>?): ProjectInvitationsRV_Adapter {
        if (newProjects != null) {
            newProjects.toMutableList().also { freelancersInvitedProjects = it }
            notifyDataSetChanged()
        }
        return this
    }

    fun favouriteAproject(projectId: Int?, position: Int) {
        if (projectId != null){
            freelancersInvitedProjects.get(position).favourite = true
            notifyItemChanged(position)
        }

    }

    fun unFavourite_aProject(projectId: Int?, position: Int) {
        if (projectId != null){
            freelancersInvitedProjects.get(position).favourite = false
            notifyItemChanged(position)
        }

    }

    fun delete_aProject(position: Int) {
        if (position in 0 until itemCount){
            freelancersInvitedProjects.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
    }


    interface OnClickListener {
        fun onProjectClicked(biddingInvitation: BiddingInvitation)
        fun onDetailsClicked(project: BiddingInvitation)
        fun onFavoriteClicked(project: BiddingInvitation, position: Int)
    }

}