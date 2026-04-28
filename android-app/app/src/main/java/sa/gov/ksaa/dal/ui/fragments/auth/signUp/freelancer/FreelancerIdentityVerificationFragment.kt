package sa.gov.ksaa.dal.ui.fragments.auth.signUp.freelancer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.DateValidatorPointBackward
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Certification
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import java.lang.Error
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FreelancerIdentityVerificationFragment :
    BaseFragment(R.layout.fragment_sign_up_6_freelancer_identity_verification) {


    lateinit var verifyIdBtn: MaterialButton
    lateinit var previousBtn: MaterialButton
    lateinit var saveBtn: MaterialButton
    lateinit var certification: Certification
    val authVM: FreelancersVM by viewModels()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)

        certification = activityVM.certificationMLD.value!!

        verifyIdBtn.setOnClickListener {
//            notImplemented()
        }

        saveBtn.setOnClickListener {
            if (isValidId()) {
                // freelancer
                // firstName=قيس&lastName=احمد&phone=966536637215&about=عن المستقل&email=kha@gmail.com&password=123456&
                // dateOfBirth=22/11/2000&nationalId=1234567890&country=السعودية&userType=Freelancer&
                // experience=خبرة&typeOfService=ترجمة&nameOfUnivesity=ام القرى&graduationYear=2019&
                // enrollmentYear=2016&typeOfCertificate=بكالوريوس&yearOfExperience=2&
                // previousWorkfile0=file&previousWorkDesc0=desc&previousWorkfile1=file&previousWorkDesc1=Desc&
                // educationCertificate0=File&educationCertificate1=File


                val params = mutableMapOf<String, String>(
                    "firstName" to _user!!.firstName!!,
                    "lastName" to _user!!.lastName!!,
                    "gender" to _user!!.gender!!,
                    "phone" to _user!!.phone!!,
                    "about" to _user!!.about!!,
                    "email" to _user!!.email!!,
                    "password" to _user!!.password!!,
                    "dateOfBirth" to _user!!.dateOfBirth!!,
                    "nationalId" to _user!!.nationalId!!,
                    "country" to _user!!.country!!,
                    "userType" to "Freelancer",
                    "experience" to currentFreelancer!!.experience!!,
                    "typeOfService" to "\"${currentFreelancer!!.typeOfServices!!}\"",
                    "nameOfUnivesity" to certification.institute_name!!,
                    "typeOfCertificate" to certification.type!!,
                    "yearOfExperience" to currentFreelancer!!.yearOfExperience.toString(),
                    "username" to "username"
                )

//                calendar.time = certification.graduation_date!!
                params["graduationYear"] = convertEnglishNumbersToString(certification.graduation_date!!) //calendar.get(Calendar.YEAR).toString()
//                calendar.time = certification.enrollmrent_date!!
                params["enrollmentYear"] = convertEnglishNumbersToString(certification.enrollmrent_date!!) // calendar.get(Calendar.YEAR).toString()

                params["previousWorkDesc0"] = currentFreelancer!!.previousWorkDesc0 ?: ""
                params["previousWorkDesc1"] = currentFreelancer!!.previousWorkDesc1 ?: ""

                Log.w(javaClass.simpleName, "onViewCreated: params = $params")
                authVM.create_aFreelancer(
                    params,
                    currentFreelancer!!.previousWorkfile0,
                    currentFreelancer!!.previousWorkfile1,
                    certification.educationCertificate0,
                    certification.educationCertificate1
                )

                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { freelance ->
//                            _user!!.userId = freelance.userId
////                            _user!!.username = freelance.username
//                            activityVM.setUser(_user)
//                            currentFreelancer!!.createdDate = freelance.createdDate
//                            currentFreelancer!!.freelancerId = freelance.freelancerId
//                            currentFreelancer!!.freelancerLevel = freelance.freelancerLevel
//                            currentFreelancer!!.userId = freelance.userId
//                            currentFreelancer!!.userType = freelance.userType
////                            currentFreelancer!!.username = freelance.username
//                            activityVM.currentFreelancerMLD.postValue(currentFreelancer)
                            val action = FreelancerIdentityVerificationFragmentDirections.actionFreelancerIdentityVerificationFragment3ToUserLoginFragment()
                            this@FreelancerIdentityVerificationFragment
                                .findNavController()
                                .safeNavigateWithArgs(action,null)
                        })
                    }
            }
        }

        previousBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initViews(createdView: View) {

        idNumberET = createdView.findViewById(R.id.idNumberET)
        dateOfBirthET = createdView.findViewById(R.id.dateOfBirthET)
        dateOfBirthET.setOnFocusChangeListener { view, b ->
            if (b) showMaterialDatePicker(message = dateOfBirthET.hint.toString(),
                dateValidator = DateValidatorPointBackward.now(),
                onPositiveButtonClickListener = { selection ->
                    dateOfBirthET.setText(simpleDateFormatEn.format(Date(selection)))
                })
            Log.w(this@FreelancerIdentityVerificationFragment.javaClass.simpleName, "onFileChosen: educationCertificate0 = ${certification.type}")

        }

        verifyIdBtn = createdView.findViewById(R.id.verifyIdBtn)
        previousBtn = createdView.findViewById(R.id.previousBtn)
        saveBtn = createdView.findViewById(R.id.saveBtn)


    }

    lateinit var idNumberET: EditText
    lateinit var dateOfBirthET: EditText
    lateinit var input: String
    private fun isValidId(): Boolean {
        input = idNumberET.text.toString().trim()
        if (input.isEmpty()) {
            idNumberET.error = getString(R.string.this_field_is_required)
            idNumberET.requestFocus()
            return false
        }
        if (input.length != 10) {
            idNumberET.error = "رقم الهوية يجب ان يتكون من 10 ارقام"
            idNumberET.requestFocus()
            return false
        }

        if (!(input.startsWith('1') ||
                    input.startsWith('2'))
        ) {
            idNumberET.error = "رقم الهوية يجب ان يبدا بالرقم 1 او 2"
            idNumberET.requestFocus()
            return false
        }

        _user!!.nationalId = input

        input = dateOfBirthET.text.toString().trim()
        if (input.isEmpty()) {
            dateOfBirthET.error = getString(R.string.this_field_is_required)
            dateOfBirthET.requestFocus()
            return false
        }
        if (!isAtLeast18(input)){
            dateOfBirthET.error = "يجب ان يكون العمر اكبر من 18 سنة"
            dateOfBirthET.requestFocus()
            return false
        }


        _user!!.dateOfBirth = input

        return true
    }
    fun isAtLeast18(birthDateString: String): Boolean {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust format if needed
        try {
            val birthDate = formatter.parse(birthDateString) ?: return false
            val today = Date()
            val eighteenYearsInMs = 18L * 365 * 24 * 60 * 60 * 1000
            val ageDifference = today.time - birthDate.time
            return ageDifference >= eighteenYearsInMs
        } catch (e: Error) {
            e.printStackTrace()
            return false
        }
    }
}