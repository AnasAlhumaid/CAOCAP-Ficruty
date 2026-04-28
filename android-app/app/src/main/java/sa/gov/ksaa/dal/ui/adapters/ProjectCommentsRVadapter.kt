package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.icu.text.RelativeDateTimeFormatter
import android.icu.text.RelativeDateTimeFormatter.RelativeUnit
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.CommentOnProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewCommentOnProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ReplyTo_aComment
import sa.gov.ksaa.dal.ui.fragments.projects.details.SpamDialog
import sa.gov.ksaa.dal.ui.fragments.technicalSupport.addEditTechnicalSupportTicket.AddEditTechnicalSupportTicket
import sa.gov.ksaa.dal.ui.viewModels.CommentsOnProjectVM
import java.lang.Error
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ProjectCommentsRVadapter(
    private var comments: MutableList<NewCommentOnProject>, val _user: NewUser?, val project: NewProject, private val onClickListener: OnSubmissionListener,
) :
    RecyclerView.Adapter<ProjectCommentsRVadapter.ViewHolder>() {

    var simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    var arabicDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
    var numberFormat = NumberFormat.getInstance(Locale("ar", "SA"))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIV: ImageView
        var userNameTV: TextView
        var dateTV: TextView
        var commentTV: TextView
        var spamIV: ImageButton
        var repliesCountTV: TextView
        var replyOrRepliesTV: TextView
        var replyLL: LinearLayout
        var repliesRV: RecyclerView
        var replyET: EditText
        var replyFab: FloatingActionButton

        init {
            userIV = itemView.findViewById(R.id.userIV)
            userNameTV = itemView.findViewById(R.id.userNameTV)
            dateTV = itemView.findViewById(R.id.dateTV)
            commentTV = itemView.findViewById(R.id.commentTV)
            spamIV = itemView.findViewById(R.id.spamIV)

            repliesCountTV = itemView.findViewById(R.id.repliesCountTV)
            replyOrRepliesTV = itemView.findViewById(R.id.replyOrRepliesTV)
            replyLL = itemView.findViewById(R.id.replyLL)
            repliesRV = itemView.findViewById(R.id.repliesRV)
            replyET = itemView.findViewById(R.id.replyET)
            replyFab = itemView.findViewById(R.id.replyFab)
        }
    }

    lateinit var inflatedView: View


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_project_comment, parent, false)
        return ViewHolder(inflatedView)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]

        val fmt = RelativeDateTimeFormatter.getInstance()

        if (_user == null){
            holder.spamIV.visibility = View.GONE
        }else{
            holder.spamIV.visibility = View.VISIBLE
        }


        holder.apply {
            userNameTV.text = comment.commentFrom?: ""
            comment.commentDate.let {
                dateTV.text = it?.let { it1 ->
                    convertArabicNumbersToString( relativeTime(it1))

                }
            }
            commentTV.text = comment.comment?: ""



            comment.replyComment
            val repliesRV_Adapter = ProjectCommentRepliesRV_Adapter(mutableListOf(), _user,onClickListener)
            repliesCountTV.text = numberFormat.format(repliesRV_Adapter.itemCount)
            replyOrRepliesTV.text = when(repliesRV_Adapter.itemCount) {
                0, 1, 2 -> "اجابة"
                else -> "اجابات"
            }
            repliesRV.adapter = repliesRV_Adapter
            repliesRV_Adapter.setList(comment.replyComment)

//            if (comment.user == _user) {
//                it.delIV.visibility = View.VISIBLE
//                it.delIV.setOnClickListener {
//                    removeComment(position)
//                }
//            }

            replyFab.setOnClickListener {
                if (isValidReply(holder)){
                    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                    repliesRV_Adapter.addReply(ReplyTo_aComment(
                        null,
                        gender = comment.gender,
                        null,
                        null,
                        newReply,
                        formatter.format(Date()),_user?.getFullName(),_user?.userId,project.id

                    )
                    )

                    onClickListener.onRCommentSubmitted(ReplyTo_aComment(comment.id,null,null,null,newReply,null,null,_user?.userId,project.id))
                    replyET.text.clear()



                }

            }
            spamIV.setOnClickListener {
                if (_user != null) {


                    onClickListener.onSpamClicked(comment, null, null)

                }
            }

            if (_user == null){
                replyLL.visibility = View.INVISIBLE
            } else if (_user.isClient()) {
                if (_user.userId == project.userId){
                    replyLL.visibility = View.VISIBLE

                } else {
                    replyLL.visibility = View.INVISIBLE
                }

            } else if(_user.isFreelancer()){
                if (comment.freelancerName.equals(_user.firstName)){
                        replyLL.visibility = View.INVISIBLE
                        userNameTV.text = "انت"
                    } else {

                    replyLL.visibility = View.INVISIBLE
                }

            } else {

                replyLL.visibility = View.INVISIBLE

            }

        }

    }

    lateinit var newReply: String
    fun isValidReply(viewHolder: ViewHolder): Boolean {
        newReply = viewHolder.replyET.text.toString().trim()
        if (newReply.isEmpty()){
            viewHolder.replyET.error = inflatedView.context.getString(R.string.this_field_is_required)
            viewHolder.replyET.requestFocus()
            return false
        }

        return true
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newComments: List<NewCommentOnProject>): ProjectCommentsRVadapter {
        newComments.toMutableList().also { comments = it }
        notifyDataSetChanged()
        return this
    }

    fun addComment(newComment: NewCommentOnProject) {
        comments.add(newComment)
        notifyItemInserted(comments.size-1)
    }

    fun removeComment(position: Int) {

        if (position in 0 until itemCount){
            comments.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
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

    interface OnSubmissionListener{
        fun onRCommentSubmitted(newRComment: ReplyTo_aComment)
        fun onSpamClicked(comment: NewCommentOnProject?, commentId: Int?,reportText:String?)
    }

}