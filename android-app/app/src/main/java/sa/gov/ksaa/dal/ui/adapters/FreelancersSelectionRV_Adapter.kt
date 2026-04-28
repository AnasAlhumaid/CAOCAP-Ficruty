package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import kotlin.Int

class FreelancersSelectionRV_Adapter(private var freelancers: MutableList<NewFreelancer>,
                                     private val onClickListener: OnClickListener,
                                     val user: NewUser?,
                                     override var context: Context
) :
    RecyclerView.Adapter<FreelancersSelectionRV_Adapter.ViewHolder>(), MyRecyclerViewAdapter{
    var selectedFreelancers = mutableSetOf<NewFreelancer>()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        var isSelected = false
        var  level_TV: TextView
//        var  favorite_a_freelancer_IB: ImageView
//        var  freelance_image: ImageView
        var  user_name_tv: TextView
        var  serviceTypeTV: TextView
        var  freelancer_rating: RatingBar
//        var  user_profile_btn: Button
        var  containerCL: LinearLayout
        init {
            level_TV = itemView.findViewById(R.id.level_TV)
//            favorite_a_freelancer_IB = itemView.findViewById(R.id.favorite_a_freelancer_IB)
//            freelance_image = itemView.findViewById(R.id.freelance_image)
            user_name_tv = itemView.findViewById(R.id.user_name_tv)
            serviceTypeTV = itemView.findViewById(R.id.serviceTypeTV)
            freelancer_rating = itemView.findViewById(R.id.freelancer_rating)
//            user_profile_btn = itemView.findViewById(R.id.user_profile_btn)
            containerCL = itemView.findViewById(R.id.freelanceCard)
        }

//        fun toggleSelection(){
//            isSelected = !isSelected
//        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_freelancer_to_be_invited, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val freelancer = freelancers[position]
        holder.apply {
            itemView.setOnClickListener {
                freelancer.isSelected = !freelancers[position].isSelected
                if (freelancers[position].isSelected) {
                    selectedFreelancers.add(freelancers[position])
                    onClickListener.onFreelancerSelected(freelancer)
                }
                else {
                    selectedFreelancers.remove(freelancer)
                    onClickListener.onFreelancerUnselected(freelancer)
                }
                notifyItemChanged(position)
            }
            if (freelancer.isSelected) {
                holder.containerCL.setBackgroundResource(R.drawable.bg_20_curved_gray_borders_ligt_gray_filled)

            }
            else {
                holder.containerCL.setBackgroundResource(R.drawable.bg_20_curved_gray_borders_white_filled)
                selectedFreelancers.remove(freelancers[position])
            }



//        holder.level_TV.text = freelancers[position].freelancer_level

//            setUserImage(freelancer.image, freelance_image, NewUser.FREELANCER_USER_TYPE, freelancer.gender)
//
//            val imageId = if (freelancer.isFemale()) R.drawable.freelancer_female_cercular_100
//            else R.drawable.freelancer_male_cercular_100
//
//            if (freelancer.image == null)
//                freelance_image.imageTintList =
//                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.accent))
//
//            Glide.with(context)
//                .load(freelancer.image)
//                .placeholder(imageId)
//                .centerCrop()
//                .transform(CenterCrop(), CircleCrop())//  RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius))
//                .into(freelance_image)

            user_name_tv.text = freelancer.getFullName()
            serviceTypeTV.text = freelancer.listOfServices?.firstOrNull()?.typeOfServices
            level_TV.text = freelancer.freelancerLevel

//        holder.serviceTypeTV.text = freelancers[position].type_of_service
            if (freelancer.rating != null)
                freelancer_rating.rating = freelancer.rating?: 0f

//            user_profile_btn.setOnClickListener{
//                it.isEnabled = false
//                onClickListener.onProfileBtnClicked(freelancer)
//            }
//            if (user != null && user.isClient()){
//                favorite_a_freelancer_IB.isSelected = freelancer.favourite == true
//                favorite_a_freelancer_IB.setOnClickListener {
//                    it.isSelected = !it.isSelected
//                    onClickListener.onFavoriteBtnClicked(freelancers[position], position)
//                }
//            } else {
//                favorite_a_freelancer_IB.visibility = View.INVISIBLE
//            }
        }


    }


    override fun getItemCount(): Int {
        return freelancers.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newFreelancers: List<NewFreelancer>?): FreelancersSelectionRV_Adapter{
        if (newFreelancers != null){
            freelancers.addAll(newFreelancers)
            notifyDataSetChanged()
        }
        return this
    }

    fun setFavorite(position: Int, value: Boolean){
        freelancers[position].favourite = value
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newFreelancers: List<NewFreelancer>): FreelancersSelectionRV_Adapter{
        freelancers = newFreelancers.toMutableList()
        freelancers.forEach {
                freelancer -> if (selectedFreelancers.contains(freelancer))
                    freelancer.isSelected = true
        }
        notifyDataSetChanged()
        return this
    }

    fun filter(searchFilter: SearchFilter?) {
//        setList(freelancers.filter {
//            freelancersFilter.serviceTypes!!.contains(it.typeOfServices)
//                    || freelancersFilter.freelancerLevels!!.contains(it.freelancerLevel)
//                    || (freelancersFilter.freelancerRating != null && it.rating == freelancersFilter.freelancerRating)
//        })
    }


    interface OnClickListener{
        fun onProfileBtnClicked(freelancer: NewFreelancer)
        fun onFavoriteBtnClicked(freelancer: NewFreelancer, position: Int)
        fun onFreelancerSelected(freelancer: NewFreelancer)
        fun onFreelancerUnselected(freelancer: NewFreelancer)
    }
}