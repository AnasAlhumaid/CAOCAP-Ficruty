package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FreelancerFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Locale

class FreelancerFilesRvAdapter(
    private var attachedFiles: MutableList<FreelancerFile>,
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<FreelancerFilesRvAdapter.ViewHolder>() {

    val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
    val numberFormat = NumberFormat.getInstance(Locale("ar", "SA"))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView
        var mBtn: MaterialButton
        init {
            textView = itemView.findViewById(R.id.textView)
            mBtn = itemView.findViewById(R.id.mBtn)
        }
    }

    lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item_freelancer_file, parent, false)
        return ViewHolder(view)
    }

    lateinit var _user: NewUser
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = attachedFiles[position]
        holder.apply {
            textView.text = file.getCategory()
            textView.setOnClickListener {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(file.fileUrl)))
            }

            mBtn.visibility = View.GONE
            mBtn.setOnClickListener {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(file.fileUrl)))
            }
        }
    }


    override fun getItemCount(): Int {
        return attachedFiles.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<FreelancerFile>): FreelancerFilesRvAdapter {
        newBids.toMutableList().also { attachedFiles = it }
        notifyDataSetChanged()
        return this
    }


    interface OnClickListener {
        fun onClicked(project: NewProject)
    }
}