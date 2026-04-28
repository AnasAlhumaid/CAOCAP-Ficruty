package sa.gov.ksaa.dal.ui.fragments.technicalSupport

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.htf.drdshsdklibrary.Activities.UserDetailActivity
//import com.htf.drdshsdklibrary.Activities.UserDetailActivity
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.models.Enquiry
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.ui.adapters.RV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.ContactUsVM


class ContactUsFragment : BaseFragment(R.layout.fragment_tech_support_contact_us) {
    lateinit var name_TIL: TextInputLayout
    lateinit var emailTIL: TextInputLayout
    lateinit var enquiry_ET: EditText
    lateinit var submit_btn: Button
    lateinit var questions: TextView
    lateinit var qAndA_LL: LinearLayout
    lateinit var enquiries_LL: LinearLayout
    lateinit var enquiries: TextView
    lateinit var categorySpnr: Spinner
    lateinit var spinner_container: FrameLayout
    lateinit var liveCatBtn: FloatingActionButton
    lateinit var aboutUserChar: TextView
    lateinit var aboutUserCharMax: TextView

    val contactUsVM: ContactUsVM by viewModels()
    lateinit var enquiry: Enquiry


    //    override fun onActivityCreated() {
//        super.onActivityCreated()
//        appBarLayout.visibility = View.VISIBLE
//        bottomNavigationView.visibility = View.VISIBLE
//    }
    @SuppressLint("HardwareIds")
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.VISIBLE

        initViews(createdView)

        questions.setOnClickListener {
            qAndA_LL.visibility = View.VISIBLE
            enquiries_LL.visibility = View.GONE
            questions.isSelected = true
            enquiries.isSelected = false
//            questions.setBackgroundResource(R.drawable.bg_filled_accent_curved_5_borders)
//            questions.setTextColor(resources.getColor(R.color.white))
//            enquiries.setBackgroundResource(R.drawable.input_drwable_bg_gray_borders_curved_5)
//            enquiries.setTextColor(resources.getColor(R.color.gray_font))
        }

