package sa.gov.ksaa.dal.ui.fragments.auth.signUp.freelancer

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Certification
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import java.util.Calendar

class FreelancerRegistration3Education : BaseFragment(R.layout.fragment_sign_up_5_freelancer_2) {

    private lateinit var certification: Certification
//    private var educationCertificate0: MyFile? = null
//    private var educationCertificate1: MyFile? = null

    val eduCert0ActivityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object: FileListener{
                override fun onFileChosen(myFile: MyFile) {
                    certification.educationCertificate0 = myFile
                    f0Name.text = myFile.name
                }

            })

        }
    }
//    val vm: CertificationsVM by viewModels()
    val authVM: FreelancersVM by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)
        certification = Certification()
        add_file_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                openFile(null, PDF_TYPE, eduCert0ActivityResultLauncher )
            }
        }
        add_certificate_btn.setOnClickListener {
            notImplemented()
        }



        next_btn.setOnClickListener{
            if (isValidInput()){
                activityVM.setUser(_user)
                activityVM.currentFreelancerMLD.postValue(currentFreelancer)
                activityVM.certificationMLD.postValue(certification)
                navigateToIdIdVerification()
                Log.w(this@FreelancerRegistration3Education.javaClass.simpleName, "onFileChosen: gender = ${_user?.gender}")

//                vm.create_aCertification(certification)
//                    .observe(viewLifecycleOwner) {
//                        handleSuccessOrErrorResponse(it, { certification ->
//                            Snackbar.make(
//                                next_btn,
//                                "تم اضافة الشهادات بنجاح",
//                                Snackbar.LENGTH_SHORT
//                            )
//                                .addCallback(object :
//                                    BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                                    override fun onDismissed(
//                                        transientBottomBar: Snackbar?,
//                                        event: Int
//                                    ) {
//                                        super.onDismissed(transientBottomBar, event)
//                                        navigateToIdIdVerification()
//                                    }
//                                }).show()
//                        }) { errors ->
//
//                        }
//                    }
            }
        }
    }

    private fun navigateToIdIdVerification() {
        findNavController().navigate(R.id.action_freelancerRegisterationFragment3_to_freelancerIdentityVerificationFragment3)
    }

    lateinit var inputStr: String
    private fun isValidInput(): Boolean {

        if(certificateTypeSpnr.selectedItemPosition == 0){
            Snackbar.make(certificateTypeSpnr, getString(R.string.this_field_is_required), Snackbar.LENGTH_SHORT).show()
            spinnerFL.setBackgroundResource(R.drawable.input_bg_red_borders_curved_5_transpaent_filled)
            return false
        }
        certification.type = certificateTypeSpnr.selectedItem.toString()

        inputStr = enrollmentDateET.text.toString()
        if(inputStr.isEmpty()){
            enrollmentDateET.error = getString(R.string.this_field_is_required)
            enrollmentDateET.requestFocus()
            return false
        }
        currentFreelancer!!.enrollmentYear = inputStr
        certification.enrollmrent_date = inputStr

        inputStr = graduationDateET.text.toString()
        if(inputStr.isEmpty()){
            graduationDateET.error = getString(R.string.this_field_is_required)
            graduationDateET.requestFocus()
            return false
        }
        currentFreelancer!!.graduationDate = inputStr
        certification.graduation_date = inputStr

        inputStr = universityNameET.text.toString()
        if(inputStr.isEmpty()){
            universityNameET.error = getString(R.string.this_field_is_required)
            universityNameET.requestFocus()
            return false
        }
        certification.institute_name = inputStr

        if (certification.educationCertificate0 == null){
            add_file_btn.error = "يجب رفع ملف الشهادة"
            add_file_btn.requestFocus()
            return false
        }

        inputStr = universityNameET.text.toString()
        if(inputStr.isEmpty()){
            universityNameET.error = getString(R.string.this_field_is_required)
            universityNameET.requestFocus()
            return false
        }
        certification.institute_name = inputStr

        certification.freelance_id = activityVM.currentFreelancerMLD.value!!.id
        currentFreelancer!!.certification = certification
        return true
    }

    lateinit var certificateTypeSpnr: Spinner
    lateinit var spinnerFL: LinearLayout
    lateinit var graduationDateET: EditText
    lateinit var enrollmentDateET: EditText
    lateinit var universityNameET: EditText
    lateinit var add_file_btn: MaterialButton
    lateinit var add_certificate_btn: MaterialButton
    lateinit var next_btn: MaterialButton
//    lateinit var graduationDate: Date
//    lateinit var enrollmentDate: Date
    lateinit var f0Name: TextView
    private fun initViews(createdView: View) {
        certificateTypeSpnr = createdView.findViewById(R.id.certificateTypeSpnr)

        spinnerFL = createdView.findViewById(R.id.spinnerFL)
        enrollmentDateET = createdView.findViewById(R.id.enrollmentDateET)
        enrollmentDateET.setOnFocusChangeListener { v, b ->
            if (b) showMaterialDatePicker(message = enrollmentDateET.hint.toString(),
                dateValidator = DateValidatorPointBackward.now(),
                onPositiveButtonClickListener = {
                    selection ->
                    calendar.setTimeInMillis(selection)
                currentFreelancer!!.enrollmentYear = calendar.get(Calendar.YEAR).toString()
                enrollmentDateET.setText(convertArabicNumbersToString(currentFreelancer!!.enrollmentYear))
            })
        }


        graduationDateET = createdView.findViewById(R.id.graduationDateET)
        graduationDateET.setOnFocusChangeListener { v, b ->
            if (b) {
                val dateValidatorList = mutableListOf<DateValidator>( DateValidatorPointBackward.now())
//                if (currentFreelancer!!.enrollmentDate != null) dateValidatorList.add(
//                    DateValidatorPointForward.from(currentFreelancer!!.enrollmentDate!!.time))

                showMaterialDatePicker(message = graduationDateET.hint.toString(),
                    dateValidator = CompositeDateValidator.allOf(dateValidatorList),
                    onPositiveButtonClickListener = { selection ->
                        calendar.timeInMillis = selection
                        currentFreelancer!!.graduationDate = calendar.get(Calendar.YEAR).toString()
                        graduationDateET.setText(convertArabicNumbersToString(currentFreelancer!!.graduationDate))
                    })
            }
        }
        universityNameET = createdView.findViewById(R.id.universityNameET)
        add_file_btn = createdView.findViewById(R.id.add_file_btn)
        add_certificate_btn = createdView.findViewById(R.id.add_certificate_btn)
        next_btn = createdView.findViewById(R.id.next_btn)
        f0Name = createdView.findViewById(R.id.f0Name)
    }
}