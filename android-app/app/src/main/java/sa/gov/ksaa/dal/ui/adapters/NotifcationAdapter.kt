package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Notifcation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class NotifcationAdapter(
    private var notifcations: MutableList<Notifcation>, private val onClickListener: NotifcationAdapter.OnClickListener

) :
    RecyclerView.Adapter<NotifcationAdapter.ViewHolder>(){

        val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
        val numberFormat = NumberFormat.getInstance(Locale("ar", "SA"))

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title: TextView
            var dateTV: TextView


            init {
                title = itemView.findViewById(R.id.notifcationDetails)
                dateTV = itemView.findViewById(R.id.notifcationDate)

            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_notifcation, parent, false)
            return ViewHolder(view)
        }

        lateinit var _user: NewUser
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val notifcation = notifcations[position]
            holder.let {
                it.title.text = notifcation.notificationn

                val numberAm: Int = try {
                    notifcation.time?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
                } catch (e: NumberFormatException) {
                    0 // Set to 0 if conversion fails
                }
                val formattedNumberAm = when (numberAm) {
                    is Int, -> numberFormat.format(numberAm) // Format if it's a number
                    // Use the string as is
                    else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
                }
                it.dateTV.text = formattedNumberAm

                it.title.setOnClickListener {
                    onClickListener.onClicked()
                }


            }

        }


        override fun getItemCount(): Int {
            return notifcations.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setList(newTickets: List<Notifcation>): NotifcationAdapter {
            newTickets.toMutableList().also { notifcations = it }
            notifyDataSetChanged()
            return this
        }

    interface OnClickListener {
        fun onClicked()
    }

}