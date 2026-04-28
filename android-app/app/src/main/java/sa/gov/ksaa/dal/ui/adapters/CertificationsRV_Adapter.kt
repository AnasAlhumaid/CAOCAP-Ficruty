package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.data.models.Certification
import kotlin.Int

class CertificationsRV_Adapter(
    private var certifications: MutableList<Certification>):
    RecyclerView.Adapter<CertificationsRV_Adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       var certTypeTV: TextView
       var cert_instituteTV: TextView
       var dateYearTV: TextView

        init {
            certTypeTV = itemView.findViewById(R.id.certTypeTV)
            cert_instituteTV = itemView.findViewById(R.id.cert_instituteTV)
            dateYearTV = itemView.findViewById(R.id.dateYearTV)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_freelancer_profile_certificates, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.certTypeTV.text = certifications[position].type
        holder.cert_instituteTV.text = certifications[position].institute_name
        holder.dateYearTV.text = certifications[position].getFormattedGraduationYear()
    }


    override fun getItemCount(): Int {
        return certifications.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newProjects: List<Certification>): CertificationsRV_Adapter {
        certifications = newProjects.toMutableList()
        notifyDataSetChanged()
        return this
    }

}