package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile

class ExperienceFilesRvAdapter(
    private var files: MutableList<MyFile>,
    private val onClickListener: OnClickListener,
    override var context: Context
) :
    RecyclerView.Adapter<ExperienceFilesRvAdapter.ViewHolder>(), MyRecyclerViewAdapter{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView
        var editBtn: MaterialButton
        var deleteBtn: MaterialButton
        var horizontal_divider: View

        init {
            textView = itemView.findViewById(R.id.textView)
            editBtn = itemView.findViewById(R.id.editBtn)
            deleteBtn = itemView.findViewById(R.id.deleteBtn)
            horizontal_divider = itemView.findViewById(R.id.horizontal_divider)



        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item_experience_file, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.apply {
            textView.text = file.description
         if (file.freelancerFile?.fileName?.isNotEmpty() == true)
            if (file.freelancerFile?.fileName!!.contains(".tmp")){
                editBtn.text = "إضافة"
            }


            textView.setOnClickListener {

                val browserIntent =
                    Intent(Intent.ACTION_VIEW, file.uri)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(browserIntent)


            }
            editBtn.setOnClickListener {
                onClickListener.editExperienceFile(file, position)
            }
            deleteBtn.setOnClickListener {
                delete(position)
                onClickListener.onExperienceFileSubmittedRemove(file)

            }
            if (position == itemCount)
                horizontal_divider.visibility = View.INVISIBLE
        }
    }


    override fun getItemCount(): Int {
        return files.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newBids: List<MyFile>?): ExperienceFilesRvAdapter {
        if (newBids != null){
            newBids.toMutableList().also { files = it }
            notifyDataSetChanged()
        }
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newBids: List<MyFile>?): ExperienceFilesRvAdapter {
        if (newBids != null){
            for (file in newBids){
                files.add(file)
            }
            notifyDataSetChanged()
        }
        return this
    }

    fun getList(): List<MyFile> {
        return files
    }

    fun delete(position: Int){
        if (position < itemCount){
            files.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)

        }

    }

    fun update(it: MyFile, pos: Int) {
//        files[pos] = it
        notifyItemChanged(pos, it)
    }
    fun add(it: MyFile) {
        files.add(it)
        notifyItemInserted(files.size-1)
    }

    fun updateOrAdd(file: MyFile) {
        if (files.contains(file)){
            update(file, getIndex(file))
        } else {
            add(file)
        }

    }

    fun getIndex(file: MyFile): Int{
        files.forEachIndexed{ index, myFile ->
            if (myFile == file)
                return index
        }
        return -1
    }


    interface OnClickListener {
        fun editExperienceFile(file: MyFile, pos: Int)
       fun onExperienceFileSubmittedRemove (file:MyFile)


    }
}