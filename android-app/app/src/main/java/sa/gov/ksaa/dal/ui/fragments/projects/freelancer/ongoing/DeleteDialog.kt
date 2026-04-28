package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.ongoing

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.DeliverablesVM

class DeleteDialog: BaseMaterialDialogFragment(R.layout.fragment_delete_conformation_dialog){

    val vm: DeliverablesVM by viewModels()

    lateinit var filed : NewDeliverableFile

    lateinit var cancelImage: ImageView
    lateinit var cancelBtn: MaterialButton
    lateinit var submitBtn: MaterialButton
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        filed = activityVM.deliverable.value!!
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
            val params = mutableMapOf<String, String>(

                "fileId" to filed?.id.toString(),
                "fileDescription" to "deliverable.description!!",
            )
            vm.deleteDraivable(params, null)
                .observe(viewLifecycleOwner){
                    newHandleSuccessOrErrorResponse(it, {postDeliverable ->

                        dismiss()
                    })
                }

        }
    }
    override fun initViews(createdView: View) {


        cancelImage = createdView.findViewById(R.id.cancelDeleteBtn2Btn)
        submitBtn = createdView.findViewById(R.id.okDeleteBtn)
        cancelBtn = createdView.findViewById(R.id.cancelDeleteBtn2)
    }
}