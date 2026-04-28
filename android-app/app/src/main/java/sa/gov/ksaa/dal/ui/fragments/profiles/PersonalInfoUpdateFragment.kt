package sa.gov.ksaa.dal.ui.fragments.profiles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.CountryItem
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.adapters.CountryCodeArrayAdapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment


import sa.gov.ksaa.dal.ui.viewModels.UserVM

class PersonalInfoUpdateFragment : BaseFragment(R.layout.fragment_personal_info_editing_client) {

    val vm: UserVM by viewModels()
    val MAX_FILE_SIZE = 20_000
    lateinit var generalInfoTitleLL: LinearLayout
    lateinit var generalInfoLL: LinearLayout
    lateinit var generalInfoTitleIV: ImageView

    //    lateinit var user_name_TIL: TextInputLayout
//    lateinit var editIBtn: ImageView
    lateinit var first_name_TIL: TextInputLayout
    lateinit var second_name_TIL: TextInputLayout
    lateinit var phone_number_TIL: TextInputLayout
    lateinit var countryCodeSpnr: Spinner
    lateinit var countryList: List<CountryItem>
    lateinit var countryCodeArrAdapter: CountryCodeArrayAdapter
    lateinit var email_TIL: TextInputLayout
    lateinit var genderRG: RadioGroup
    lateinit var aboutET: EditText
    lateinit var clientTypeRG: RadioGroup
    lateinit var submitBtn: MaterialButton

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE

        initViews(createdView)

        updateUI()
phone_number_TIL.editText?.setArabicNumberFormatter()

    }

    private fun updateUI() {
//        user_name_TIL.editText?.setText(_user!!.username?.trim())
        first_name_TIL.editText?.setText(_user!!.firstName?.trim())
        second_name_TIL.editText?.setText(_user!!.lastName?.trim())
        phone_number_TIL.editText?.setText(convertArabicNumbersToString(_user!!.phone?.takeLast(9) ?: _user!!.phone) )
        countryCodeSpnr.setSelection(
            countryList.indexOf(countryList.find {
                var codeLengtth = it.dialCode.length - 9
                if (codeLengtth < 0)
                    codeLengtth = 0
                _user!!.phone?.let { it1 ->
                    it.dialCode.endsWith(
                        it1.substring(
                            0,
                            codeLengtth
                        )
                    )
                } == true
            })
        )
        email_TIL.editText?.setText(_user!!.email?.trim())

        genderRG.check(
            when (_user!!.gender) {
                NewUser.MALE_USER_GENDER -> R.id.maleRadio
                NewUser.FEMALE_USER_GENDER -> R.id.femaleRadio
                else -> R.id.maleRadio
            }
        )

        aboutET.setText(_user!!.about?.trim())

        clientTypeRG.check(
            when (_user!!.userType) {
                NewUser.ORG_CLIENT_USER_TYPE -> R.id.orgMRB
                NewUser.INDIVIDUAL_CLIENT_USER_TYPE -> R.id.individualMRB
                else -> R.id.individualMRB
            }
        )

        generalInfoTitleLL.setOnClickListener {
            generalInfoTitleIV.isSelected = !generalInfoTitleIV.isSelected
            togleVisisbilityAndGg(generalInfoLL, it)
        }
        submitBtn.setOnClickListener {
            if (isValidUser()) {
                Log.w(javaClass.simpleName, "onClick: user = $_user")
                // userId=1&firstName=محمد&lastName=test&phone=966536637215&about=نبذة&email=kha@gmail.com&username=none,

                val params = mutableMapOf<String, String>(
                    "userId" to _user!!.userId.toString(),
                    "firstName" to _user!!.firstName!!,
                    "lastName" to _user!!.lastName!!,
                    "phone" to convertEnglishNumbersToString(_user!!.phone!!) ,
                    "about" to _user!!.about!!,
                    "email" to _user!!.email!!,
                    "username" to "username",
                    "gender" to _user!!.gender!!,
                    "password" to _user!!.password!!,
                )

                vm.updateClient(params, null,null)
                    .observe(viewLifecycleOwner) { res ->
                        newHandleSuccessOrErrorResponse(res, { respo ->
                            activitySnackbar
                                .setText("تم تحديث الملف الشخصي بنجاح")
                                .show()

                            this@PersonalInfoUpdateFragment
                                .findNavController()
                                .popBackStack()

                        })
                    }
            }
        }


    }

    lateinit var input: String
    private fun isValidUser(): Boolean {
//        input = user_name_TIL.editText?.text.toString().trim()
//        if (input.isEmpty()){
//            user_name_TIL.error = getString(R.string.this_field_is_required)
//            user_name_TIL.requestFocus()
//            return false
//        }
//        _user!!.username = input

        input = first_name_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()
            first_name_TIL.error = getString(R.string.this_field_is_required)
            first_name_TIL.requestFocus()
            return false
        }
        _user!!.firstName = input

        input = second_name_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()
            second_name_TIL.error = getString(R.string.this_field_is_required)
            second_name_TIL.requestFocus()
            return false
        }
        _user!!.lastName = input

        input = convertEnglishNumbersToString(phone_number_TIL.editText?.text.toString().trim())
        if (input.isEmpty()) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()
            phone_number_TIL.error = getString(R.string.this_field_is_required)
            phone_number_TIL.requestFocus()
            return false
        }

