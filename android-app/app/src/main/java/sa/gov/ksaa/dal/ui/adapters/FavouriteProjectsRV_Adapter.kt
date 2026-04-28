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
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.Int

class FavouriteProjectsRV_Adapter(
    private var favorates: MutableList<NewProject>,
    private val onClickListener: OnClickListener,
    override var context: Context
//    val user: User?
) :
    RecyclerView.Adapter<FavouriteProjectsRV_Adapter.ViewHolder>(),MyRecyclerViewAdapter {



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var project_name_TV: TextView
        var clientNameTV: TextView
        var freelancerLevelTV: TextView
        var durationTV: TextView
        var waitingTV: TextView
        var clientIV : ImageView

        //        lateinit var  project_desc_TV: TextView
        var favorite_project_IB: ImageView
        var budget_TV: TextView
        var sendQuotationBtn: Button
        var domainTypeTV: RecyclerView
        var numberOfBidsTV: TextView

        init {
            project_name_TV = itemView.findViewById(R.id.project_name_TV)
            clientNameTV = itemView.findViewById(R.id.clientNameTV)
            freelancerLevelTV = itemView.findViewById(R.id.freelancerLevelTV)
//            project_desc_TV = itemView.findViewById(R.id.project_desc_TV)
            favorite_project_IB = itemView.findViewById(R.id.favoriteBtn)
            budget_TV = itemView.findViewById(R.id.budget_TV)
            durationTV = itemView.findViewById(R.id.durationTV)
            sendQuotationBtn = itemView.findViewById(R.id.sendQuotationBtn)
            numberOfBidsTV = itemView.findViewById(R.id.numberOfBidsTV)
            waitingTV = itemView.findViewById(R.id.waitingTV)
            domainTypeTV = itemView.findViewById(R.id.specialityRV3)
            clientIV = itemView.findViewById(R.id.clientIV)

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = favorates[position]

        setUserImage(project.image, holder.clientIV, NewUser.CLIENT_USER_TYPE, project.gender)
        holder.project_name_TV.text = project.projectTitle
//        holder.project_desc_TV.text = projects[position].project_description
        holder.clientNameTV.text = project.clientName ?: "شركة كاف الإعلامية"

        holder.freelancerLevelTV.text = project.ratingOfFreelancer ?: "متمرس"
        holder.durationTV.text =    project.createdDate?.let { dateFormat.format(it) }

        holder.waitingTV.visibility = View.GONE

        holder.numberOfBidsTV.text = numberFormat.format(project.numberOfOffers ?:  0)

        val projectcat = DomainsProjectScreen_Adapter(mutableListOf())
        holder.domainTypeTV.adapter = projectcat

        projectcat.setList(project.listOfCategory!!)

        holder.sendQuotationBtn.setOnClickListener {
            onClickListener.onSendQuotClicked(project)
        }

        holder.favorite_project_IB.isSelected = project.favourite == true
        holder.favorite_project_IB.setOnClickListener {
            it.isSelected = !it.isSelected
            onClickListener.onFavoriteClicked(project, position)
        }

        holder.itemView.setOnClickListener {
            onClickListener.onProjectClicked(project)
        }

        val numberAm: Int = try {
            project.amount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
        } catch (e: NumberFormatException) {
            0 // Set to 0 if conversion fails
        }
        val formattedNumberAm = when (numberAm) {
            is Int, -> numberFormat.format(numberAm) // Format if it's a number
            // Use the string as is
            else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
        }
        holder.budget_TV.text = formattedNumberAm
//        holder.quotations_num_TV.text = projects[position].
    }


    override fun getItemCount(): Int {
        return favorates.size
    }

//    fun addList(newProjects: List<Project>?): FavouriteProjectsRV_Adapter {
//        if (newProjects != null) {
//            favorates.addAll(newProjects)
//            notifyDataSetChanged()
//        }
//        return this
//    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newProjects: List<NewProject>): FavouriteProjectsRV_Adapter {
        newProjects.toMutableList().also { favorates = it }
        notifyDataSetChanged()
        return this
    }


    fun delete_aProject(position: Int) {

        if (position in 0 until itemCount){
            favorates.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }

    }


    interface OnClickListener {
        fun onProjectClicked(project: NewProject)
        fun onSendQuotClicked(projectId: NewProject)
        fun onFavoriteClicked(freelancerFavoriteProject: NewProject, position: Int)
    }

}