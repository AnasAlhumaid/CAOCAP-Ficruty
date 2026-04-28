package sa.gov.ksaa.dal.ui.fragments.auth.signUp.client

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.viewModels.UserVM
import java.util.Date

class ClientIdentityVerificationFragment :
    BaseFragment(R.layout.fragment_sign_up_6_user_identity_verification) {

    lateinit var verifyIdBtn: MaterialButton
    lateinit var previousBtn: MaterialButton
    lateinit var saveBtn: MaterialButton
    val vm: UserVM by viewModels()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)

        verifyIdBtn.setOnClickListener {
            notImplemented()
        }

        saveBtn.setOnClickListener {
            if (isValidId()) {
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
                        "dateOfBirth" to _user!!.dateOfBirth!!,
                        "nationalId" to _user!!.nationalId!!,
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
                            val action = ClientIdentityVerificationFragmentDirections.actionClientIdentityVerificationFragmentToUserLoginFragment()

                            this@ClientIdentityVerificationFragment.findNavController()
                                .safeNavigateWithArgs(action,null)

                        }) { errors ->

                        }
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
        dateOfBirthET.setOnFocusChangeListener{ view, b->
            if (b) showMaterialDatePicker(message = dateOfBirthET.hint.toString(),
                dateValidator = DateValidatorPointBackward.now(),
                onPositiveButtonClickListener = { selection ->
                    dateOfBirth = Date(selection)
                    dateOfBirthET.setText(simpleDateFormatEn.format(dateOfBirth))
                })
        }

        verifyIdBtn = createdView.findViewById(R.id.verifyIdBtn)
        previousBtn = createdView.findViewById(R.id.previousBtn)
        saveBtn = createdView.findViewById(R.id.saveBtn)
    }

    lateinit var dateOfBirth: Date
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
                            input.startsWith('2'))){
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

        _user!!.dateOfBirth = input

        return true
    }
}