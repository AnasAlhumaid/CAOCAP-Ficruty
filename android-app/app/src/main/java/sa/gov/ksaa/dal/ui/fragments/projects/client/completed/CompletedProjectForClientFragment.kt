package sa.gov.ksaa.dal.ui.fragments.projects.client.completed


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.adapters.ProjectDeliverableCompelete_Adapter
import sa.gov.ksaa.dal.ui.adapters.ProjectDeliverableRV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancerProfileFragment

import sa.gov.ksaa.dal.ui.viewModels.CompletedProjectsVM
import sa.gov.ksaa.dal.ui.viewModels.DeliverablesVM
import java.util.Date

class CompletedProjectForClientFragment : BaseFragment(R.layout.fragment_project_completed_4_client),
    ProjectDeliverableCompelete_Adapter.OnClickListener {

    companion object {
        const val closedProjectIdKey = "closedProjectId"
    }
    val closedProjectsVM: CompletedProjectsVM by viewModels()
    val deliverablesVM: DeliverablesVM by viewModels()

    var closedProjectId: Int? = 0
    lateinit var closedProject: ClosedProject
    private lateinit var projectDeliverablervAdapter: ProjectDeliverableCompelete_Adapter

    lateinit var filesList: List<NewDeliverableFile>


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)

        projectDeliverablervAdapter = ProjectDeliverableCompelete_Adapter(mutableListOf(), this)
        deliverableRV.adapter = projectDeliverablervAdapter


//        closedProjectId = arguments?.getInt(closedProjectIdKey, 0)
//        closedProjectId?.let {
//            closedProjectsVM.getById(it)
//                .observe(viewLifecycleOwner){ res ->
//                    handleSuccessOrErrorResponse(res, {newClosedProject ->
//                        closedProject = newClosedProject
//                        updateUI()
//                    })
//                }
//        }

        closedProject = activityVM.closedProject.value!!
        updateUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        projectTitleTV.text = closedProject.projectTitle
        startDateTV.text = dateFormatAr.format(closedProject.startDate?: Date())
        remainingDaysTV.text = convertArabicNumbersToString( closedProject.durationOfProject)

        val cost: Int = try {
            closedProject.amount?.toInt() ?: 0 // Convert amount to Int or set to 0 if null
        } catch (e: NumberFormatException) {
            0 // Set to 0 if conversion fails
        }
        val formattedCost = when (cost) {
            is Int, -> numberFormat.format(cost) // Format if it's a number
            // Use the string as is
            else -> throw IllegalArgumentException("numberAm must be a number or a string") // Handle unexpected types
        }
        costTV.text = formattedCost
        paymentStatusTV.text = "تم الدفع"

        setOtherUserImage(closedProject.image, freelancerIV, NewUser.FREELANCER_USER_TYPE, closedProject.gender)

//        freelancerIV.setOnClickListener {
//            findNavController()
//                .navigate(R.id.action_completedProjectForClientFragment_to_freelancerProfileFragment,
//                    bundleOf(FreelancerProfileFragment.freelancerUserIdKey to closedProject.userId)
//                )
//        }

        freelancerNameTV.text = "${closedProject.freelancerName} ${closedProject.freelancerLastName}"


        // projectId
        val params = mutableMapOf(
            "projectId" to closedProject.projectId.toString()
        )
        Log.i(javaClass.simpleName, "updateUI: deliverablesVM.getAll($params)")

        deliverablesVM.getAll(params)
            .observe(viewLifecycleOwner){ res ->
                newHandleSuccessOrErrorResponse(res,
                    onSuccess = { file ->

                        filesList = file.filter {
                            !it.imageUrl?.endsWith(".tmp" , false)!!

                        }
                        projectDeliverablervAdapter.setList(filesList)
                    }
//                    {
//                    if (it.isEmpty()) {
//                        nodateTV.visibility = View.VISIBLE
//                        deliverablesCard.visibility = View.GONE
//                    } else {
//                        projectDeliverablervAdapter.setList(it)
//                    }
//                }

                )
            }
    }


    lateinit var projectTitleTV: TextView
    lateinit var startDateTV: TextView
    lateinit var remainingDaysTV: TextView
    lateinit var costTV: TextView
    lateinit var paymentStatusTV: TextView
    lateinit var freelancerIV: ImageView
    lateinit var freelancerNameTV: TextView
    lateinit var deliverableRV: RecyclerView

    private fun initViews(createdView: View) {
        projectTitleTV = createdView.findViewById(R.id.projectTitleTV)
        startDateTV = createdView.findViewById(R.id.startDateTV)
        remainingDaysTV = createdView.findViewById(R.id.remainingDaysTV)
        costTV = createdView.findViewById(R.id.costTV)
        paymentStatusTV = createdView.findViewById(R.id.paymentStatusTV)
        freelancerIV = createdView.findViewById(R.id.freelancerIV)
        freelancerNameTV = createdView.findViewById(R.id.freelancerNameTV)
        deliverableRV = createdView.findViewById(R.id.deliverableRV)
        deliverablesCard = createdView.findViewById(R.id.deliverablesCard)
        nodateTV = createdView.findViewById(R.id.nodateTV)
    }

    lateinit var deliverablesCard: CardView
    lateinit var nodateTV: TextView

    override fun onDeliverableClicked(deliverable: NewDeliverableFile) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deliverable.imageUrl))
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(browserIntent)
//        activityVM.deliverable.postValue(deliverable)
//        findNavController()
//            .navigate(R.id.action_completedProjectForClientFragment_to_fileViewerFragment)
    }
    override fun onDeleteDeliverabl(deliverable: NewDeliverableFile) {
        TODO("Not yet implemented")
    }
}