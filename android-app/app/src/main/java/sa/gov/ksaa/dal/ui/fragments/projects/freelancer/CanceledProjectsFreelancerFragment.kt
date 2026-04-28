package sa.gov.ksaa.dal.ui.fragments.projects.freelancer

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
class CanceledProjectsFreelancerFragment : BaseFragment(R.layout.fragment_projects_canceled_client) {

    var canceledProjs: List<Project>? = null
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)
        canceledProTV.text = numberFormat.format(0)
    }

    lateinit var canceledProTV: TextView
    lateinit var canceledProjectsRV: RecyclerView
    lateinit var showMoreBtn: Button

    private fun initViews(createdView: View) {
        canceledProTV = createdView.findViewById(R.id.canceledProTV)
        canceledProjectsRV = createdView.findViewById(R.id.canceledProjectsRV)
        showMoreBtn = createdView.findViewById(R.id.showMoreBtn)
        if (canceledProjs == null)
            showMoreBtn.visibility = View.GONE
    }
}