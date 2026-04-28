package sa.gov.ksaa.dal.ui.fragments.auth.signUp.client

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
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

class ClientRegistrationFragment1 : BaseFragment(R.layout.fragment_sign_up_3_client_1) {

    lateinit var first_name_TIL: TextInputLayout
    lateinit var second_name_TIL: TextInputLayout
    lateinit var phone_number_TIL: TextInputLayout
    lateinit var countryCodeSpnr: Spinner
    lateinit var countryList: List<CountryItem>
    lateinit var countryCodeArrAdapter: ArrayAdapter<CountryItem>

    lateinit var email_TIL: TextInputLayout
    lateinit var genderRG: RadioGroup
    lateinit var next_btn: MaterialButton

    val vm: UserVM by viewModels()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)


        updateUI()
        phone_number_TIL.editText?.setArabicNumberFormatter()

        next_btn.setOnClickListener {
            if (isValidUser()) {
                Log.i(TAG, "onClick: user = $_user")
                activityVM.setUser(_user)
                findNavController().navigate(R.id.action_clientRegistrationFragment_to_clientRegistrationFragment2)
            }
        }

    }

    private fun updateUI() {
        first_name_TIL.editText?.setText(_user!!.firstName)
        second_name_TIL.editText?.setText(_user!!.lastName)
        phone_number_TIL.editText?.setText(convertArabicNumbersToString(_user!!.phone ?: "") )

        countryCodeSpnr.setSelection(
            countryList.indexOf(countryList.find {
                _user!!.phone?.let { it1 -> it.dialCode.endsWith("966") } == true
            })
        )

        email_TIL.editText?.setText(_user!!.email)
    }

    private fun initViews(createdView: View) {
        first_name_TIL = createdView.findViewById(R.id.first_name_TIL)
        second_name_TIL = createdView.findViewById(R.id.second_name_TIL)
        phone_number_TIL = createdView.findViewById(R.id.phone_number_TIL)
        countryCodeSpnr = createdView.findViewById(R.id.countryCodeSpnr)
        val itemType = object : TypeToken<List<CountryItem>>() {}.type
        val fileInString: String =
            requireActivity()
                .applicationContext.assets.open("countries.json")
                .bufferedReader().use { it.readText() }
        countryList = Gson().fromJson(fileInString, itemType)
        countryCodeArrAdapter = CountryCodeArrayAdapter(countryList,requireContext())
        countryCodeSpnr.adapter = countryCodeArrAdapter

        email_TIL = createdView.findViewById(R.id.email_TIL)
        genderRG = createdView.findViewById(R.id.genderRG)
        next_btn = createdView.findViewById(R.id.next_btn)
    }

    lateinit var input: String
    private fun isValidUser(): Boolean {

        input = first_name_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            first_name_TIL.error = getString(R.string.this_field_is_required)
            first_name_TIL.requestFocus()
            return false
        }
        _user!!.firstName = input

        input = second_name_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            second_name_TIL.error = getString(R.string.this_field_is_required)
            second_name_TIL.requestFocus()
            return false
        }
        _user!!.lastName = input

        input = convertEnglishNumbersToString(phone_number_TIL.editText?.text.toString().trim())

        if (input.isEmpty()) {
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

        input = email_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            email_TIL.error = getString(R.string.this_field_is_required)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            email_TIL.error = getString(R.string.in_valid_email_ddress)
            email_TIL.requestFocus()
            return false
        }
        _user!!.email = input

        _user!!.gender = when(genderRG.checkedRadioButtonId){
            R.id.maleRadio -> NewUser.MALE_USER_GENDER
            R.id.femaleRadio -> NewUser.FEMALE__USER_GENDER
            else -> NewUser.MALE_USER_GENDER
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.appBarLayout.visibility = View.VISIBLE
//        mainActivity.toolbar.title = null
    }
}