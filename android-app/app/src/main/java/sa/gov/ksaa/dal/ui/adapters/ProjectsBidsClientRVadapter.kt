package sa.gov.ksaa.dal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import sa.gov.ksaa.dal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import sa.gov.ksaa.dal.data.webservices.newDal.responses.GroupedBids
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser

import sa.gov.ksaa.dal.utils.ViewUlils
import java.text.DateFormat
import java.util.Locale
import kotlin.Int

class ProjectsBidsClientRVadapter(
//    private var projectsBids: MutableList<Pair<Int?, List<NewBid>>>,
    private var projectsBids: List<GroupedBids>,
    private val onClickListener: OnClickListener, override var context: Context,

    ) :
    RecyclerView.Adapter<ProjectsBidsClientRVadapter.ViewHolder>(), MyRecyclerViewAdapter,BidsClientRVadapter.OnClickListener {



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var projectsTitleTV: MaterialTextView
        var noOfBidsTV: MaterialTextView
        var arrowIV: ImageView
        var headerLL: LinearLayout
        var bidsRV: RecyclerView
        var bidsClientRVadapter: BidsClientRVadapter
        var insideCardLL: LinearLayout

        init {
            projectsTitleTV = itemView.findViewById(R.id.projectsTitleTV)
            noOfBidsTV = itemView.findViewById(R.id.noOfBidsTV)
            arrowIV = itemView.findViewById(R.id.arrowIV)
            headerLL = itemView.findViewById(R.id.headerLL)
            bidsRV = itemView.findViewById(R.id.bidsRV)

            bidsClientRVadapter = BidsClientRVadapter(mutableListOf(), this@ProjectsBidsClientRVadapter, context)
            bidsRV.adapter = bidsClientRVadapter

            insideCardLL = itemView.findViewById(R.id.insideCardLL)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_projects_bids_client, parent, false)
        return ViewHolder(view)
    }

    lateinit var _user: NewUser
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val projectId = projectsBids[position].first
//        val projectBids = projectsBids[position].second
        val project = projectsBids[position]
        val projectId = project.projectId
        val projectTitle = project.projectTitle
        val projectBids = project.listOfTenderRequestDto

        holder.apply {
            projectsTitleTV.text = projectBids[0].projectTitle
            projectsTitleTV.text = projectTitle
            noOfBidsTV.text = convertArabicNumbersToString(projectBids.size.toString())
//            bidsClientRVadapter.setList(projectBids)

            insideCardLL.setOnClickListener {
                arrowIV.isSelected = !arrowIV.isSelected
                ViewUlils.getInstance().togleVisisbilityAndGg(bidsRV, headerLL)
            }
        }

}


    override fun getItemCount(): Int {
        return projectsBids.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(groupedBids: List<GroupedBids>) {
        projectsBids = groupedBids.toMutableList()
        Log.w(javaClass.simpleName, "setMap: projectsBids = $projectsBids" )
        notifyDataSetChanged()
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun setMap(groupedBids: Map<Int?, List<NewBid>>) {
//        projectsBids = groupedBids.toList().toMutableList()
//        Log.w(javaClass.simpleName, "setMap: projectsBids = $projectsBids" )
//        notifyDataSetChanged()
//    }


    interface OnClickListener {
        fun onBidClicked(project: NewBid)
    }

    override fun onBidClicked(bid: NewBid) {
        onClickListener.onBidClicked(bid)
    }
}