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
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import sa.gov.ksaa.dal.data.webservices.newDal.responses.GroupedBids
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.text.DateFormat
import java.util.Locale
import kotlin.Int

class BidsClientRVadapter(
    private var bids: MutableList<GroupedBids>,
    private val onClickListener: OnClickListener, override var context: Context
) :
    RecyclerView.Adapter<BidsClientRVadapter.ViewHolder>(), MyRecyclerViewAdapter{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIV: ImageView
        var freelancerNameTV: TextView
        var ratingBar: RatingBar
        var titleTV: TextView
        var costTV: TextView
        var remainingDaysTV: TextView
        var detailsBtn: Button

        init {
            userIV = itemView.findViewById(R.id.userIV)
            freelancerNameTV = itemView.findViewById(R.id.freelancerNameTV)
            ratingBar = itemView.findViewById(R.id.ratingBar)
            titleTV = itemView.findViewById(R.id.titleTV)
            costTV = itemView.findViewById(R.id.costTV)
            remainingDaysTV = itemView.findViewById(R.id.remainingDaysTV)
            detailsBtn = itemView.findViewById(R.id.detailsBtn)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item_projects_bid_client, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bid = bids[position].listOfTenderRequestDto[0]
        holder.apply {

            setUserImage(bid.image, userIV, NewUser.FREELANCER_USER_TYPE, bid.gender)

            freelancerNameTV.text = "${bid.freelancerName} ${bid.freelancerLastName}"



            bid.rating?.let {

                ratingBar.rating = it.toFloat()
            }
            titleTV.text =  bid.projectTitle
            val numberAm: Int = try {
                bid.freelancerBiddingAmount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
            } catch (e: NumberFormatException) {
                0 // Set to 0 if conversion fails
            }
            val formattedNumberAm = when (numberAm) {
                is Int, -> numberFormat.format(numberAm) // Format if it's a number
                // Use the string as is
                else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
            }

            costTV.text =  formattedNumberAm


            remainingDaysTV.text = numberFormat.format(bid.freelancerExpectedTime?.trim()?.toInt() ?: 0)
            detailsBtn.setOnClickListener {
                onClickListener.onBidClicked(bids[position].listOfTenderRequestDto[0])
            }
        }

    }


    override fun getItemCount(): Int {
        return bids.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<GroupedBids>): BidsClientRVadapter {
        newBids.toMutableList().also { bids = it }
        notifyDataSetChanged()
        return this
    }


    interface OnClickListener {
        fun onBidClicked(project: NewBid)
    }
}