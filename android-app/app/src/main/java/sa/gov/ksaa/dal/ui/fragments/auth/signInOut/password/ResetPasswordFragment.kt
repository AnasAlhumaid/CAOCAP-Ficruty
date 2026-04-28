package sa.gov.ksaa.dal.ui.fragments.auth.signInOut.password

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.UserVM
import java.util.regex.Pattern

class ResetPasswordFragment : BaseFragment(R.layout.fragment_reset_password) {

    val vm: UserVM by viewModels()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)

        passwordResetBtn.setOnClickListener {
            if (validateInput()){
//                vm.resetPassword(_user!!)
//                    .observe(viewLifecycleOwner) {
//                        handleSuccessOrErrorResponse(it, { user ->
//                            if (user == null){
//                                Snackbar.make(createdView, R.string.something_went_wrong_please_try_again_later, Snackbar.LENGTH_SHORT)
//                                    .show()
//                            } else {
//                                Snackbar.make(createdView, "Password is updated successfully, Please login again.", Snackbar.LENGTH_SHORT)
//                                    .addCallback(object :
//                                        BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                                        override fun onDismissed(
//                                            transientBottomBar: Snackbar?,
//                                            event: Int
//                                        ) {
//                                            super.onDismissed(transientBottomBar, event)
//                                            findNavController().navigate(R.id.action_resetPasswordFragment_to_userLoginFragment2)
//                                        }
//
//                                    })
//                                    .show()
//                            }
//
//                        }) { errors ->
//                            errors?.errorsList?.forEach { error ->
//                                when (error?.path) {
//                                    User::password.name -> password_TIL.error = error.message
//                                }
//                            }
//                        }
//                    }
            }

        }
    }

    lateinit var password1: String
    lateinit var password2: String
    private fun validateInput(): Boolean {
        if (password_TIL.editText?.text.isNullOrEmpty()) {
            password_TIL.error = getString(R.string.this_field_is_required)
            password_TIL.requestFocus()
            return false
        }

        if (confirmPassword_TIL.editText?.text.isNullOrEmpty()) {
            confirmPassword_TIL.error = getString(R.string.this_field_is_required)
            confirmPassword_TIL.requestFocus()
            return false
        }
        password1 = password_TIL.editText?.text.toString().trim()
        if (password1.length < 8) {
            password_TIL.error = getString(R.string.the_password_must_consist_of_8_characters)
            password_TIL.requestFocus()
            return false
        }

        val specailCharPatten: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePatten: Pattern = Pattern.compile("[A-Z ]")
        val lowerCasePatten: Pattern = Pattern.compile("[a-z ]")
        val digitCasePatten: Pattern = Pattern.compile("[0-9 ]")

        if (!UpperCasePatten.matcher(password1).find()) {
            password_TIL.error = getString(R.string.password_must_have_at_least_one_uppercase_character)
            password_TIL.requestFocus()
            return false
        }

        if (!lowerCasePatten.matcher(password1).find()) {
            password_TIL.error = getString(R.string.password_must_have_at_least_one_uppercase_character)
            password_TIL.requestFocus()
            return false
        }

        if (!digitCasePatten.matcher(password1).find()) {
            password_TIL.error = getString(R.string.password_must_have_at_least_one_digit_character)
            password_TIL.requestFocus()
            return false
        }

        if (!specailCharPatten.matcher(password1).find()) {
            password_TIL.error = getString(R.string.password_must_have_at_least_one_special_character)
            password_TIL.requestFocus()
            return false
        }

        password2 = confirmPassword_TIL.editText?.text.toString().trim()

        if (password1 != password2) {
            confirmPassword_TIL.error = getString(R.string.password_mismatch)
            confirmPassword_TIL.requestFocus()
            return false
        }
        _user?.password = password1

        return true
    }

    lateinit var password_TIL: TextInputLayout
    lateinit var confirmPassword_TIL: TextInputLayout
    lateinit var passwordResetBtn: Button
    private fun initViews(createdView: View) {
        password_TIL = createdView.findViewById(R.id.password_TIL)
        confirmPassword_TIL = createdView.findViewById(R.id.confirmPassword_TIL)
        passwordResetBtn = createdView.findViewById(R.id.passwordResetBtn)
    }
}