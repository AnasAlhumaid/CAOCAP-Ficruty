package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.models.ChatMessage
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewChatMessage
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.Int

class ChatMessagesRV_Adapter(
    private var messages: MutableList<NewChatMessage>, var _user: NewUser
):
    RecyclerView.Adapter<ChatMessagesRV_Adapter.ViewHolder>() {
    val simpleDateFormatAr = SimpleDateFormat("h:mm a", Locale("ar", "SA"))
    var simpleDateFormatEn = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var receivedLL: LinearLayout
        var receivedMessageTV: TextView
        var receivedDateTV: TextView
//        var senderIV: ImageView

        var sentLL: LinearLayout
        var sentMessageTV: TextView
        var sentDateTV: TextView

        init {
            receivedLL = itemView.findViewById(R.id.receivedLL)
            receivedMessageTV = itemView.findViewById(R.id.receivedMessageTV)
            receivedDateTV = itemView.findViewById(R.id.receivedDateTV)
//            senderIV = itemView.findViewById(R.id.senderIV)
            sentLL = itemView.findViewById(R.id.sentLL)
            sentMessageTV = itemView.findViewById(R.id.sentMessageTV)
            sentDateTV = itemView.findViewById(R.id.sentDateTV)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        Log.w(javaClass.simpleName, "onBindViewHolder: message = $message \n size = ${messages.size}")

        holder.apply {
            if(message.sendById == _user.userId){
                receivedLL.visibility = View.GONE
                sentLL.visibility = View.VISIBLE
                sentMessageTV.text = message.message
                sentDateTV.text = simpleDateFormatEn.parse(message.createDate?: simpleDateFormatEn.format(Date()))
                    ?.let { simpleDateFormatAr.format(it) }

            } else {
                sentLL.visibility = View.GONE
                receivedLL.visibility = View.VISIBLE
                receivedMessageTV.text = message.message
                receivedDateTV.text = simpleDateFormatEn.parse(message.createDate?: simpleDateFormatEn.format(Date()))
                    ?.let { simpleDateFormatAr.format(it) }
            }
        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newMessagess: List<NewChatMessage>): ChatMessagesRV_Adapter {
        Log.w(javaClass.simpleName, "setList: newMessagess = $newMessagess")
        newMessagess.toMutableList().also { messages = it }
        notifyDataSetChanged()
        return this
    }

    fun addMessage(newMessage: NewChatMessage): ChatMessagesRV_Adapter {
        messages.add(newMessage)
        notifyItemInserted(messages.size-1)
        return this
    }
}