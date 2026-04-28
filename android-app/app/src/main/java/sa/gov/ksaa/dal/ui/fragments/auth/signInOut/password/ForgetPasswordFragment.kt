package sa.gov.ksaa.dal.ui.fragments.auth.signInOut.password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.UserVM


class ForgetPasswordFragment : BaseFragment(R.layout.fragment_password_forget) {

    val vm: UserVM by viewModels()

    lateinit var emailTIL : TextInputLayout
    lateinit var password_reset_btn : MaterialButton
    lateinit var backBtn : MaterialButton
    lateinit var emailCard : LinearLayout
    lateinit var resetCodeCard : LinearLayout
    lateinit var validateCodeBtn : MaterialButton
    lateinit var back2Btn : MaterialButton
    lateinit var d1ET : EditText
    lateinit var d2ET : EditText
    lateinit var d3ET : EditText
    lateinit var d4ET : EditText
    lateinit var d5ET : EditText
    lateinit var d6ET : EditText
    var resetCode: Int = 0
    lateinit var input: String
    lateinit var user: NewUser

    private fun isValidResetCode(): Boolean {
        if (d1ET.text.isNullOrEmpty()){
            d1ET.error = getString(R.string.this_field_is_required)
            d1ET.requestFocus()
            return false
        }
        input = d1ET.text.toString().trim()
        if (d2ET.text.isNullOrEmpty()){
            d2ET.error = getString(R.string.this_field_is_required)
            d2ET.requestFocus()
            return false
        }
        input += d2ET.text.toString().trim()
        if (d3ET.text.isNullOrEmpty()){
            d3ET.error = getString(R.string.this_field_is_required)
            d3ET.requestFocus()
            return false
        }
        input += d3ET.text.toString().trim()
        if (d4ET.text.isNullOrEmpty()){
            d4ET.error = getString(R.string.this_field_is_required)
            d4ET.requestFocus()
            return false
        }
        input += d4ET.text.toString().trim()
        if (d5ET.text.isNullOrEmpty()){
            d5ET.error = getString(R.string.this_field_is_required)
            d5ET.requestFocus()
            return false
        }
        input += d5ET.text.toString().trim()
        if (d6ET.text.isNullOrEmpty()){
            d6ET.error = getString(R.string.this_field_is_required)
            d6ET.requestFocus()
            return false
        }
        input += d6ET.text.toString().trim()
        try {
            resetCode = input.toInt()
        } catch (e: Exception){
            return false
        }

        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        password_reset_btn.setOnClickListener{
            if (isValidEmail()){
                vm.requestPasswordReset(mapOf("email" to email)).
                observe(viewLifecycleOwner) {
                    newHandleSuccessOrErrorResponse(it, { data ->
                        if (data.email == null) {
                            emailTIL.error = "هذا البريد الالكتروني غير مسجل"
                            emailTIL.requestFocus()
                        } else {
                            activitySnackbar.setText("تم ارسال رابط اعادة الضبط الى بريدك الشبكي ($email).")
                                .show()
                            this@ForgetPasswordFragment
                                .findNavController()
                                .popBackStack()

//                            vm.requestPasswordReset(user)
//                                .observe(viewLifecycleOwner) {
//                                    newHandleSuccessOrErrorResponse(it, { data ->
//                                        if (data == null) message = getString(R.string.something_went_wrong_please_try_again_later)
//                                        else message = "An Email is sent to the email ($email) containing the reset Code"
//                                        Snackbar.make(password_reset_btn, message, Snackbar.LENGTH_LONG)
//                                            .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>(){
//                                                override fun onDismissed(
//                                                    transientBottomBar: Snackbar?,
//                                                    event: Int
//                                                ) {
//                                                    super.onDismissed(transientBottomBar, event)
////                                                    activityVM.setUser(user)
////                                                    findNavController().navigate(R.id.action_forgetPasswordFragment_to_resetPasswordFragment)
//                                                    emailCard.visibility = View.GONE
//                                                    resetCodeCard.visibility = View.VISIBLE
//                                                    sb = StringBuilder()
//                                                }
//                                            })
//                                            .show()
//                                    }) { errors ->
//                                        errors.errorsList?.forEach { error ->
//                                            when (error?.path) {
//                                                User::email.name -> {
//                                                    emailTIL.error = error.message
//                                                }
//                                                else ->
//                                                    showAlertDialog(errors?.message!!)
//                                            }
//                                        }
//
//                                    }
//                                }

                        }
                    }){ errors ->

                    }
                }
            }
        }

        validateCodeBtn.setOnClickListener {
            if (isValidResetCode()){
//                user.pwd_reset_code = resetCode
//                Log.i(TAG, "onViewCreated: user = $user")
//                vm.isResetCodeValid(user)
//                    .observe(viewLifecycleOwner) {
//                        handleSuccessOrErrorResponse(it, { res ->
//
//                            if (res) {
//                                user.pwd_reset_code = resetCode
//                                activityVM.setUser(user)
//                                navigateToResetPassword()
//
//                            } else {
//                                showAlertDialog("the Password Rest Code is not valid")
//                            }
//                        }) { errors ->
//                            Log.e(TAG, "onViewCreated: errors = $errors")
//                            errors.errorsList?.forEach { error ->
//                                when (error?.path) {
//                                    User::pwd_reset_code.name -> {
//                                        d1ET.error = error.message
//                                        d1ET.requestFocus()
//                                    }
//                                    else ->
//                                        showAlertDialog(errors?.message!!)
//                                }
//                            }
//                        }
//                    }
            }

        }

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        back2Btn.setOnClickListener {
            resetCodeCard.visibility = View.GONE
            emailCard.visibility = View.VISIBLE
        }
    }

