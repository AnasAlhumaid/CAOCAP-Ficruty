package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.FreelancerFilter
import sa.gov.ksaa.dal.data.models.ProjectsFilter
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancersFilterBottomSheetModal


class FilterServiceAdapter(private var services: MutableList<String>,
                           private val onClickListener: FilterServiceAdapter.OnClickListener,


                           var selectedListener: String,
                           var filter: FreelancerFilter? = null,
                           var filterProject : ProjectsFilter? = null,
                           var context: Context) :
    RecyclerView.Adapter<FilterServiceAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var  service: SwitchMaterial
        lateinit var framel: FrameLayout
        init {
            service = itemView.findViewById(R.id.filtersSwitch)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_grid_filters, parent, false)
        return ViewHolder(view)
    }

    var isSelected = false
    var isSelectedtext = ""
    var statSelected = false

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.service.text = services[position]
        if(!filter?.services.isNullOrEmpty()) {

            holder.service.isChecked = filter!!.services!!.contains(services[position])
        }

        if (!filterProject?.services.isNullOrEmpty()){
            holder.service.isChecked = filterProject!!.services!!.contains(services[position])
        }





        holder.service.setOnClickListener {
            if (!holder.service.isChecked ){
                onClickListener.onSwitchClicked(null)
                filter?.services?.remove(services[position])
                filterProject?.services?.remove(services[position])
            }else{
                onClickListener.onSwitchClicked(services[position])
            }

            isSelected =  holder.service.isChecked
            isSelectedtext = services[position]



        }
    }


    override fun getItemCount(): Int {
        return services.size
    }
    fun setList(newBids: List<String>): FilterServiceAdapter {
        newBids.toMutableList().also { services = it }
        notifyDataSetChanged()
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int) {
        if (position < itemCount){
            services.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(speciality: String) {
        services.add(speciality)
        notifyItemInserted(services.size -1)
    }


    override fun toString(): String {
        return "\'${services[0]}\'"
    }

    fun getDoubleQuoted(): String {
        return isSelectedtext
    }

    fun isSelectedS() : Boolean {
       return statSelected
    }



    fun getList(): MutableList<String>{
        return services
    }
    fun maxList(): String?{
        return "\"${services[0]}\",\"${services[1]}\""
    }

    interface OnClickListener {
        fun onSwitchClicked(name: String?)

    }


}