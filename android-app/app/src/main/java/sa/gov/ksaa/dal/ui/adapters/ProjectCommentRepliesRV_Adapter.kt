package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.models.CommentOnProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ReplyTo_aComment
import java.lang.Error
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.Int

class ProjectCommentRepliesRV_Adapter(var replies: MutableList<ReplyTo_aComment>, val user: NewUser?,private val onClickListener: ProjectCommentsRVadapter.OnSubmissionListener) : RecyclerView.Adapter<ProjectCommentRepliesRV_Adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTV: TextView
        var dateTV: TextView
        var commentTV: TextView
        var delIV: ImageView

        init {
            userNameTV = itemView.findViewById(R.id.userNameTV)
            dateTV = itemView.findViewById(R.id.dateTV)
            commentTV = itemView.findViewById(R.id.commentTV)
            delIV = itemView.findViewById(R.id.delIV)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment_reply, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = replies[position]
        holder.let {
            it.userNameTV.text = comment.replyFrom
             comment.rCommentDate.let {
                holder.dateTV.text  = it?.let { it1 ->
                    convertArabicNumbersToString(relativeTime(it1))

                }
            }
            it.commentTV.text = comment.replyComment?: ""
            if (comment.userId == user?.userId){
                it.delIV.visibility = View.VISIBLE
                it.delIV.setOnClickListener {
                    onClickListener.onSpamClicked(null,comment.id,"")
//                    removeReply(position)
                }
            }
        }


    }
    fun convertArabicNumbersToString(text: String?): String {
        val arabicToWesternMap = mapOf(
            '0' to '٠' , '1' to '١'   , '2' to  '٢','3'  to '٣', '4' to  '٤',
            '5' to  '٥', '6'  to '٦', '7' to  '٧', '8' to  '٨','9' to  '٩'
        )
        val builder = text?.let { StringBuilder(it.length) }
        if (text != null) {
            for (char in text) {
                if (builder != null) {
                    builder.append(arabicToWesternMap[char] ?: char)
                }
            }
        }
        return builder.toString()
    }
    fun relativeTime(dateString: String): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH) // Parse the date string
        val date: Date? = try {
            formatter.parse(dateString)
        } catch (e: Error) {
            e.printStackTrace()
            return "Invalid Date"
        }

        if (date != null) {
            val now = Date()
            val diff = now.time - date.time

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 30 // Approximate calculation for months

            val unit: String = when {
                months > 0 -> "$months شهر${if (months > 1) "" else ""}"
                days > 0 -> "$days يوم${if (days > 1) "" else ""}"

                else -> return "الآن"
            }

            return " قبل $unit"
        }
        return "Invalid Date"
    }
    override fun getItemCount(): Int {
        return replies.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newComments: List<ReplyTo_aComment>?): ProjectCommentRepliesRV_Adapter {
        if (newComments != null) {
            replies.addAll(newComments)
            notifyDataSetChanged()
        }
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newComments: List<ReplyTo_aComment>?): ProjectCommentRepliesRV_Adapter {
        if (newComments != null) {
            newComments.toMutableList().also { replies = it }
            notifyDataSetChanged()
        }
        return this
    }

    fun addReply(newComment: ReplyTo_aComment) {
        replies.add(newComment)
        notifyItemInserted(itemCount-1)
    }

    fun removeReply(position: Int) {

        if (position in 0 until itemCount){
            replies.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
    }

}