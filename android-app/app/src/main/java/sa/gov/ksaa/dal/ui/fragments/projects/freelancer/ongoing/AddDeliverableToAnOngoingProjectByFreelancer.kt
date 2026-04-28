package sa.gov.ksaa.dal.ui.fragments.projects.freelancer.ongoing

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.ProjectDeliverable
import sa.gov.ksaa.dal.data.models.UploadedFile
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.DeliverablesVM
import java.util.Date

class AddDeliverableToAnOngoingProjectByFreelancer(val ongoingProjectId: Int, val onSubmitLister: OnSubmitLister)
    : BaseMaterialDialogFragment(R.layout.fragment_dialog_add_new_deliverable){

    companion object {
        const val tag = "AddDeliverableToAnOngoingProjectByFreelancer"
    }

    val vm: DeliverablesVM by viewModels()

    var deliverableFile: MyFile? =null
    var status : String = "Draft"

    val deliverableFileAtivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object : FileListener {
                override fun onFileChosen(myFile: MyFile) {
                    if (myFile.size > 15000000) {
                        fileNameTV.setTextColor(DARK_RED_COLOR_STATE_LIST)
                        fileNameTV.text = "حجم الملف تجاوز الحد المسوح به (15 م.ب)"
                        deliverableFile = null

                    } else {
                        fileNameTV.setTextColor(WHITE_COLOR_STATE_LIST)
                        fileNameTV.text = myFile.name
                        deliverableFile = myFile
                        var color = Color.BLUE
                        fileNameTV.setTextColor(color)
                    }
                }

            })

        }
    }


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        cancelBtn.setOnClickListener {
            dismiss()
        }

        addFileBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                openFile(null, BaseFragment.All_MIME_TYPES, deliverableFileAtivityResultLauncher)
            }
        }
        submitBtn.setOnClickListener {
            if (isValid()){
                // userId, projectId, uploadfile, attachmentDesc
//                fun add(queryMap: MutableMap<String, String>, uploadfile: MyFile?) :
                val params = mutableMapOf<String, String>(
                    "userId" to deliverable.userId.toString(),
                    "projectId" to deliverable.projectId.toString(),
                    "attachmentDesc" to deliverable.description!!,
                    "status" to status,
                    "fileName" to deliverableFile!!.name
                )
                vm.add(params, deliverableFile)
                    .observe(viewLifecycleOwner){
                        newHandleSuccessOrErrorResponse(it, {postDeliverable ->
                            onSubmitLister.onDeliverableSubmitted(postDeliverable)
                            dismiss()
                        })
                    }
            }
        }
        deliverable = ProjectDeliverable()

        fileNameTV.setOnClickListener {
            if (deliverableFile?.uri != null) {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, deliverableFile?.uri)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(browserIntent)
            }

        }

        if (_user!!.isClient()){
            fileStatusSpinner.visibility = View.GONE
        }

        fileStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long)
            {
                val textView = view as TextView
                textView.setTextColor(Color.WHITE) // Replace with your desired color
                when (position){
                     1 ->{
                         status = "Final"
                     }
                    2 -> {
                        status = "Draft"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    lateinit var deliverable: ProjectDeliverable
    lateinit var input: String
    private fun isValid(): Boolean {
        input = descriptionTV.text.toString().trim()
        if (input.isEmpty()){
            descriptionTV.error = getString(R.string.this_field_is_required)
            descriptionTV.requestFocus()
            return false
        }
        deliverable.description = input

        if (deliverableFile == null){
            Snackbar.make(requireView(), "الرجاء تحديد ملف", Snackbar.LENGTH_SHORT).show()
            return false
        }

        deliverable.file = UploadedFile(deliverableFile?.name, "file_type", "file_path")
        deliverable.submission_date = Date(System.currentTimeMillis())
        deliverable.working_project_id = ongoingProjectId
        deliverable.userId = _user!!.userId
        deliverable.projectId = activityVM.ongoingProjectLD.value!!.projectId
        deliverable.uploadfile = deliverableFile

        return true
    }

    lateinit var descriptionTV: TextView
    lateinit var cancelBtn: ImageView
    lateinit var submitBtn: MaterialButton
    lateinit var addFileBtn: MaterialButton
    lateinit var fileNameTV: TextView
    lateinit var fileStatusSpinner: Spinner

    override fun initViews(createdView: View) {
        descriptionTV = createdView.findViewById(R.id.descriptionTV)
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
        addFileBtn = createdView.findViewById(R.id.addFileBtn)
        fileNameTV = createdView.findViewById(R.id.fileNameTV)
        fileStatusSpinner = createdView.findViewById(R.id.statusSpnr)

    }

    interface OnSubmitLister{
        fun onDeliverableSubmitted(deliverableFile: NewDeliverableFile)
    }
}