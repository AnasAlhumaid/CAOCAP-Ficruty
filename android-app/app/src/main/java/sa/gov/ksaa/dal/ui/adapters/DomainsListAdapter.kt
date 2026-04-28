package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Skill
import kotlin.Int


//class DomainsListAdapter(
//    private val activity: Activity,
//    specialities: List<String>,
//    c: Context
//) :
//    BaseAdapter() {
//    private var inflater: LayoutInflater? = null
//    private val _specialities: List<String>
//    private val context: Context
//
//    init {
//        this._specialities = specialities
//        context = c
//    }
//
//    override fun getCount(): Int {
//        return _specialities.size
//    }
//
//    override fun getItem(location: Int): Any {
//        return _specialities[location]
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
//        var convertView: View? = convertView
//        if (convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.speciality_item, parent, false)
//
//        val specialityTV = convertView!!.findViewById(R.id.speciality) as TextView
//        specialityTV.text = _specialities[position]
//        return convertView
//    }
//
//}

class DomainsListAdapter(private var specialities: MutableList<String>) :
    RecyclerView.Adapter<DomainsListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var  speciality: MaterialTextView
        lateinit var framel: FrameLayout
        init {
            speciality = itemView.findViewById(R.id.speciality)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_domain, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.speciality.text = specialities[position]
        holder.speciality.setOnClickListener {
            deleteItem(position)
        }
    }


    override fun getItemCount(): Int {
        return specialities.size
    }
    fun setList(newBids: List<String>): DomainsListAdapter {
        newBids.toMutableList().also { specialities = it }
        notifyDataSetChanged()
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int) {
        if (position < itemCount){
            specialities.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(speciality: String) {
        specialities.add(speciality)
        notifyItemInserted(specialities.size -1)
    }


    override fun toString(): String {
        return specialities[0]
    }

    fun getDoubleQuoted(): String {
        return "\"${specialities[0]}\""
    }



    fun getList(): MutableList<String>{
        return specialities
    }
    fun maxList(): String?{
        return "\"${specialities[0]}\",\"${specialities[1]}\""
    }
}