//        if (!Patterns.PHONE.matcher(input).matches() ||
//            input.length < 10 ||
//            (input.length == 10 && !input.startsWith('0')) ||
//            input.length < 12 ||
//            (input.length == 12 && !input.startsWith("966")) ||
//            input.length > 12
//        ){
//            phone_number_TIL.error = getString(R.string.phone_number_is_not_valid)
//            phone_number_TIL.requestFocus()
//            return false
//        }
//
//        if (input.length == 10)
//            input = "966${input.substring(1)}"

        if (!Patterns.PHONE.matcher(input).matches()) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()
            phone_number_TIL.error = getString(R.string.phone_number_is_not_valid)
            phone_number_TIL.requestFocus()
            return false
        }

        if (input.startsWith('0')) {
            input = input.substring(1)
        }

        if (input.length != 9) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()
            phone_number_TIL.error = getString(R.string.phone_number_is_not_valid)
            phone_number_TIL.requestFocus()
            return false
        }

        if (countryCodeSpnr.selectedItemPosition == 0) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()
            activitySnackbar.setText("يجب تحديد مفتاح الدولة")
                .show()
            return false
        }

        input = (countryCodeSpnr.selectedItem as CountryItem).dialCode.substring(1) + input
        _user!!.phone = input

        _user!!.phone = input

        input = email_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()

            email_TIL.error = getString(R.string.this_field_is_required)
            email_TIL.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()
            email_TIL.error = getString(R.string.in_valid_email_ddress)
            email_TIL.requestFocus()
            return false
        }
        _user!!.email = input

        _user!!.gender = when (genderRG.checkedRadioButtonId) {
            R.id.maleRadio -> NewUser.MALE_USER_GENDER
            R.id.femaleRadio -> NewUser.FEMALE_USER_GENDER
            else -> NewUser.MALE_USER_GENDER
        }

        input = aboutET.text.toString().trim()
        if (input.isEmpty()) {
            if(!generalInfoLL.isVisible)
                generalInfoTitleLL.performClick()

            aboutET.error = getString(R.string.this_field_is_required)
            aboutET.requestFocus()
            return false
        }
        _user!!.about = input

//        input = user_name_TIL.editText?.text.toString().trim()
//        if (input.isEmpty()){
//            user_name_TIL.error = getString(R.string.this_field_is_required)
//            user_name_TIL.requestFocus()
//            return false
//        }
//        _user!!.username = input
        return true
    }


    private fun initViews(createdView: View) {
        generalInfoTitleLL = createdView.findViewById(R.id.generalInfoTitleLL)
        generalInfoTitleIV = createdView.findViewById(R.id.generalInfoTitleIV)
        generalInfoLL = createdView.findViewById(R.id.generalInfoLL)

//        editIBtn = createdView.findViewById(R.id.editIBtn)
//        user_name_TIL = createdView.findViewById(R.id.user_name_TIL)
        first_name_TIL = createdView.findViewById(R.id.first_name_TIL)
        second_name_TIL = createdView.findViewById(R.id.second_name_TIL)
        phone_number_TIL = createdView.findViewById(R.id.phone_number_TIL)
        countryCodeSpnr = createdView.findViewById(R.id.countryCodeSpnr)
        countryCodeSpnr = createdView.findViewById(R.id.countryCodeSpnr)
        val itemType = object : TypeToken<List<CountryItem>>() {}.type
        val fileInString: String =
            requireActivity()
                .applicationContext.assets.open("countries.json")
                .bufferedReader().use { it.readText() }
        countryList = Gson().fromJson<List<CountryItem>>(fileInString, itemType)
        countryCodeArrAdapter = CountryCodeArrayAdapter(countryList, requireContext())
        countryCodeSpnr.adapter = countryCodeArrAdapter

        email_TIL = createdView.findViewById(R.id.email_TIL)
        genderRG = createdView.findViewById(R.id.genderRG)
        aboutET = createdView.findViewById(R.id.aboutET)
        clientTypeRG = createdView.findViewById(R.id.clientTypeRG)
        submitBtn = createdView.findViewById(R.id.submitBtn)
    }
}
