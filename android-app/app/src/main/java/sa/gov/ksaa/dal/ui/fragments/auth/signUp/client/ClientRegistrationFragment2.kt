package sa.gov.ksaa.dal.ui.fragments.auth.signUp.client

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.fragments.projects.explore.formatLongToDateString
import sa.gov.ksaa.dal.ui.viewModels.UserVM
import java.lang.Error
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ClientRegistrationFragment2 : BaseFragment(R.layout.fragment_sign_up_3_client_2),DatePickerDialog.OnDateSetListener{


    lateinit var aboutET: EditText
    lateinit var next_btn: MaterialButton
    lateinit var prevBtn: MaterialButton
    lateinit var clientTypeRG: RadioGroup
    lateinit var aboutUserChar: TextView
    lateinit var aboutUserCharMax: TextView
    lateinit var birthDate: EditText

    var selectdDate : Long? = 0L

    val vm: UserVM by viewModels()
    val tomorrowCalendar = Calendar.getInstance(arabicLocale)
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)


        next_btn.setOnClickListener {
            if (isValidUser()){
                Log.i(TAG, "onClick: user = $_user")
                activityVM.setUser(_user)

                    // .clientRegister?firstName=محمد&lastName=أحمد&phone=966536637215&about=نبذة&email=kha01@gmail.com&
                    // password=123456&dateOfBirth=22/11/1990&nationalId=1023456789&country=السعودية&userType=client&
                    // clientType=Client_individual&gender=ذكر
                    vm.create_aClient(
                        mapOf(
                            "firstName" to _user!!.firstName!!,
                            "lastName" to _user!!.lastName!!,
                            "phone" to _user!!.phone!!,
                            "about" to _user!!.about!!,
                            "email" to _user!!.email!!,
                            "password" to _user!!.password!!,
                            "dateOfBirth" to "22/11/1991",
                            "nationalId" to "100000000",
                            "country" to _user!!.country!!,
                            "userType" to "client",
                            "clientType" to _user!!.userType!!,
                            "gender" to _user!!.gender!!,
                            "username" to "username"
                        )
                    )
                        .observe(viewLifecycleOwner) {
                            newHandleSuccessOrErrorResponse(it, { data ->
//                            _user = data
//                            activityVM.setUser(_user)
                                mainActivity.getPreferences(Context.MODE_PRIVATE)
                                    .edit()
                                    .remove("user")
                                    .apply()
                                activityVM.setUser(null)
                                Snackbar.make(
                                    requireView(),
                                    "تم إنشاء المستخدم بنجاح",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                val action = ClientRegistrationFragment2Directions.actionClientRegistrationFragment2ToUserLoginFragment()

                                this@ClientRegistrationFragment2.findNavController()
                                    .safeNavigateWithArgs(action,null)

                            }) { errors ->

                            }
                        }

            }
        }

        prevBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        birthDate.setOnClickListener{
            showDatePicker()
        }

    }

    private fun initViews(createdView: View) {
        aboutET = createdView.findViewById(R.id.aboutET)
        next_btn = createdView.findViewById(R.id.next_btn)
        prevBtn = createdView.findViewById(R.id.prevBtn)
        clientTypeRG = createdView.findViewById(R.id.clientTypeRG)
        aboutUserChar = createdView.findViewById(R.id.aboutUser_char_TV)
        aboutUserCharMax = createdView.findViewById(R.id.aboutUserMax)
        birthDate = createdView.findViewById(R.id.birthDateClient)


        aboutUserCharMax.text = numberFormat.format(250)
        aboutUserChar.text = numberFormat.format(0)

        aboutET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar.text = p0?.length.toString()

                val noChars = p0?.length?:0
                aboutUserChar.text = numberFormat.format(noChars)
                if (noChars >=250){
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    aboutET.text?.delete(250, noChars)
                } else {
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_font_gray))
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun navigateToIadentityVerification() {
        findNavController().navigate(R.id.action_clientRegistrationFragment2_to_clientIdentityVerificationFragment)
    }

    lateinit var input: String
    private fun isValidUser(): Boolean {
        input  = aboutET.text.toString().trim()
        if (input.isEmpty()){
            _user!!.about = "لا يوجد"
        }else{
        _user!!.about = input
        }


        input = birthDate.text.toString().trim()

        if (input.isEmpty()){
            birthDate.error = "يجب ادخال تاريخ الميلاد"
            birthDate.requestFocus()
        }else if (!isAtLeast18(input)){
            birthDate.error = "يجب ان يكون العمر اكبر من 18 سنة"
            birthDate.requestFocus()
            return false
        }else{
            if (selectdDate != null)
            _user!!.dateOfBirth = formatLongToDateString(selectdDate ?: 0L)
        }

        _user!!.userType = when(clientTypeRG.checkedRadioButtonId){
            R.id.orgMRB -> NewUser.ORG_CLIENT_USER_TYPE
            R.id.individualMRB -> NewUser.INDIVIDUAL_CLIENT_USER_TYPE
            else -> NewUser.INDIVIDUAL_CLIENT_USER_TYPE
        }

        _user!!.country = "السعودية"
        return true
    }

    private fun showDatePicker() {


        val popDate =  DatePickerDialog(requireContext(),this, tomorrowCalendar.get(Calendar.YEAR),
            tomorrowCalendar.get(Calendar.MONTH),
            tomorrowCalendar.get(Calendar.DAY_OF_MONTH)



        )
        val year = tomorrowCalendar.get(Calendar.YEAR)
        val month = tomorrowCalendar.get(Calendar.MONTH)
        val day = tomorrowCalendar.get(Calendar.DAY_OF_MONTH)
        popDate.datePicker.updateDate(year, month, day)
        popDate.show()

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.e("calender","$year, --- $month")

        tomorrowCalendar.set(year,month,dayOfMonth)
        onDisplay(tomorrowCalendar.timeInMillis)
    }
    private fun onDisplay(timeStrmp:Long){
        val dateFormatter = dateFormatAr
        val simpleDateFormatEn = incommingSimpleDateFormatAr
        birthDate.setText(incommingSimpleDateFormatAr.format(timeStrmp))
        selectdDate = timeStrmp




    }
    fun isAtLeast18(birthDateString: String): Boolean {
        val formatter = SimpleDateFormat("d MMM yyyy", arabicLocale) // Adjust format if needed
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