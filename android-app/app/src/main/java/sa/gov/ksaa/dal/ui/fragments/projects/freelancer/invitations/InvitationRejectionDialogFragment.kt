package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.invitations

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.BiddingInvitation
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM

class InvitationRejectionDialogFragment : BaseMaterialDialogFragment(R.layout.fragment_dialog_invitation_rejection_confirmation){

    companion object {
        const val tag = "BidRejectionDialogFragment"
    }

    val projectsVM: ProjectsVM by viewModels()
    lateinit var invitation: BiddingInvitation

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        invitation = activityVM.projecInvitationtMLD.value!!

        cancelBtn.setOnClickListener {
//            dismiss()
            findNavController().popBackStack()
        }

        submitBtn.setOnClickListener {

//            projectsVM.declineAnInvitation(mapOf("project_id" to invitation.project_id.toString().trim(),
//            "freelance_id" to "_user!!.freelancer!!.freelance_id.toString().trim()"))
//                .observe(viewLifecycleOwner){
////                    dismiss()
//                    findNavController().popBackStack()
//                }

        }
    }
    lateinit var cancelBtn: ImageView
    lateinit var submitBtn: MaterialButton
    override fun initViews(createdView: View) {
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
    }

}