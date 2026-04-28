package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel

class ServiceRVadapter(override var context: Context, private var newService: MutableList<ServicesModel>,):  RecyclerView.Adapter<ServiceRVadapter.ViewHolder>(), MyRecyclerViewAdapter {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.fragment_service5_card_front,
                parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var nameBack : TextView
        var description: TextView
        var imageIV: ImageView



        init {
            name = itemView.findViewById(R.id.front1ttl)
            description = itemView.findViewById(R.id.textView32)
            imageIV = itemView.findViewById(R.id.front1IV)
            nameBack = itemView.findViewById(R.id.textView14)

        }
    }


    override fun onBindViewHolder(holder: ServiceRVadapter.ViewHolder, position: Int) {
        val service = newService[position]

        holder.apply {

            name.text = service.name
            description.text = service.description
            nameBack.text = service.name


            Glide.with(context)
                .load(service.image)
                .placeholder(R.drawable.tadqeek)

//                .transform(
//                    CenterCrop(),
//                    CircleCrop()
//                )//  RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius))
                .into(imageIV)
        }
    }

    lateinit var  originalList: MutableList<ServicesModel>
    @SuppressLint("NotifyDataSetChanged")
    fun setList(newNewProjects: List<ServicesModel>?): ServiceRVadapter {
        if (newNewProjects != null) {
            newNewProjects.toMutableList().also {
                newService = it
            }
            originalList = newService
            notifyDataSetChanged()
        }
        return this
    }

    override fun getItemCount(): Int {
        return newService.size
    }
}