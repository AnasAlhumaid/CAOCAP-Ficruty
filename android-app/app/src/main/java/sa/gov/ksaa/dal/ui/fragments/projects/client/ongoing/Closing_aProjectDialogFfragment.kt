package sa.gov.ksaa.dal.ui.fragments.projects.client.ongoing

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.OngoingProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.OngoingProjectsVM

class Closing_aProjectDialogFfragment: BaseMaterialDialogFragment(R.layout.fragment_dialog_closing_a_project_confirmation){

    companion object {
        const val tag = "Closing_aProjectDialogFfragment"
    }
    val ongoingProjectsVM: OngoingProjectsVM by viewModels()
    lateinit var ongoingProject: ProjectUnderway
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        ongoingProject = activityVM.ongoingProjectLD.value!!

        cancelBtn.setOnClickListener {
            dismiss()
        }
        cancelBtn2.setOnClickListener {
            dismiss()
        }
        okBtn.setOnClickListener {
            // projectId=79
            ongoingProjectsVM.close_aProject(mapOf("projectId" to ongoingProject.projectId.toString()))
                .observe(viewLifecycleOwner) {res ->
                    newHandleSuccessOrErrorResponse(res, {
                        activityVM.ongoingProjectLD.value!!.isClosed = true
                        val postRatingAndReview = RatingAndReview()
                        postRatingAndReview.projectId = ongoingProject.projectId
                        postRatingAndReview.clientUserId = ongoingProject!!.clientUserId
                        postRatingAndReview.freelancerUserId = ongoingProject!!.freelancerUserId

                        activityVM.postRatingAndReview.postValue(postRatingAndReview)
                        findNavController().navigate(R.id.action_closing_aProjectDialogFfragment_to_addEditReviewDialogFragment)
//                        findNavController().navigate(R.id.action_closing_aProjectDialogFfragment_to_completedProjectsClientFragment)
//                        activityVM.ongoingProjectLD.postValue(ongoingProject)
                    })
                }

        }
    }
    lateinit var cancelBtn: ImageView
    lateinit var okBtn: MaterialButton
    lateinit var cancelBtn2: MaterialButton
    override fun initViews(createdView: View) {
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        cancelBtn2 = createdView.findViewById(R.id.cancelBtn2)
        okBtn = createdView.findViewById(R.id.okBtn)
    }

}