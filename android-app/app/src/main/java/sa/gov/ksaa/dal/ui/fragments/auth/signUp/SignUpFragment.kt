package sa.gov.ksaa.dal.ui.fragments.auth.signUp

import android.graphics.Paint
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.UserVM
import java.util.regex.Pattern

class SignUpFragment : BaseFragment(R.layout.fragment_sign_up_1), TermsBottomSheetModal.OnTermsConfirmedListener{

    val vm: UserVM by viewModels()

    lateinit var emailTIL: TextInputLayout
    lateinit var password_TIL: TextInputLayout
    lateinit var password_check_TIL: TextInputLayout
    lateinit var create_account_btn: Button
    lateinit var skip_account_btn: Button
//    lateinit var signInBtn: TextView
    lateinit var login_btn: Button
    lateinit var termsNconditionsCB: CheckBox
    lateinit var termsNconditionsTV: TextView


    override fun onActivityCreated() {
        super.onActivityCreated()
        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE
    }

    override fun onViewCreated(createsView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createsView, savedInstanceState)
        initViews(createsView)

        termsNconditionsCB.setOnCheckedChangeListener { compoundButton, b ->
            activityVM.termsAndConditionAcceptanceLD.value = b
        }

        activityVM.termsAndConditionAcceptanceLD.observe(viewLifecycleOwner){
            termsNconditionsCB.isChecked = it
        }


        login_btn.setOnClickListener {
            findNavController().navigate(R.id.action_slide5Fragment_to_userLoginFragment)
        }
        termsNconditionsTV.setOnClickListener{

            findNavController().navigate(R.id.action_signUpFragment_to_newTermsAndConditionFragment)
//            termsBottomSheetModal.show(mainActivity.supportFragmentManager, TermsBottomSheetModal.TAG)
//            showTermsAndCondions()
        }

//        termsNconditionsCB.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
//            run {
//                termsBottomSheetModal.termsNconditionsCB2.isChecked = termsNconditionsCB.isChecked
//            }
//        }

        create_account_btn.setOnClickListener {
            if (isValidUser()) {
                activityVM.setUser(_user)
                vm.isEmailAvailable(_user!!.email!!).observe(viewLifecycleOwner) {
                    newHandleSuccessOrErrorResponse(it, { emailAvailability ->
                        if (emailAvailability.message != "Email Id already exist in db...")
                            this@SignUpFragment.navigateToSelectUserTypeFragment()
                        else {
                            emailTIL.error = getString(R.string.email_is_already_registered)
                            emailTIL.requestFocus()
                        }

                    }){errors ->
                        errors?.errorsList?.forEach { error ->
                            when (error?.path) {
                                NewUser::email.name -> emailTIL.error = error.message
                            }
                        }
                    }
                }
            }
        }

        skip_account_btn.setOnClickListener {
//            findNavController().navigate(R.id.action_SignUpFragment_to_exploreFragment)
            findNavController().popBackStack()
        }
    }
    private fun navigateToSelectUserTypeFragment(){
        this@SignUpFragment
            .findNavController()
            .navigate(R.id.action_slide5Fragment_to_selectingAccountTypeFragment)
    }

    private fun initViews(createsView: View) {
        emailTIL = createsView.findViewById(R.id.emailTIL)
        password_TIL = createsView.findViewById(R.id.password_TIL)
        password_check_TIL = createsView.findViewById(R.id.password_check_TIL)
        create_account_btn = createsView.findViewById(R.id.create_account_btn)
        skip_account_btn = createsView.findViewById(R.id.skip_account_btn)
//        signInBtn = createsView.findViewById(R.id.signInBtn)
        login_btn = createsView.findViewById(R.id.login_btn)
        termsNconditionsCB = createsView.findViewById(R.id.termsNconditionsCB)

        termsNconditionsTV = createsView.findViewById(R.id.termsNconditionsTV)
        termsNconditionsTV.paintFlags = termsNconditionsTV.paintFlags or Paint.UNDERLINE_TEXT_FLAG
//        termsBottomSheetModal = TermsBottomSheetModal(this)
    }

    lateinit var email: String
    lateinit var password1: String
    lateinit var password2: String

    private fun isValidUser(): Boolean {
        if (emailTIL.editText?.text.isNullOrEmpty()) {
            emailTIL.error = getString(R.string.this_field_is_required)
            emailTIL.requestFocus()
            return false
        }

        if (password_TIL.editText?.text.isNullOrEmpty()) {
            password_TIL.error = getString(R.string.this_field_is_required)
            password_TIL.requestFocus()
            return false
        }

        if (password_check_TIL.editText?.text.isNullOrEmpty()) {
            password_check_TIL.error = getString(R.string.this_field_is_required)
            password_check_TIL.requestFocus()
            return false
        }
        email = emailTIL.editText?.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTIL.error = getString(R.string.in_valid_email_ddress)
            emailTIL.requestFocus()
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

        password2 = password_check_TIL.editText?.text.toString().trim()

        if (password1 != password2) {
            password_check_TIL.error = getString(R.string.password_mismatch)
            password_check_TIL.requestFocus()
            return false
        }

        if (!termsNconditionsCB.isChecked) {
            termsNconditionsCB.error = getString(R.string.password_mismatch)
            termsNconditionsCB.requestFocus()
            return false
        }

        _user = NewUser(email = email, password = password1);

        return true
    }

//    lateinit var termsBottomSheetModal: TermsBottomSheetModal
    override fun setConfirmed(b: Boolean) {
        termsNconditionsCB.isChecked = b
    }
    /*
    fun showTermsAndCondions() {
        termsBottomSheetModal = TermsBottomSheetModal()
        val bottomSheetBehavior = (modalBottomSheet.dialog as BottomSheetDialog).behavior
        BottomSheetBehavior.from(mainActivity.bottomSheetModal)
        .addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> Log.i(
                        sa.gov.ksaa.dal.TAG,
                        "onStateChanged: STATE_COLLAPSED"
                    )

                    BottomSheetBehavior.STATE_EXPANDED -> Log.i(
                        sa.gov.ksaa.dal.TAG,
                        "onStateChanged: STATE_EXPANDED"
                    )

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> Log.i(
                        sa.gov.ksaa.dal.TAG,
                        "onStateChanged: STATE_HALF_EXPANDED"
                    )

                    BottomSheetBehavior.STATE_HIDDEN -> Log.i(
                        sa.gov.ksaa.dal.TAG,
                        "onStateChanged: STATE_HIDDEN"
                    )

                    BottomSheetBehavior.STATE_DRAGGING -> Log.i(
                        sa.gov.ksaa.dal.TAG,
                        "onStateChanged: STATE_HIDDEN"
                    )

                    BottomSheetBehavior.STATE_SETTLING -> Log.i(
                        sa.gov.ksaa.dal.TAG,
                        "onStateChanged: STATE_SETTLING"
                    )
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset.
            }
        })

        termsBottomSheetModal.show(mainActivity.supportFragmentManager, TermsBottomSheetModal.TAG)

    }

     */
}