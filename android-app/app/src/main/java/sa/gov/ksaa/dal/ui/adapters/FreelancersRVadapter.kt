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
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.models.FreelancerFilter
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import kotlin.Int

class FreelancersRVadapter(
    private var freelancers: MutableList<NewFreelancer>,
    private val onClickListener: OnClickListener,
    val user: NewUser?, override var context: Context
) :
    RecyclerView.Adapter<FreelancersRVadapter.ViewHolder>(), MyRecyclerViewAdapter{
//    var selectedFreelancers = mutableSetOf<NewFreelancer>()

    lateinit var originalList: MutableList<NewFreelancer>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        var isSelected = false
        var level_TV: TextView
        var favorite_a_freelancer_IB: ImageView
        var freelance_image: ImageView
        var user_name_tv: TextView
        var serviceTypeTV: TextView
        var freelancer_rating: RatingBar
        var user_profile_btn: Button
        var containerCL: ConstraintLayout
//        var ratingValueTV: TextView
        var noReviewsTV: TextView


        init {
            level_TV = itemView.findViewById(R.id.level_TV)
            favorite_a_freelancer_IB = itemView.findViewById(R.id.favorite_a_freelancer_IB)
            freelance_image = itemView.findViewById(R.id.freelance_image)
            user_name_tv = itemView.findViewById(R.id.user_name_tv)
            serviceTypeTV = itemView.findViewById(R.id.serviceTypeTV)
//            ratingValueTV = itemView.findViewById(R.id.ratingValueTV)
            noReviewsTV = itemView.findViewById(R.id.noReviewsTV)
            freelancer_rating = itemView.findViewById(R.id.freelancer_rating)
            user_profile_btn = itemView.findViewById(R.id.user_profile_btn)
            containerCL = itemView.findViewById(R.id.containerCL)
        }

//        fun toggleSelection(){
//            isSelected = !isSelected
//        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_freelancer, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val freelancer0 = freelancers[position]


        holder.apply {

            if (user != null) {
                itemView.setOnClickListener {
                    onClickListener.onProfileBtnClicked(freelancer0)
//            freelancer.isSelected = !freelancers[position].isSelected
//            if (freelancers[position].isSelected) {
//                selectedFreelancers.add(freelancers[position])
//                onClickListener.onFreelancerSelected(freelancer)
//            }
//            else {
//                selectedFreelancers.remove(freelancer)
//                onClickListener.onFreelancerUnselected(freelancer)
//            }
//            notifyItemChanged(position)
                }
            }

            level_TV.text = freelancer0.freelancerLevel ?: "متمرس"


            setUserImage(freelancer0.image, freelance_image, NewUser.FREELANCER_USER_TYPE, freelancer0.gender)


//            .into(R.drawable.freelancer_user);
            user_name_tv.text = freelancer0.getFullName()
//            serviceTypeTV.text = freelancer.listOfServices[0].typeOfServices ?: "ترجمة"
//        holder.serviceTypeTV.text = freelancers[position].type_of_service

            freelancer0.listOfServices?.let {
                if (it.isNotEmpty())
                    serviceTypeTV.text = it[0].typeOfServices
            }

            val rating = freelancer0.rating ?: 0.000001f
//            ratingValueTV.text = numberFormat.format(rating)
            freelancer_rating.rating = rating
            noReviewsTV.text = numberFormat.format(freelancer0.reviewCount ?: 0)


            if (user != null){
                user_profile_btn.setOnClickListener {
                    onClickListener.onProfileBtnClicked(freelancer0)
                }

            }else{
                user_profile_btn.visibility = View.INVISIBLE
            }



            if (user != null && user.isClient()) {
                favorite_a_freelancer_IB.isSelected = freelancer0.favourite == true
                favorite_a_freelancer_IB.setOnClickListener {
                    it.isSelected = !it.isSelected
                    onClickListener.onFavoriteBtnClicked(freelancers[position], position)
                }
            } else {
                favorite_a_freelancer_IB.visibility = View.INVISIBLE
            }
        }
    }


    override fun getItemCount(): Int {
        return freelancers.size
    }

    fun setFavorite(position: Int, value: Boolean) {
        freelancers[position].favourite = value
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newFreelancers: List<NewFreelancer>): FreelancersRVadapter {
        freelancers = newFreelancers.toMutableList()
        originalList = freelancers
//        freelancers.forEach { freelancer ->
//            if (selectedFreelancers.contains(freelancer))
//                freelancer.isSelected = true
//        }
        notifyDataSetChanged()
        return this
    }

    var searchFilter: FreelancerFilter? = null

    fun setFilter(searchFilter: FreelancerFilter?) {
        this.searchFilter = searchFilter

        filter()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reset() {
        freelancers = originalList?:freelancers
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun search(query: String?) {
        originalList?.let {
            freelancers = it.filter { freelancer ->
                var result = if (query.isNullOrEmpty()) true else {
                    freelancer.freelancerName?.contains(query) == true ||
                            freelancer.freelancerLastName?.contains(query) == true ||
                            freelancer.lastName?.contains(query) == true ||
                            freelancer.firstName?.contains(query) == true ||
                            freelancer.getFullName().contains(query)
                }
                Log.w(javaClass.simpleName, "search: searchResult = $result")
                searchFilter?.let { filter ->
                    val typeOfServices = freelancer.typeOfServices?.trim()
                    if (!filter.serviceTypes.isNullOrEmpty() && !typeOfServices.isNullOrEmpty()){
                        result = result && filter.serviceTypes!!.contains(typeOfServices)
                    }
                    val freelancerLevel = freelancer.freelancerLevel?.trim()
                    if (!filter.freelancerLevels.isNullOrEmpty() && !freelancerLevel.isNullOrEmpty()){
                        result = result && filter.freelancerLevels!!.contains(freelancerLevel)
                    }

                    if (filter.freelancerRating != null && filter.freelancerRating!! > 0)
                        result = result && freelancer.rating == filter.freelancerRating

                    Log.w(javaClass.simpleName, "search: filterResult = $result")
                }

                result
            }.toMutableList()

            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter() {
        originalList?.let {
            freelancers = it.filter { freelancer ->
                var result = true
                searchFilter?.let { filter ->


                    val typeOfServices = freelancer.listOfServices?.firstOrNull()?.typeOfServices?.trim()
                    val freelancerLevel = freelancer.freelancerLevel

                    if (!filter.services.isNullOrEmpty() && !typeOfServices.isNullOrEmpty() && filter.freelancerLevels.isNullOrEmpty()  ){
                        result = result && filter.services!!.contains(typeOfServices)
//                        Log.w(javaClass.simpleName, "search: filterResult = yeeesssss${filter.serviceTypes}")
                    }




                    if (!filter.freelancerLevels.isNullOrEmpty() && !freelancerLevel.isNullOrEmpty() && !filter.services.isNullOrEmpty() && !typeOfServices.isNullOrEmpty()){
                        result = result && filter.freelancerLevels!!.contains(freelancerLevel) &&   filter.services!!.contains(typeOfServices)
                    }

                    if (!filter.freelancerLevels.isNullOrEmpty() && !freelancerLevel.isNullOrEmpty() && filter.services.isNullOrEmpty()){
                        result = result && filter.freelancerLevels!!.contains(freelancerLevel)
                    }


                    if (filter.freelancerRating != null && filter.freelancerRating!! > 0 && filter.services.isNullOrEmpty() && typeOfServices.isNullOrEmpty())
                        result = result && freelancer.rating == filter.freelancerRating

//                    Log.w(javaClass.simpleName, "search: filterResult = ${freelancer.listOfServices?.first()?.freelancerId}")
                }

                result
            }.toMutableList()

            notifyDataSetChanged()
        }
    }


    interface OnClickListener {
        fun onProfileBtnClicked(freelancer: NewFreelancer)
        fun onFavoriteBtnClicked(freelancer: NewFreelancer, position: Int)
        fun onFreelancerSelected(freelancer: NewFreelancer)
        fun onFreelancerUnselected(freelancer: NewFreelancer)
    }
}