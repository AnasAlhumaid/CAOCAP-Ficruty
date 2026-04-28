package sa.gov.ksaa.dal.ui.fragments.auth.signUp.freelancer

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.models.CountryItem
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.adapters.CountryCodeArrayAdapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.UserVM


class FreelancerRegistration1BasicInfo : BaseFragment(R.layout.fragment_sign_up_3_freelancer){


    lateinit var first_name_TIL: TextInputLayout
    lateinit var second_name_TIL: TextInputLayout
    lateinit var phone_number_TIL: TextInputLayout
    lateinit var countryCodeSpnr: Spinner
    lateinit var countryList: List<CountryItem>
    lateinit var countryCodeArrAdapter: ArrayAdapter<CountryItem>

    lateinit var email_TIL: TextInputLayout
    lateinit var genderRG: RadioGroup
    // lateinit var backIV: ImageView
//    lateinit var nationality_ACTV: AutoCompleteTextView

//    lateinit var vm: FreeLancerRegistrationVM
    val vm: UserVM by viewModels()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

//        vm = ViewModelProvider(this).get(FreeLancerRegistrationVM::class.java)
        first_name_TIL = createdView.findViewById(R.id.first_name_TIL)
        second_name_TIL = createdView.findViewById(R.id.second_name_TIL)
        phone_number_TIL = createdView.findViewById(R.id.phone_number_TIL)
        countryCodeSpnr = createdView.findViewById(R.id.countryCodeSpnr)

        phone_number_TIL.editText?.setArabicNumberFormatter()


        val itemType = object : TypeToken<List<CountryItem>>() {}.type
        val fileInString: String =
            requireActivity()
                .applicationContext.assets.open("countries.json")
                .bufferedReader().use { it.readText() }
        countryList = Gson().fromJson(fileInString, itemType)
        countryCodeArrAdapter = CountryCodeArrayAdapter(countryList, requireContext())
        countryCodeSpnr.adapter = countryCodeArrAdapter

        email_TIL = createdView.findViewById(R.id.email_TIL)
        genderRG = createdView.findViewById(R.id.genderRG)
        // backIV = view.findViewById(R.id.backIV)
//        nationality_ACTV = view.findViewById(R.id.nationality_ACTV)

//        backIV.setOnClickListener {
//            findNavController().popBackStack()
//        }

        _user = activityVM.userMLD.value
        _user = _user?: NewUser()
        _user!!.email?.let {
            email_TIL.editText?.setText(it)
        }

//        nationality_ACTV.setAdapter(
//            ArrayAdapter(requireContext(), android.R.layout.select_dialog_item,
//                resources.getStringArray(R.array.nationality)))
//        nationality_ACTV.threshold = 1 //will start working from first character

        createdView.findViewById<Button>(R.id.next_btn).setOnClickListener {
            if (isValidUser()){
                Log.i(TAG, "onClick: user = $_user")
                activityVM.setUser(_user)
                navigateToFreelancerRegisteration()
            }
        }

    }

    private fun navigateToFreelancerRegisteration() {
        findNavController().navigate(R.id.action_freelancerRegistrationFragment1_to_freelancerRegisterationFragment2)
    }

    lateinit var input: String
    private fun isValidUser(): Boolean {

        input = first_name_TIL.editText?.text.toString().trim()
        if (input.isEmpty()){
            first_name_TIL.error = getString(R.string.this_field_is_required)
            first_name_TIL.requestFocus()
            return false
        }
        _user!!.firstName = input

        input = second_name_TIL.editText?.text.toString().trim()
        if (input.isEmpty()){
            second_name_TIL.error = getString(R.string.this_field_is_required)
            second_name_TIL.requestFocus()
            return false
        }
        _user!!.lastName = input

        input = convertEnglishNumbersToString(phone_number_TIL.editText?.text.toString().trim())
        if (input.isEmpty() ) {
            phone_number_TIL.error = getString(R.string.this_field_is_required)
            phone_number_TIL.requestFocus()

            return false
        }

        if (!Patterns.PHONE.matcher(input).matches()) {
            phone_number_TIL.error = getString(R.string.phone_number_is_not_valid)
            phone_number_TIL.requestFocus()
            return false
        }

        if (input.startsWith('0')) {
            input = input.substring(1)
        }

        if (input.length != 9) {
            phone_number_TIL.error = getString(R.string.phone_number_is_not_valid)
            phone_number_TIL.requestFocus()
            return false
        }
        if (countryCodeSpnr.selectedItemPosition == 0 ){
            activitySnackbar.setText("يجب تحديد رمز الدولة")
                .show()
            return false
        }

        input = (countryCodeSpnr.selectedItem as CountryItem).dialCode.substring(1) + input

        _user!!.phone = input

        if (email_TIL.editText?.text.isNullOrEmpty()){
            email_TIL.error = getString(R.string.this_field_is_required)
            return false
        }
        _user!!.email = email_TIL.editText?.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(_user!!.email).matches()){
            email_TIL.error = getString(R.string.in_valid_email_ddress)
            email_TIL.requestFocus()
            return false
        }

        _user!!.gender = when(genderRG.checkedRadioButtonId){
            R.id.maleRadio -> NewUser.MALE_USER_GENDER
            R.id.femaleRadio -> NewUser.FEMALE__USER_GENDER
            else -> NewUser.MALE_USER_GENDER
        }

        _user!!.country = "السعودية"
        return true
    }

    override fun onActivityCreated() {
        super.onActivityCreated()
        appBarLayout.visibility = View.VISIBLE
    }
}