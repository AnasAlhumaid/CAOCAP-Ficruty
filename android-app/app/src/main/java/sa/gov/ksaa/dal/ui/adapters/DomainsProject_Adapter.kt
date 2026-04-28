package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Skill


class DomainsProject_Adapter(
    private var specialities: MutableList<Skill>) :
    RecyclerView.Adapter<DomainsProject_Adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var  speciality: TextView

        init {
            speciality = itemView.findViewById(R.id.serviceTypeTV1)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_domaidn_on_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val strings = specialities[position].proejctCategory
        if( strings != null){
            holder.speciality.text = strings
        }else{
            holder.speciality.text = specialities[position].typeOfServices
        }


    }
    @SuppressLint("NotifyDataSetChanged")
    fun addItem(speciality: Skill) {
        specialities.add(speciality)
        notifyItemInserted(specialities.size - 1)
    }


    override fun getItemCount(): Int {
        return specialities.size
    }

    fun setList(newBids: List<Skill>): DomainsProject_Adapter {
        newBids.toMutableList().also { specialities = it }
        notifyDataSetChanged()
        return this
    }

    override fun toString(): String {
        return "\'${specialities[0]}\'"
    }

    fun getDoubleQuoted(): String {
        return "\"${specialities[0]}\""
    }



    fun getList(): MutableList<Skill>{
        return specialities
    }


}