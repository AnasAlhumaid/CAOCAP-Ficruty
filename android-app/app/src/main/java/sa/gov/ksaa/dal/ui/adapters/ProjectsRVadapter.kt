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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.models.ProjectsFilter
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Skill
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.Int

class ProjectsRVadapter(
    private var newProjects: MutableList<NewProject>,
    private val onClickListener: OnClickListener,
    val user: NewUser?,
    val currentFreelancer: NewFreelancer?,
    override var context: Context

) :
    RecyclerView.Adapter<ProjectsRVadapter.ViewHolder>(), MyRecyclerViewAdapter{

    val allowedLevel = mapOf(
        "نشط" to listOf("نشط", "متمرس", "متميز" , "محترف"),
        "متمرس" to listOf("متمرس", "متميز" , "محترف"),
        "متميز" to listOf("متميز" , "محترف"),
        "محترف" to listOf("محترف")
    )



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item_project, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var clientIV: ImageView
        var project_name_TV: TextView
        var clientNameTV: TextView
        var freelancerLevelTV: TextView
        var durationTV: TextView
        var domainTypeTV: RecyclerView
        var favorite_project_IB: ImageView
        var budget_TV: TextView
        var sendQuotationBtn: Button
        var numberOfBidsTV: TextView
        var messageTV: TextView


        init {
            clientIV = itemView.findViewById(R.id.clientIV)
            project_name_TV = itemView.findViewById(R.id.project_name_TV)
            clientNameTV = itemView.findViewById(R.id.clientNameTV)
            freelancerLevelTV = itemView.findViewById(R.id.freelancerLevelTV)
            durationTV = itemView.findViewById(R.id.durationTV)
            domainTypeTV = itemView.findViewById(R.id.specialityRV3)
            favorite_project_IB = itemView.findViewById(R.id.favoriteBtn)
            budget_TV = itemView.findViewById(R.id.budget_TV)
            sendQuotationBtn = itemView.findViewById(R.id.sendQuotationBtn)
            numberOfBidsTV = itemView.findViewById(R.id.numberOfBidsTV)
            messageTV = itemView.findViewById(R.id.waitingTV)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val project = newProjects[position]
        holder.apply {
//            clientIV

            setUserImage(project.image, clientIV, NewUser.CLIENT_USER_TYPE, project.gender)
            project_name_TV.text =  convertArabicNumbersToString(project.projectTitle)
            clientNameTV.text = "${project.clientName} ${project.clientLastName?:""}"
            freelancerLevelTV.text = project.freelancerLevel
            durationTV.text =
                project.createdDate?.let { dateFormat.format(it) }// numberFormat.format(project.durationOfProject?.toInt() ?: 7) //  project.deliveryTime?:""
//            project.proejctCategory?.let {
//                domainTypeTV.text = project.proejctCategory
//            }
//
//            project.listOfCategory?.let {
//                if (it.isNotEmpty())
//                    domainTypeTV.text = it[0].proejctCategory
//            }



            val projectcat = DomainsProjectScreen_Adapter(mutableListOf())
                  holder.domainTypeTV.adapter = projectcat

                    projectcat.setList(project.listOfCategory!!)





            favorite_project_IB.isSelected = project.favourite == true
            if (user != null) {
                if (user.isClient()) favorite_project_IB.visibility = View.GONE
                else {
                    favorite_project_IB.setOnClickListener {
                        it.isSelected = !it.isSelected
                        onClickListener.onFavoriteClicked(project, position)
                    }

                }
            }else{
                favorite_project_IB.visibility = View.INVISIBLE
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

            budget_TV.text = formattedNumberAm

//            if (user?.isFreelancer() == true) {
//                sendQuotationBtn.setOnClickListener {
//                    if (
//                        allowedLevel[project.freelancerLevel?.trim()]
//                        ?.contains(currentFreelancer!!.freelancerLevel?.trim())
//                        == true){
//                        Log.w(javaClass.simpleName, "onBindViewHolder: freelancer is allowed to submit bit")
//                        onClickListener.onSendQuotClicked(project)
//                    } else {
//                        Log.w(javaClass.simpleName, "onBindViewHolder: freelancer is not allowed to submit bit")
//                        onClickListener.showInsufficientLevel()
//                    }
//
//                }
//            } else {
//                sendQuotationBtn.visibility = View.GONE
//            }

            if (user != null){
                sendQuotationBtn.setOnClickListener {
                    onClickListener.onProjectClicked(project)
                }
            }else{
                sendQuotationBtn.visibility = View.INVISIBLE
            }




            val numberBid = project.numberOfBidding ?: 0




            numberOfBidsTV.text = numberFormat.format(numberBid)





            messageTV.text = if (project.projectStatus == "noBid" && (project.numberOfOffer != "")&& project.numberOfBidding!! <= (project.numberOfOffer?.toInt()
                    ?: 0)
            ) "استقبال العروض جارٍ" else "انتهى استقبال العروض"

        }

    }


    override fun getItemCount(): Int {
        return newProjects.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newNewProjects: List<NewProject>?): ProjectsRVadapter {
        if (newNewProjects != null) {
            newProjects.addAll(newNewProjects)
            notifyDataSetChanged()
        }
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newNewProjects: List<NewProject>?): ProjectsRVadapter {
        if (newNewProjects != null) {
            newNewProjects.toMutableList().also {
                newProjects = it
            }
            originalList = newProjects
            notifyDataSetChanged()
        }
        return this
    }

    fun favouriteAproject(projectId: Int?, position: Int) {
        if (projectId != null) {
            val pro = newProjects.get(position)
//            pro.isfavorite = true
            notifyItemChanged(position)
        }

    }

    fun unFavourite_aProject(projectId: Int?, position: Int) {
        if (projectId != null) {
            val pro = newProjects.get(position)
//            pro.isfavorite = false
            notifyItemChanged(position)
        }

    }

    fun delete_aProject(position: Int) {
        if (position in 0 until itemCount){
            newProjects.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
    }

    lateinit var originalList: MutableList<NewProject>

    @SuppressLint("NotifyDataSetChanged")
    fun reset() {
        newProjects = originalList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun search(query: NewProject?) {


//        originalList?.let {
//            newProjects = it.filter { project ->
//                var result = true
////                if (!query.isNullOrEmpty())
////                    result = project.projectTitle?.contains(query) == true ||
////                            project.aboutProject?.contains(query) == true ||
////                            project.clientName?.contains(query) == true ||
////                            project.freelancerLevel?.contains(query) == true ||
////                            project.proejctCategory?.contains(query) == true
//
//                val proejctCategory = project.proejctCategory?.trim()
//
////                projectsFilter?.let {filter ->
////
////                    val proejctCategory = project.proejctCategory?.trim()
////                    if (!filter.serviceTypes.isNullOrEmpty() && !proejctCategory.isNullOrEmpty()){
////                        result = result && filter.serviceTypes!!.contains(proejctCategory)
////                    }
////                    val freelancerLevel = project.freelancerLevel?.trim()
////                    if (!filter.freelancerLevels.isNullOrEmpty() && !freelancerLevel.isNullOrEmpty()){
////                        result = result && filter.freelancerLevels!!.contains(freelancerLevel)
////                    }
////
////                    if (filter.freelancerRating != null && filter.freelancerRating!! > 0)
////                        result = result && filter.freelancerRating?.equals(project.rating) == true
////
////                    Log.w(javaClass.simpleName, "search: filterResult = $result")
////                }
//                project == query!!
//
//                result
//            }.toMutableList()
//
//            notifyDataSetChanged()
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter() {
        originalList?.let {
            newProjects = it.filter { project ->
                var result = true

                projectsFilter?.let {filter ->
                    val freelancerLevel = project.freelancerLevel
                    val proejctCategory = project.listOfCategory?.first()?.proejctCategory?.trim()
                    val projectDuration = project.durationOfProject
                    if (!filter.services.isNullOrEmpty() && !proejctCategory.isNullOrEmpty()


                        ){
                        result = result && filter.services!!.contains(proejctCategory)
                    }


                    if (!filter.freelancerLevels.isNullOrEmpty() && !freelancerLevel.isNullOrEmpty() ){
                        result = result && filter.freelancerLevels!!.contains(freelancerLevel)
                    }

                    if (!filter.projectDuration.isNullOrEmpty() && !projectDuration.isNullOrEmpty() ){
                        result = result && filter.projectDuration!!.contains(projectDuration)
                    }


                    if (!filter.services.isNullOrEmpty() && !proejctCategory.isNullOrEmpty()
                        && !filter.freelancerLevels.isNullOrEmpty() && !freelancerLevel.isNullOrEmpty()
                        ){
                        result = result && filter.services!!.contains(proejctCategory ) && filter.freelancerLevels!!.contains(freelancerLevel)
                    }
                    if (!filter.services.isNullOrEmpty() && !proejctCategory.isNullOrEmpty()
                        && !filter.projectDuration.isNullOrEmpty() && !projectDuration.isNullOrEmpty()
                    ){
                        result = result && filter.services!!.contains(proejctCategory ) && filter.projectDuration!!.contains(projectDuration)
                    }
                    if (!filter.services.isNullOrEmpty() && !proejctCategory.isNullOrEmpty()
                        && !filter.projectDuration.isNullOrEmpty() && !projectDuration.isNullOrEmpty() &&
                        !filter.freelancerLevels.isNullOrEmpty() && !freelancerLevel.isNullOrEmpty()
                    ){
                        result = result && filter.services!!.contains(proejctCategory ) && filter.projectDuration!!.contains(projectDuration)
                                && filter.freelancerLevels!!.contains(freelancerLevel)
                    }

                    if (filter.freelancerRating != null && filter.freelancerRating!! > 0 && filter.services.isNullOrEmpty())
                        result = result && filter.freelancerRating?.equals(project.rating) == true

                    Log.w(javaClass.simpleName, "search: filterResult = $result")
                }

                result
            }.toMutableList()

            notifyDataSetChanged()
        }
    }

    fun updateFavState(position: Int) {
//        notifyItemChanged(position)
    }

    var projectsFilter: ProjectsFilter? = null

    fun setFilter(searchFilter: ProjectsFilter?) {
        this.projectsFilter = searchFilter
        filter()
    }



    interface OnClickListener {
        fun onProjectClicked(newProject: NewProject)
        fun onSendQuotClicked(project: NewProject)
        fun onFavoriteClicked(newProject: NewProject, position: Int)
        fun showInsufficientLevel()
    }

}