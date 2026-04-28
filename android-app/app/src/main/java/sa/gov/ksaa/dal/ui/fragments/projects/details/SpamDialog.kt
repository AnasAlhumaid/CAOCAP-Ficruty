package sa.gov.ksaa.dal.ui.fragments.projects.details

import android.accounts.AuthenticatorDescription
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.data.webservices.newDal.responses.SpamCommint
import sa.gov.ksaa.dal.ui.adapters.RV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.ContactUsVM

class SpamDialog:BaseMaterialDialogFragment(R.layout.fragment_dialog_spam) {

    companion object {
        const val tag = "SpamDialog"
    }
    val contactUsVM: ContactUsVM by viewModels()

    lateinit var postSpamCommint: SpamCommint
    lateinit var aboutUserChar: TextView
    lateinit var aboutUserCharMax: TextView

    var reportString: String = ""
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        otherComplaintLL.visibility = View.GONE
    viewUpdate()
        aboutUserCharMax.text = RV_Adapter.numberFormat.format(250)
        aboutUserChar.text = RV_Adapter.numberFormat.format(0)
        descriptionET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar.text = p0?.length.toString()



                val noChars = p0?.length?:0


                aboutUserChar.text = RV_Adapter.numberFormat.format(noChars)
                if (noChars >=250){
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    descriptionET.text?.delete(250, noChars)
                } else {
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))



                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })



        cancelBtn.setOnClickListener {
            dismiss()
        }

         postSpamCommint = activityVM.postReportCommint.value!!
         submitBtn.setOnClickListener {
    if (checkBox1.isChecked == true || checkBox2.isChecked == true || checkBox3.isChecked == true || checkBox4.isChecked == true){



        contactUsVM.create_commwent_spam(
            mapOf("commentId" to postSpamCommint.id.toString(),
                "reportReason" to reportString,
                "userId" to _user?.userId.toString(),

            ))
            .observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, { objectAndComplaint ->
                    Snackbar.make(requireView(), "تم رفع الابلاغ بنجاح", Snackbar.LENGTH_SHORT)
                        .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            override fun onDismissed(
                                transientBottomBar: Snackbar?,
                                event: Int
                            ) {
                                super.onDismissed(transientBottomBar, event)
                                dismiss()
                                showCustomAlert("تم رفع الابلاغ بنجاح")


                            }
                        })
                        .show()

                }) { errors: Errors? ->
                    Snackbar.make(requireView(), "", Snackbar.LENGTH_SHORT)
                        .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            override fun onDismissed(
                                transientBottomBar: Snackbar?,
                                event: Int
                            ) {
                                super.onDismissed(transientBottomBar, event)
                                this@SpamDialog
                                    .findNavController()
                                    .popBackStack()
                            }
                        })
                        .show()
                }
            }

    }else{
        Snackbar.make(requireView(), "لم يتم اختيار محتوى الابلاغ", Snackbar.LENGTH_SHORT).show()
    }
                 }
    }

    lateinit var submitBtn: MaterialButton
    lateinit var checkBox1: CheckBox
    lateinit var checkBox2: CheckBox
    lateinit var checkBox3: CheckBox
    lateinit var checkBox4: CheckBox
    lateinit var descriptionET: EditText
    lateinit var otherComplaintLL : LinearLayout
    lateinit var cancelBtn: ImageButton


    fun viewUpdate(){
        checkBox1.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {

                checkBox3.isChecked = false
                checkBox4.isChecked = false
                reportString = "الاساءة"
                checkBox2.isChecked = false
            }
        }
        checkBox2.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {

                checkBox3.isChecked = false
                checkBox4.isChecked = false
                reportString = "تعليق غير لائق"
                checkBox1.isChecked = false
            }
        }
        checkBox3.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {

                checkBox2.isChecked = false
                checkBox4.isChecked = false
                reportString = "إنتهاك خصوصية"
                checkBox1.isChecked = false
            }
        }
        checkBox4.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                otherComplaintLL.visibility = View.VISIBLE
                checkBox2.isChecked = false
                checkBox3.isChecked = false
                reportString = descriptionET.text.toString()
                checkBox1.isChecked = false
            }else{
                otherComplaintLL.visibility = View.GONE
            }
        }

    }

    override fun initViews(createdView: View) {
       checkBox1 = createdView.findViewById(R.id.checkbox1)
        checkBox2 = createdView.findViewById(R.id.checkbox2)
        checkBox3 = createdView.findViewById(R.id.checkbox3)
        checkBox4 = createdView.findViewById(R.id.checkbox4)
        descriptionET = createdView.findViewById(R.id.descriptionTV)
        submitBtn = createdView.findViewById(R.id.submitBtn)
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        aboutUserChar = createdView.findViewById(R.id.aboutUser_char_TV)
        aboutUserCharMax = createdView.findViewById(R.id.aboutUserMax)
        otherComplaintLL = createdView.findViewById(R.id.otherComplaintLL)

    }
}