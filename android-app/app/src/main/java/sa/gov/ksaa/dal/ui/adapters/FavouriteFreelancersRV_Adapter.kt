package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FavouriteFreelancer
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.Int

class FavouriteFreelancersRV_Adapter(
    private var freelancers: MutableList<FavouriteFreelancer>,
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<FavouriteFreelancersRV_Adapter.ViewHolder>() {

    val arabicDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
    val numberFormat = NumberFormat.getInstance(Locale("ar", "SA"))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var level_TV: TextView
        var favorite_a_freelancer_IB: ImageView
        var freelance_image: ImageView
        var user_name_tv: TextView
        var serviceTypeTV: TextView
//        var ratingValueTV: TextView
        var freelancer_rating: RatingBar
        var noReviewsTV: TextView
        var user_profile_btn: Button

        init {
            level_TV = itemView.findViewById(R.id.level_TV)
            favorite_a_freelancer_IB = itemView.findViewById(R.id.favorite_a_freelancer_IB)
            freelance_image = itemView.findViewById(R.id.freelance_image)
            user_name_tv = itemView.findViewById(R.id.user_name_tv)
            serviceTypeTV = itemView.findViewById(R.id.serviceTypeTV)
//            ratingValueTV = itemView.findViewById(R.id.ratingValueTV)
            freelancer_rating = itemView.findViewById(R.id.freelancer_rating)
            noReviewsTV = itemView.findViewById(R.id.noReviewsTV)
            user_profile_btn = itemView.findViewById(R.id.user_profile_btn)
        }
    }

    lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_freelancer, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favFreelancer = freelancers[position]

        holder.apply {
            level_TV.text = favFreelancer.freelancerLevel?: "متمرس"


            val imageId = if (favFreelancer.isFemale()) R.drawable.freelancer_female_cercular_100
            else R.drawable.freelancer_male_cercular_100

            if (favFreelancer.image == null)
                freelance_image.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.accent))

            Glide.with(context)
                .load(favFreelancer.image)
                .placeholder(imageId)
                .centerCrop()
                .transform(CenterCrop(), CircleCrop())//  RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius))
                .into(freelance_image)

            //        Glide.with(fragment)
//            .load(freelancers[position].files)
//            .centerCrop()
//            .placeholder(R.drawable.shimagh)
//            .into(holder.freelance_image);

            favorite_a_freelancer_IB.isSelected = true
            favorite_a_freelancer_IB.setOnClickListener {
                Log.i(javaClass.simpleName, "onBindViewHolder: position = $position")
                if (position < freelancers.size && position >= 0) {
                    onClickListener.onUnFavoriteBtnClicked(freelancers[position], position)
                }
            }

            user_name_tv.text = "${favFreelancer.freelancerName} ${favFreelancer.freelancerLastName}"
            serviceTypeTV.text = favFreelancer.typeOfService?: "ترجمة"

//            ratingValueTV.text = numberFormat.format(favFreelancer.rating?:0)
            freelancer_rating.rating = favFreelancer.rating?:0.0f
            noReviewsTV.text = numberFormat.format(favFreelancer.reviewCount?:0)
            user_profile_btn.setOnClickListener {
                onClickListener.onProfileBtnClicked(favFreelancer)
            }
        }
    }


    override fun getItemCount(): Int {
        return freelancers.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newFreelancers: List<FavouriteFreelancer>): FavouriteFreelancersRV_Adapter {
        freelancers = newFreelancers.toMutableList()
        notifyDataSetChanged()
        return this
    }

    fun deleteFreelancerAt(position: Int): FavouriteFreelancersRV_Adapter {
        if (position < itemCount){
            freelancers.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
        return this
    }


    interface OnClickListener {
        fun onProfileBtnClicked(freelancer: FavouriteFreelancer)
        fun onUnFavoriteBtnClicked(freelancer: FavouriteFreelancer, position: Int)
    }
}