    private fun navigateToResetPassword() {
        findNavController()
            .navigate(R.id.action_forgetPasswordFragment_to_resetPasswordFragment)
    }
    private fun initViews(createdView: View) {
        emailTIL = createdView.findViewById(R.id.emailTIL)
        password_reset_btn = createdView.findViewById(R.id.password_reset_btn)
        backBtn = createdView.findViewById(R.id.backBtn)
        emailCard = createdView.findViewById(R.id.emailCard)
        resetCodeCard = createdView.findViewById(R.id.resetCodeCard)
        validateCodeBtn = createdView.findViewById(R.id.validateCodeBtn)
        back2Btn = createdView.findViewById(R.id.back2Btn)

        d1ET = createdView.findViewById(R.id.d1ET)
        d2ET = createdView.findViewById(R.id.d2ET)
        addTextChangedListener(d1ET, d2ET)
        d3ET = createdView.findViewById(R.id.d3ET)
        addTextChangedListener(d2ET, d3ET)
        d4ET = createdView.findViewById(R.id.d4ET)
        addTextChangedListener(d3ET, d4ET)
        d5ET = createdView.findViewById(R.id.d5ET)
        addTextChangedListener(d4ET, d5ET)
        d6ET = createdView.findViewById(R.id.d6ET)
        addTextChangedListener(d5ET, d6ET)
        addTextChangedListener(d6ET, null)
    }

    lateinit var sb: StringBuilder
    private fun addTextChangedListener(editText1: EditText, editText2: EditText?){

        editText1.onFocusChangeListener =
            View.OnFocusChangeListener { view: View?, b: Boolean -> if (b) editText1.selectAll() }

        editText1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if ((sb.isEmpty()) and (editText1.length() === 1)) {
                    sb.append(s)
                    editText1.clearFocus()
                    editText2?.requestFocus()
                    editText2?.isCursorVisible = true
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                if (sb.length == 1) {
                    sb.deleteCharAt(0)
                }
            }
            override fun afterTextChanged(s: Editable) {
                if (sb.isEmpty()) {
                    editText1.requestFocus()
                }
            }
        })
    }
    lateinit var email: String

    private fun isValidEmail(): Boolean {
        if (emailTIL.editText?.text.isNullOrEmpty()){
            emailTIL.error = getString(R.string.this_field_is_required)
            emailTIL.requestFocus()
            return false
        }
        email = emailTIL.editText?.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTIL.error = getString(R.string.in_valid_email_ddress)
            emailTIL.requestFocus()
            return false
        }
        return true
    }
}