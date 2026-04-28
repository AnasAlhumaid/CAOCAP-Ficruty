package sa.gov.ksaa.dal.ui.fragments.technicalSupport.addEditTechnicalSupportTicket

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Enquiry
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.ui.adapters.RV_Adapter.Companion.numberFormat
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.ContactUsVM
import sa.gov.ksaa.dal.ui.viewModels.ObjectionsAndEnquiriesVM


class AddEditTechnicalSupportTicket: BaseMaterialDialogFragment(R.layout.fragment_dialog_add_edit_technical_support_ticket){

    companion object {
        const val tag = "AddEditTechnicalSupportTicket"
    }

    val vm: ObjectionsAndEnquiriesVM by viewModels()
    val contactUsVM: ContactUsVM by viewModels()
    lateinit var enquiry: Enquiry

    private fun isValid(): Boolean {
        if (issueTypeSpnr.selectedItemPosition == 0){
            Snackbar.make(issueTypeSpnr, "Please select an issue type", Snackbar.LENGTH_SHORT).show()
            spinnerFrame.setBackgroundResource(R.drawable.input_bg_red_borders_curved_5_transpaent_filled)
            spinnerFrame.requestFocus()
            return false
        }
        enquiry.category = issueTypeSpnr.selectedItem.toString()

        if (issueDescriptionTV.text.trim().isEmpty()){
            issueDescriptionTV.error = getString(R.string.this_field_is_required)
            spinnerFrame.requestFocus()
            return false
        }
        enquiry.message = issueDescriptionTV.text.toString().trim()
        enquiry.name =  _user!!.getFullName()
        enquiry.email = _user!!.email
        enquiry.user_id = _user!!.userId
        return true
    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        cancelBtn.setOnClickListener {
            dismiss()
        }


        aboutUserCharMax.text = numberFormat.format(250)
        aboutUserChar.text = numberFormat.format(0)
        issueDescriptionTV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar.text = p0?.length.toString()



                val noChars = p0?.length?:0


                aboutUserChar.text = numberFormat.format(noChars)
                if (noChars >=250){
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    issueDescriptionTV.text?.delete(250, noChars)
                } else {
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))



                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        submitBtn.setOnClickListener {
            if (isValid()){
                // enquiry
                // userId=1&objectionTo=2&category=شكوى&description=وصف الشكوى&status
                contactUsVM.create_anEnquiry(
                    mapOf("userId" to _user!!.userId.toString(),
                        "objectionTo" to "0",
                        "category" to enquiry.category!!,
                        "description" to enquiry.message!!,
                        "status" to "",
                        "email" to _user!!.email.toString()
                    ))
                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { objectAndComplaint ->
                            Snackbar.make(requireView(), "", Snackbar.LENGTH_SHORT)
                                .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        super.onDismissed(transientBottomBar, event)
                                        dismiss()
                                        showCustomAlert("تم رفع التذكرة بنجاح")


                                    }
                                })
                                .show()

                        }) { errors: Errors? ->
                            Snackbar.make(requireView(), getString(R.string.your_enquiry_is_submitted_successfully), Snackbar.LENGTH_SHORT)
                                .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        super.onDismissed(transientBottomBar, event)
                                        this@AddEditTechnicalSupportTicket
                                            .findNavController()
                                            .popBackStack()
                                    }
                                })
                                .show()
                        }
                    }

                // userId=6&objectionTo=2&category=التصنيف&description=test&status
//                vm.addEnquery(mapOf("userId" to _user!!.userId.toString(), "objectionTo" to "",
//                    "category" to enquiry.category!!, "description" to enquiry.message!!))
//                    .observe(viewLifecycleOwner) {
//                        newHandleSuccessOrErrorResponse(it, { enquiry ->
//                            Snackbar.make(issueTypeSpnr, "Your Enquiry is submitted successfully", Snackbar.LENGTH_SHORT).show()
//                        }) {errors: Errors? ->
//                            errors?.errorsList?.forEach { error ->
//                                when (error?.path) {
//                                    Enquiry::message.name -> {
//                                        issueDescriptionTV.error = error.message
//                                        issueDescriptionTV.requestFocus()
//                                    }
//                                    Enquiry::name.name, Enquiry::email.name, Enquiry::user_id.name -> {
//                                        Snackbar.make(issueTypeSpnr, "Please Log-in and Try again", Snackbar.LENGTH_SHORT).show()
//                                    }
//                                    else -> {
//                                        Snackbar.make(issueTypeSpnr, R.string.something_went_wrong_please_try_again_later, Snackbar.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }
//                        }
//                    }
            }
        }
        enquiry = Enquiry()
    }
    lateinit var issueTypeSpnr: Spinner
    lateinit var issueDescriptionTV: EditText
    lateinit var cancelBtn: ImageView
    lateinit var submitBtn: MaterialButton
    lateinit var spinnerFrame: FrameLayout
    lateinit var aboutUserChar: TextView
    lateinit var aboutUserCharMax: TextView

    override fun initViews(createdView: View) {
        issueTypeSpnr = createdView.findViewById(R.id.issueTypeSpnr)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.ts_complaint_type,
            R.layout.list_item_spinner_dark
        )
//        val adapter = ArrayAdapter<String>(this, R.layout.list_item_spinner_dark,
//             resources.getStringArray(R.array.ts_complaint_type)
//        )
        issueTypeSpnr.adapter = adapter
        issueDescriptionTV = createdView.findViewById(R.id.issueDescriptionTV)
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
        spinnerFrame = createdView.findViewById(R.id.spinnerFrame)
        aboutUserChar = createdView.findViewById(R.id.aboutUser_char_TV)
        aboutUserCharMax = createdView.findViewById(R.id.aboutUserMax)

    }

}