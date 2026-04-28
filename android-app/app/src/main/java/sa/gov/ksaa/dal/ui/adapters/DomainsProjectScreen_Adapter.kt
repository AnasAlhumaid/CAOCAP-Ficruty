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


class DomainsProjectScreen_Adapter(
    private var specialities: MutableList<Skill>) :
    RecyclerView.Adapter<DomainsProjectScreen_Adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var  speciality: TextView

        init {
            speciality = itemView.findViewById(R.id.serviceTypeTV2)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_projectscreen_domaid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val strings = specialities[position].proejctCategory
        holder.speciality.text = strings

    }


    override fun getItemCount(): Int {
        return specialities.size
    }

    fun setList(newBids: List<Skill>): DomainsProjectScreen_Adapter {
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