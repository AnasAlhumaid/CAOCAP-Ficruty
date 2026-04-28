package sa.gov.ksaa.dal.ui.fragments.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.bids.AddEditQuotationFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.fragments.starter.ViewPagerFragmentDirections
import sa.gov.ksaa.dal.ui.viewModels.UserVM
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM

class UserLoginFragment : BaseFragment(R.layout.fragment_sign_in) {
    val vm: UserVM by viewModels()
    val freelancersVM: FreelancersVM by viewModels()
    var otp :String = ""

    lateinit var emailTIL: TextInputLayout
    lateinit var password_TIL: TextInputLayout
    lateinit var sign_up_TV: Button
    lateinit var login_btn: Button
//    lateinit var skip_account_btn: Button
    lateinit var resetPasswordTV: TextView
    lateinit var rememberMeCB: CheckBox
    var newUser: NewUser? = null

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        val userStr = mainActivity.getPreferences(Context.MODE_PRIVATE)
            .getString("user", null)

//        if (!userStr.isNullOrEmpty()) {
//            _user = Gson().fromJson(userStr, NewUser::class.java)
//            if (_user != null && _user!!.userId != null && _user!!.userId != 0) {
//                if(_user!!.isFreelancer() && currentFreelancer == null){
//                    // userId=5
//                    val freelancerStr = mainActivity.getPreferences(Context.MODE_PRIVATE)
//                        .getString("freelancer", null)
//                    if (!freelancerStr.isNullOrEmpty()) {
//                        currentFreelancer = Gson().fromJson(freelancerStr, NewFreelancer::class.java)
//                        if (currentFreelancer != null){
//                            activityVM.currentFreelancerMLD.postValue(currentFreelancer)
//                            activityVM.setUser(_user)
//                            navigateToProfileFragment()
//                        }
//
//                    }
//                }
//                else {
//                    activityVM.setUser(_user)
//                    navigateToProfileFragment()
//                }
//            }
//        }


        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE

        initViews(createdView)

        login_btn.setOnClickListener {

            if (isValidUser()) {

                vm.login(mapOf("email" to email, "password" to password))
                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { newUser ->

                            if (
//                                newUser.message == null &&
                                newUser.userId != null &&
                                newUser.userId != 0) {

                                _user = newUser
                                _user!!.password = password


                                vm.getOtp(mapOf("phoneNumber" to _user?.phone.toString())).observe(viewLifecycleOwner) {
                                    newHandleSuccessOrErrorResponse(it, { otp ->
                                        this.otp = otp.otp.toString()


                                        activityVM.otpData.postValue(otp)
                                        activityVM.userMLD.postValue(newUser)
                                        val action = UserLoginFragmentDirections.actionUserLoginFragmentToOtpFragment()
                                        val action2 = ViewPagerFragmentDirections.viewPagerFragment2ToOtpFragment()
                                        findNavController().safeNavigateWithArgs(action,null)
                                        findNavController().safeNavigateWithArgs(action2,null)


                                    }) { errors ->
                                        errors.errorsList?.forEach { error ->
                                            when (error?.path) {

                                            }
                                        }
                                    }
                                }


                            } else {
                                showAlertDialog(
                                    newUser.message ?: "البريد الشبكي او كلمة المرور غير مطابقة"
                                )
                            }
                        }) { errors ->
                            errors.errorsList?.forEach { error ->
                                when (error?.path) {
                                    NewUser::email.name -> emailTIL.error = error.message
                                    NewUser::password.name -> password_TIL.error = error.message
                                }
                            }
                        }
                    }
            }
        }

        sign_up_TV.setOnClickListener {


            val action1 = UserLoginFragmentDirections.actionUserLoginFragmentToSlide5Fragment()
            val action2 = ViewPagerFragmentDirections.actionViewPagerFragment2ToSignUpFragment()

            findNavController().safeNavigateWithArgs(action1,null)
            findNavController().safeNavigateWithArgs(action2,null)

        }
//        if (mainActivity.bottomNavigationView.isVisible) {
//            skip_account_btn.visibility = View.INVISIBLE
//        } else {
//            skip_account_btn.setOnClickListener {
//                mainActivity.bottomNavigationView.visibility = View.VISIBLE
//                findNavController().popBackStack()
//            }
//        }


        resetPasswordTV.setOnClickListener {

            val action1 = UserLoginFragmentDirections.actionUserLoginFragmentToResetPasswordFragment()
            val action2 = ViewPagerFragmentDirections.actionViewPagerFragment2ToForgetPasswordFragment()

            findNavController().safeNavigateWithArgs(action1,null)
            findNavController().safeNavigateWithArgs(action2,null)
        }
    }

    private fun navigateToProfileFragment() {
//        val activity =  ViewPagerFragmentDirections.actionViewPagerFragment2ToHomeFragment()// Or use 'this' if inside a Fragment

        val action = UserLoginFragmentDirections.actionUserLoginFragmentToOtpFragment()
        findNavController().safeNavigateWithArgs(action,
           null
        )
//        findNavController().safeNavigateWithArgs(activity,null)
    }

    private fun initViews(view: View) {
        emailTIL = view.findViewById(R.id.emailTIL)
        password_TIL = view.findViewById(R.id.password_TIL)
        sign_up_TV = view.findViewById(R.id.sign_up_TV)
        login_btn = view.findViewById(R.id.login_btn)
//        skip_account_btn = view.findViewById(R.id.skip_account_btn)
        resetPasswordTV = view.findViewById(R.id.resetPasswordTV)
        rememberMeCB = view.findViewById(R.id.rememberMeCB)
    }

    lateinit var email: String
    lateinit var password: String

    private fun isValidUser(): Boolean {
        if (emailTIL.editText?.text.isNullOrEmpty()) {
            emailTIL.editText?.let {
                it.error = getString(R.string.this_field_is_required)
                it.requestFocus()
            }
            return false
        }

        if (password_TIL.editText?.text.isNullOrEmpty()) {
            password_TIL.error = getString(R.string.this_field_is_required)
            password_TIL.requestFocus()
            return false
        }
        email = emailTIL.editText?.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTIL.error = getString(R.string.in_valid_email_ddress)
            emailTIL.requestFocus()
            return false
        }
        password = password_TIL.editText?.text.toString().trim()

        return true
    }
}