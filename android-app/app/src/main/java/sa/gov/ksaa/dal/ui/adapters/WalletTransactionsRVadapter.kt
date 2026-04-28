package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.models.Review
import sa.gov.ksaa.dal.data.models.WalletTransaction
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import kotlin.Int

class WalletTransactionsRVadapter(
    private var transactions: MutableList<WalletTransaction>, val context: Context) :
    RecyclerView.Adapter<WalletTransactionsRVadapter.ViewHolder>() {

    val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
    val numberFormat = NumberFormat.getInstance(Locale("ar", "SA"))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var paymentStatusTV: TextView
        var dateTV: TextView
        var senderTV: TextView
        var amountTV: TextView
        var transactionIdTV: TextView

        init {
            paymentStatusTV = itemView.findViewById(R.id.paymentStatusTV)
            dateTV = itemView.findViewById(R.id.dateTV)
            senderTV = itemView.findViewById(R.id.senderTV)
            amountTV = itemView.findViewById(R.id.amountTV)
            transactionIdTV = itemView.findViewById(R.id.transactionIdTV)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_wallet_transaction, parent, false)
        return ViewHolder(view)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.apply {
            val status = transaction.payment_status?: WalletTransaction.PAIED_STATUS
            paymentStatusTV.text = status
            when(status) {
                WalletTransaction.PAIED_STATUS -> paymentStatusTV.setTextColor(context.resources.getColor(R.color.accent))
                WalletTransaction.REJECTED_STATUS -> paymentStatusTV.setTextColor(context.resources.getColor(android.R.color.holo_red_dark))
                WalletTransaction.SUSPENDED_STATUS -> paymentStatusTV.setTextColor(context.resources.getColor(android.R.color.holo_orange_dark))
            }
            dateTV.text = dateFormat.format(transaction.transaction_date?: Date())
            senderTV.text = transaction.sender?.getFullName()
            amountTV.text = numberFormat.format(transaction.amount_sar?: 100) + " ريال"
            transactionIdTV.text = numberFormat.format(transactionIdTV.text.toString().toLong())
        }
    }


    override fun getItemCount(): Int {
        return transactions.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<WalletTransaction>): WalletTransactionsRVadapter {
        newBids.toMutableList().also { transactions = it }
        notifyDataSetChanged()
        return this
    }
}