        enquiries.setOnClickListener {
            enquiries_LL.visibility = View.VISIBLE
            qAndA_LL.visibility = View.GONE

            questions.isSelected = false
            enquiries.isSelected = true
//            enquiries.setBackgroundResource(R.drawable.bg_filled_accent_curved_5_borders)
//            enquiries.setTextColor(resources.getColor(R.color.white))
//            questions.setBackgroundResource(R.drawable.input_drwable_bg_gray_borders_curved_5)
//            questions.setTextColor(resources.getColor(R.color.gray_font))
        }
        submit_btn.setOnClickListener {
            if (isvalid()) {
                val user = activityVM.userMLD.value
                enquiry.user_id = user?.userId
                // enquiry
                // userId=1&objectionTo=2&category=شكوى&description=وصف الشكوى&status
                contactUsVM.create_anEnquiry(
                    mapOf("userId" to (_user?.userId?:0).toString(),
                        "objectionTo" to "0",
                        "category" to enquiry.category!!,
                        "description" to enquiry.message!!,
                        "status" to ""
                    ))
                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { objectAndComplaint ->
//                            showAlertDialog(getString(R.string.your_enquiry_is_submitted_successfully))

                            showCustomAlert(getString(R.string.your_enquiry_is_submitted_successfully))
                            clearInput()

                        }) { errors: Errors? ->
//                            showAlertDialog(getString(R.string.your_enquiry_is_submitted_successfully))
                            clearInput()
                        }
                    }
            }
        }

        aboutUserCharMax.text = RV_Adapter.numberFormat.format(250)
        aboutUserChar.text = RV_Adapter.numberFormat.format(0)

        enquiry_ET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar.text = p0?.length.toString()



                val noChars = p0?.length?:0


                aboutUserChar.text = RV_Adapter.numberFormat.format(noChars)
                if (noChars >=250){
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    enquiry_ET.text?.delete(250, noChars)
                } else {
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))



                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        liveCatBtn.setOnClickListener {

//            val deviceId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
//
//            com.htf.drdshsdklibrary.Activities.UserDetailActivity.open(
//                currActivity = requireActivity(),
//                appSid = "655481af9df126ca3b986b7a.bd87be2833129dcdb7f367b2c6b1eda6e7531153",
//                locale = "ar",
//                deviceID = deviceId,
//                domain = "127.0.0.1:58465"
//            )

            val deviceId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
//            UserDetailActivity.open(
//                currActivity = requireActivity(),
//                appSid = "66b1de14202d7d6546b898e8.1761a3292142a92e88b3e61649c9f04b41e8d5e0",
//                locale = "en",
//                deviceID = deviceId,
//                domain = "localhost:8080"
//            )
//            if (_user == null){
//                findNavController().navigate(R.id.action_contactUsFragment_to_userLoginFragment)
//            } else {
//                findNavController().navigate(R.id.action_contactUsFragment_to_liveChatFragment)
//            }

        }

        // This callback is only called when MyFragment is at least started
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner,onBackPressedCallback)
    }

    private fun clearInput() {
        enquiry_ET.setText("")
    }

    override fun onStart() {
        super.onStart()
        _user?.let {
            val name = _user!!.getFullName()
            Log.i(TAG, "initViews: name = $name")
            name_TIL.editText?.setText(name.trim())
            val email = _user!!.email
            Log.i(TAG, "initViews: email = $email")
            emailTIL.editText?.setText(email?.trim())
        }
    }

    private fun isvalid(): Boolean {
        enquiry = Enquiry()
        if (name_TIL.editText?.text.isNullOrEmpty()) {
            name_TIL.error = getString(R.string.this_field_is_required)
            name_TIL.requestFocus()
            return false
        }
        enquiry.name = name_TIL.editText?.text.toString().trim()

        if (emailTIL.editText?.text.isNullOrEmpty()) {
            emailTIL.error = getString(R.string.this_field_is_required)
            emailTIL.requestFocus()
            return false
        }
        enquiry.email = emailTIL.editText?.text.toString().trim()

        if (categorySpnr.selectedItemPosition <= 0) {
            spinner_container.setBackgroundResource(R.drawable.input_bg_red_borders_curved_5_transpaent_filled)
            return false
        }
        enquiry.category = categorySpnr.selectedItem.toString()

        if (enquiry_ET.text.isNullOrEmpty()) {
            enquiry_ET.error = getString(R.string.this_field_is_required)
            enquiry_ET.requestFocus()
            return false
        }
        enquiry.message = enquiry_ET.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(enquiry.email.toString()).matches()) {
            emailTIL.error = getString(R.string.in_valid_email_ddress)
            emailTIL.requestFocus()
            return false
        }

        return true
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(createdView: View) {
        Log.i(TAG, "initViews: _user = $_user")
        name_TIL = createdView.findViewById(R.id.name_TIL)
        emailTIL = createdView.findViewById(R.id.emailTIL)
        categorySpnr = createdView.findViewById(R.id.categorySpnr)
        spinner_container = createdView.findViewById(R.id.spinner_container)

        aboutUserChar = createdView.findViewById(R.id.aboutUser_char_TV)
        aboutUserCharMax = createdView.findViewById(R.id.aboutUserMax)

        enquiry_ET = createdView.findViewById(R.id.enquiry_ET)
        submit_btn = createdView.findViewById(R.id.submit_btn)
        questions = createdView.findViewById(R.id.questions)
        questions.isSelected = true
        qAndA_LL = createdView.findViewById(R.id.qAndA_LL)
        enquiries_LL = createdView.findViewById(R.id.enquiries_LL)
        enquiries = createdView.findViewById(R.id.enquiries)
        enquiries.isSelected = false
        liveCatBtn = createdView.findViewById(R.id.liveCatBtn)

        aimsTitle_TV = createdView.findViewById(R.id.aimsTitle_TV)
        aimsTitleIV = createdView.findViewById(R.id.aimsTitleIV)
        aims_TV = createdView.findViewById(R.id.aims_TV)
        aimsTitle_TV.setOnClickListener {
            aimsTitleIV.isSelected = !aimsTitleIV.isSelected
            togleVisisbilityAndGg(aims_TV, aimsTitle_TV)
        }

        howToContactTitle_TV = createdView.findViewById(R.id.howToContactTitle_TV)
        howToContactTitleIV = createdView.findViewById(R.id.howToContactTitleIV)
        howToContact_TV = createdView.findViewById(R.id.howToContact_TV)
        howToContactTitle_TV.setOnClickListener {
            howToContactTitleIV.isSelected = !howToContactTitleIV.isSelected
            togleVisisbilityAndGg(howToContact_TV, howToContactTitle_TV)
        }

        canIhave1accoutTitle_TV = createdView.findViewById(R.id.canIhave1accoutTitle_TV)
        canIHave1AccoutTitleIV = createdView.findViewById(R.id.canIHave1AccoutTitleIV)
        canIhave1accout_TV = createdView.findViewById(R.id.canIhave1accout_TV)
        canIhave1accoutTitle_TV.setOnClickListener {
            canIHave1AccoutTitleIV.isSelected = !canIHave1AccoutTitleIV.isSelected
            togleVisisbilityAndGg(canIhave1accout_TV, canIhave1accoutTitle_TV)
        }

        gettingProgressTitle_TV = createdView.findViewById(R.id.gettingProgressTitle_TV)
        gettingProgressTitleIV = createdView.findViewById(R.id.gettingProgressTitleIV)
        gettingProgress_TV = createdView.findViewById(R.id.gettingProgress_TV)
        gettingProgressTitle_TV.setOnClickListener {
            gettingProgressTitleIV.isSelected = !gettingProgressTitleIV.isSelected
            togleVisisbilityAndGg(gettingProgress_TV, gettingProgressTitle_TV)
        }

        cancellingProjectTitle_TV = createdView.findViewById(R.id.cancellingProjectTitle_TV)
        cancellingProjectTitleIV = createdView.findViewById(R.id.cancellingProjectTitleIV)
        cancellingProject_TV = createdView.findViewById(R.id.cancellingProject_TV)
        cancellingProjectTitle_TV.setOnClickListener {
            cancellingProjectTitleIV.isSelected = !cancellingProjectTitleIV.isSelected
            togleVisisbilityAndGg(cancellingProject_TV, cancellingProjectTitle_TV)
        }

        addingServiceToProjectTitle_TV =
            createdView.findViewById(R.id.addingServiceToProjectTitle_TV)
        addingServiceToProjectTitleIV = createdView.findViewById(R.id.addingServiceToProjectTitleIV)
        addingServiceToProject_TV = createdView.findViewById(R.id.addingServiceToProject_TV)
        addingServiceToProjectTitle_TV.setOnClickListener {
            addingServiceToProjectTitleIV.isSelected = !addingServiceToProjectTitleIV.isSelected
            togleVisisbilityAndGg(addingServiceToProject_TV, addingServiceToProjectTitle_TV)
        }

        idVerificationTitle_TV = createdView.findViewById(R.id.idVerificationTitle_TV)
        idVerificationTitleIV = createdView.findViewById(R.id.idVerificationTitleIV)
        idVerification_TV = createdView.findViewById(R.id.idVerification_TV)
        idVerificationTitle_TV.setOnClickListener {
            idVerificationTitleIV.isSelected = !idVerificationTitleIV.isSelected
            togleVisisbilityAndGg(idVerification_TV, idVerificationTitle_TV)
        }

        cancellingServiceRequestTitle_TV =
            createdView.findViewById(R.id.cancellingServiceRequestTitle_TV)
        cancellingServiceRequestTitleIV =
            createdView.findViewById(R.id.cancellingServiceRequestTitleIV)
        cancellingServiceRequest_TV = createdView.findViewById(R.id.cancellingServiceRequest_TV)
        cancellingServiceRequestTitle_TV.setOnClickListener {
            cancellingServiceRequestTitleIV.isSelected = !cancellingServiceRequestTitleIV.isSelected
            togleVisisbilityAndGg(cancellingServiceRequest_TV, cancellingServiceRequestTitle_TV)
        }
    }

    lateinit var aimsTitle_TV: LinearLayout
    lateinit var aimsTitleIV: ImageView
    lateinit var aims_TV: TextView
    lateinit var howToContactTitle_TV: LinearLayout
    lateinit var howToContactTitleIV: ImageView
    lateinit var howToContact_TV: TextView
    lateinit var canIhave1accoutTitle_TV: LinearLayout
    lateinit var canIHave1AccoutTitleIV: ImageView
    lateinit var canIhave1accout_TV: TextView
    lateinit var gettingProgressTitle_TV: LinearLayout
    lateinit var gettingProgressTitleIV: ImageView
    lateinit var gettingProgress_TV: TextView
    lateinit var cancellingProjectTitle_TV: LinearLayout
    lateinit var cancellingProjectTitleIV: ImageView
    lateinit var cancellingProject_TV: TextView
    lateinit var addingServiceToProjectTitle_TV: LinearLayout
    lateinit var addingServiceToProjectTitleIV: ImageView
    lateinit var addingServiceToProject_TV: TextView
    lateinit var idVerificationTitle_TV: LinearLayout
    lateinit var idVerificationTitleIV: ImageView
    lateinit var idVerification_TV: TextView
    lateinit var cancellingServiceRequestTitle_TV: LinearLayout
    lateinit var cancellingServiceRequestTitleIV: ImageView
    lateinit var cancellingServiceRequest_TV: TextView

}