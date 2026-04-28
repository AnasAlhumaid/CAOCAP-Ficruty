package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewCommentOnProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ReplyTo_aComment
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import kotlin.Int

class ReviewsRVadapter(
    private var reviews: MutableList<RatingAndReview>, val userImageResId:Int, val context: Context, val onClickListener: ReviewsRVadapter.OnSubmissionListener,) :
    RecyclerView.Adapter<ReviewsRVadapter.ViewHolder>() {

    val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
    val numberFormat = NumberFormat.getInstance(Locale("ar", "SA"))

//    var userImageResId: Int
//    init {
//        userImageResId = if (user.user_type == User.FREELANCER_USER_TYPE || user.role_id == User.CLIENT_ROLE)  R.drawable.client_user
//        else if (user.user_type == User.CLIENT_USER_TYPE || user.role_id == User.CLIENT_ROLE) R.drawable.freelancer_user
//        else 0
//    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIV: ImageView
        var userFullNameTV: TextView
        var reviewTextTV: TextView
        var dateTV: TextView
        var ratingTV: TextView
        var ratingBar: RatingBar
        var reportBtn : ImageView

        init {
            userIV = itemView.findViewById(R.id.userIV)
            userFullNameTV = itemView.findViewById(R.id.userFullNameTV)
            reviewTextTV = itemView.findViewById(R.id.reviewTextTV)
            dateTV = itemView.findViewById(R.id.dateTV)
            ratingTV = itemView.findViewById(R.id.ratingTV)
            ratingBar = itemView.findViewById(R.id.ratingBar)
            reportBtn = itemView.findViewById(R.id.spamIV)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_reviews, parent, false)
        return ViewHolder(view)
    }

    lateinit var _user: NewUser
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        holder.apply {
//            Glide.with(context)
//                .asGif()
//                .load(userImageResId)
//                .into(userIV)
            userIV.setImageResource(userImageResId)
            userFullNameTV.text = review.ratingFrom?:"عبد الله الغامدي"
            reviewTextTV.text = review.userReview?: "جودة عمل عالية انصح بالتعامل معه"
            dateTV.text = dateFormat.format((review.ratingDate?: Date()))
            ratingTV.text = numberFormat.format(review.userRating)
            ratingBar.rating = review.userRating!!

            holder.reportBtn.setOnClickListener{
                onClickListener.onSpamClicked(review,null,null)
            }
        }
    }


    override fun getItemCount(): Int {
        return reviews.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<RatingAndReview>): ReviewsRVadapter {
        newBids.toMutableList().also { reviews = it }
        notifyDataSetChanged()
        return this
    }

    fun getAvg(): Float{
        var sum = 0.0f
        val avg: Float
        val count: Int
        if (reviews.isEmpty()) {
            avg = 0.0f
            count = 0
        } else {
            reviews.forEach {review ->
                sum += review.userRating!!
            }
            avg = sum / reviews.size
        }

        return avg
    }

    interface OnSubmissionListener{

        fun onSpamClicked(comment: RatingAndReview?, commentId: Int?, reportText:String?)
    }
}