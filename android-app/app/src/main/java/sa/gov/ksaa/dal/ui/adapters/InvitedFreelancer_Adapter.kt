package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser


class InvitedFreelancer_Adapter(private var freelancers: MutableList<NewFreelancer>,

                                     val user: NewUser?,
                                     override var context: Context
) :
    RecyclerView.Adapter<InvitedFreelancer_Adapter.ViewHolder>(), MyRecyclerViewAdapter{
    var selectedFreelancers = mutableSetOf<NewFreelancer>()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){



        var  user_name_tv: TextView
        var  serviceTypeTV: TextView
        var  freelancer_rating: RatingBar
        var  deleteBtn: ImageButton
        init {

            user_name_tv = itemView.findViewById(R.id.user_name_tv)
            serviceTypeTV = itemView.findViewById(R.id.serviceTypeTV)
            freelancer_rating = itemView.findViewById(R.id.freelancer_rating)
            deleteBtn = itemView.findViewById(R.id.deleteBtn)
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.invited_freelancer_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val freelancer = freelancers[position]
        holder.apply {
            deleteBtn.setOnClickListener {


                deleteItem(position)

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
    fun clearData(){
        freelancers.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newFreelancers: NewFreelancer?): InvitedFreelancer_Adapter{
        if (newFreelancers != null){



freelancers.add(newFreelancers)

            notifyDataSetChanged()
        }
        return this
    }

    fun setFavorite(position: Int, value: Boolean){
        freelancers[position].favourite = value
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newFreelancers: List<NewFreelancer>): InvitedFreelancer_Adapter{
        freelancers = newFreelancers.toMutableList()
        freelancers.forEach {
                freelancer -> if (selectedFreelancers.contains(freelancer))
            freelancer.isSelected = true
        }
        notifyDataSetChanged()
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int) {
        if (position < itemCount){
            freelancers.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
    }

    fun getList(): MutableList<NewFreelancer>{
        return freelancers
    }

    fun filter(searchFilter: SearchFilter?) {
//        setList(freelancers.filter {
//            freelancersFilter.serviceTypes!!.contains(it.typeOfServices)
//                    || freelancersFilter.freelancerLevels!!.contains(it.freelancerLevel)
//                    || (freelancersFilter.freelancerRating != null && it.rating == freelancersFilter.freelancerRating)
//        })
    }


//    interface OnClickListener{
//
//        fun onFreelancerSelected(freelancer: NewFreelancer)
//        fun onFreelancerUnselected(freelancer: NewFreelancer)
//    }
}