package sa.gov.ksaa.dal.ui.fragments.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.OtpModel
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.bids.AddEditQuotationFragment
import sa.gov.ksaa.dal.ui.fragments.client.ClientProfileFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs


import sa.gov.ksaa.dal.ui.viewModels.UserVM

class OtpFragment: BaseFragment(R.layout.opt_fragment) {
    private lateinit var otpData: OtpModel
    private lateinit var userData: NewUser
    lateinit var otpCode: EditText
    lateinit var sign_up_TV: Button
    lateinit var phoneNumberHidden: TextView
    val vm: UserVM by viewModels()

    companion object {
        const val userID = "userId"

    }
    var userID: Int? = 0
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
//        vm.getOtp(mapOf("phoneNumber" to email))
        this@OtpFragment.otpData = activityVM.otpData.value!!
        this@OtpFragment.userData = activityVM.userMLD.value!!


        initViews(createdView)
        update()
        otpCode.setArabicNumberFormatter()
        userID = arguments?.getInt(OtpFragment.userID, 0)
        sign_up_TV.setOnClickListener {
//            if (convertEnglishNumbersToString(otpCode.text.toString())  == otpData.otp.toString()) {

                if (userData.isFreelancer()) {
                    // userId=5
                    freelancerVM.getFreelancersByUserId(mapOf("userId" to userData.userId.toString()))
                        .observe(viewLifecycleOwner) {
                            newHandleSuccessOrErrorResponse(it, { newFreelancer ->
                                if (newFreelancer.isNotEmpty()) {




//                                                    if (rememberMeCB.isChecked) {
//
//                                                    }
                                    currentFreelancer = newFreelancer[0]
                                    activityVM.currentFreelancerMLD.postValue(
                                        currentFreelancer
                                    )
                                    activityVM.setUser(userData)

                                            mainActivity.getPreferences(Context.MODE_PRIVATE)

                                                .edit()
                                                .putString(
                                                    "user",
                                                    Gson().toJson(
                                                        _user,
                                                        NewUser::class.java
                                                    )
                                                )
                                                .putString(
                                                    "freelancer",
                                                    Gson().toJson(
                                                        currentFreelancer,
                                                        NewFreelancer::class.java

                                                    )
                                                )

                                                .apply()
                                    navigateToProfile()







                                } else {
                                    Log.e(
                                        TAG,
                                        "onViewCreated: currentFreelancer = null"
                                    )
                                }

                            })
                        }
                } else {


                    mainActivity.getPreferences(Context.MODE_PRIVATE)
                        .edit()
                        .putString(
                            "user",
                            Gson().toJson(
                                _user,
                                NewUser::class.java
                            )
                        )
                        .apply()
                    navigateToProfile()
                }
//            }else{
//                Snackbar.make(
//                    requireView(),
//                    "تم ادخال رمز التحقق بشكل خاطئ",
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            }
        }

    }

    private fun initViews(createdView: View) {
        otpCode = createdView.findViewById(R.id.otpCheckTextE)
        sign_up_TV = createdView.findViewById(R.id.checkOtpBtn)
        phoneNumberHidden = createdView.findViewById(R.id.textView182)

    }

    fun update(){
        val mobileNumber = otpData.phoneNumber.toString()

// Check phone number length with an assertion (replacing fatalError for debugging)
        assert(mobileNumber.length > 5) { "Mobile number is not complete: $mobileNumber" }

// Extract prefixes and suffixes using ranges/indices
        val intLetters = mobileNumber.subSequence(0..2) // First 3 characters
        val endLetters = mobileNumber.subSequence(mobileNumber.lastIndex - 1..mobileNumber.lastIndex) // Last 2 characters

// Create the masked number using StringBuilder
        val stars = StringBuilder().apply {
            repeat(mobileNumber.length - 5) { append("*") }
        }.toString()

        val result = "$endLetters$stars$intLetters"
        phoneNumberHidden.text = convertArabicNumbersToString(result)
    }



    fun navigateToProfile() {
        val action = OtpFragmentDirections.actionOtpFragmentToUserProfileFragment()
        findNavController().safeNavigateWithArgs(action,null)
    }
}