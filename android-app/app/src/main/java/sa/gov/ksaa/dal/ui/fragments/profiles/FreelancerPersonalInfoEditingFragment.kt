package sa.gov.ksaa.dal.ui.fragments.profiles

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.CountryItem
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FreelancerFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.MessageResponse
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.adapters.CountryCodeArrayAdapter
import sa.gov.ksaa.dal.ui.adapters.DomainsListAdapter
import sa.gov.ksaa.dal.ui.adapters.DomainsProject_Adapter
import sa.gov.ksaa.dal.ui.adapters.ExperienceFilesRvAdapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.auth.signUp.freelancer.AddEditExperience

import sa.gov.ksaa.dal.ui.viewModels.ServicesVM
import sa.gov.ksaa.dal.ui.viewModels.UserVM
import java.io.File
import java.util.Calendar

class FreelancerPersonalInfoEditingFragment :
    BaseFragment(R.layout.fragment_personal_info_editing_freelancer),
    ExperienceFilesRvAdapter.OnClickListener,
    AddEditExperience.OnSubmitLister
    {

    val vm: UserVM by viewModels()
    val serviceVM: ServicesVM by viewModels()
    //    val freelancersVM: FreelancersVM by viewModels()
    val MAX_FILE_SIZE = 2_000_000
    lateinit var generalInfoTV: TextView
    lateinit var generalInfoTitleLL: LinearLayout
    lateinit var generalInfoTitleIV: ImageView
    lateinit var generalInfoLL: LinearLayout
    lateinit var domainOfExpertiseTV: TextView
    lateinit var domainOfExpertiseTtlLL: LinearLayout
    lateinit var domainOfExpertiseTtlIV: ImageView
    lateinit var domainOfExpertiseLL: LinearLayout
    lateinit var educationTV: TextView
    lateinit var educationTtlLL: LinearLayout
    lateinit var educationTtlIV: ImageView
    lateinit var educationLL: LinearLayout



    //    lateinit var user_name_TIL: TextInputLayout
    lateinit var first_name_TIL: TextInputLayout
    lateinit var second_name_TIL: TextInputLayout
    lateinit var phone_number_TIL: TextInputLayout
    lateinit var countryCodeSpnr: Spinner
    lateinit var countryCodeArrAdapter: CountryCodeArrayAdapter
    lateinit var email_TIL: TextInputLayout
    lateinit var genderRG: RadioGroup

    //    lateinit var photoFileNameTV: TextView
//    lateinit var photoFileBtn: MaterialButton
    lateinit var userIV: ImageView


    lateinit var typeOfServicesAdapter: DomainsProject_Adapter
    lateinit var specialityRV: RecyclerView
    lateinit var domains_spinner: Spinner
    lateinit var services_TIL: TextInputLayout
    lateinit var services_MACTV: MaterialAutoCompleteTextView

    lateinit var experiences_TIL: TextInputLayout
    lateinit var exp_years_TIL: TextInputLayout
    lateinit var experienceAdapter: ExperienceFilesRvAdapter
    lateinit var experienceRV: RecyclerView
    lateinit var addExperienceBtn: MaterialButton
    lateinit var aboutUserChar: TextView
    lateinit var aboutUserCharMax: TextView

    lateinit var about_TIL: TextInputLayout
//    lateinit var experience_1_desc_IET: EditText
//    lateinit var addWorkExperienceFile1Btn: Button
//    lateinit var expCertFile1NameTV: MaterialTextView
//    lateinit var experience_2_desc_IET: EditText
//    lateinit var addWorkExperienceFile2Btn: Button
//    lateinit var expCertFile2NameTV: MaterialTextView

    lateinit var certificateTypeSpnr: Spinner
    lateinit var spinnerLL: LinearLayout

    lateinit var enrollmentDateET: EditText

    lateinit var graduationDateET: EditText
    lateinit var universityNameET: EditText
    lateinit var addCertBtn: MaterialButton
    lateinit var certFileTV: TextView
    lateinit var saveBtn: MaterialButton
    lateinit var countryList: List<CountryItem>
    lateinit var serviceList: List<ServicesModel>
    val domains = mutableListOf<String>()
        val selectedDomains = mutableListOf<String?>()

    val requestObserver = { newResource: NewResource<List<ServicesModel>> ->
        newHandleSuccessOrErrorResponse(newResource,
            onSuccess = { services ->
                val servicesListdd: MutableList<ServicesModel> = mutableListOf()
                servicesListdd.add(0, ServicesModel(0,"","l","المجال"))
                servicesListdd.addAll(services)
                serviceList = services




                val arrayAdapter = ArrayAdapter(requireContext(),R.layout.fragment_custom_spinner,R.id.AutoCompleteTextView,servicesListdd.map { it.name })




                domains_spinner.adapter = arrayAdapter


            }){

        }
    }



    val onClickListener = View.OnClickListener {
        if (isValidClient() && isValidFreelancer()) {
            val imagedata =  ByteArray(0)

            if (domains.isNullOrEmpty()){
                domains.add(currentFreelancer!!.listOfServices!!.firstOrNull()?.typeOfServices!!)
            }




            val multipartFormData = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("profileImage", "", RequestBody.create("image/jpeg".toMediaTypeOrNull(), imagedata))
                .build()



            val params = mutableMapOf<String, String>(
                "userId" to _user!!.userId.toString(),
                "firstName" to _user!!.firstName!!,
                "lastName" to _user!!.lastName!!,
                "phone" to convertEnglishNumbersToString(_user!!.phone!!),
                "about" to currentFreelancer!!.about!!,
                "email" to _user!!.email!!,
                "username" to "username",
//                "username" to _user!!.username!!,

                "skills" to "\"" + domains.joinToString(",") + "\""   ,
                "experience" to currentFreelancer!!.experience!!,

                "gender" to _user!!.gender!!,
                "yearOfExperience" to currentFreelancer!!.yearOfExperience.toString(),
                "typeOfCertificate" to currentFreelancer!!.typeOfCertificate!!,
                "graduationYear" to convertEnglishNumbersToString(currentFreelancer!!.graduationYear!!),
                "enrollmentYear" to convertEnglishNumbersToString(currentFreelancer!!.enrollmentYear!!),
                "nameOfUnivesity" to currentFreelancer!!.universityName!!,
                "password" to _user!!.password!!,


            )


            Log.w(javaClass.simpleName, "onViewCreated: params = $params")
            // userId=7, firstName=خالد, lastName=احمد, phone=966536637215, about=نبذة, email=f.test01@gmail.com,
            // username=none, skills=ترجمة, experience=خبرة, profileImage=
            freelancerVM.update_aFreelancer(params, null,null)
                .observe(viewLifecycleOwner) { res ->
                    newHandleSuccessOrErrorResponse(res, { msgResponse ->
                        if (msgResponse.message == null) {
                            activitySnackbar.setText("تم تعديل البيانات بنجاح")
                                .show()
                            activityVM.setUser(_user)
//                            currentFreelancer!!.photoUri = photoFile?.uri

                            currentFreelancer!!.freelancerId = res.data?.freelancerId
                            currentFreelancer!!.freelancerLevel = res.data?.freelancerLevel
                            currentFreelancer!!.userId = res.data?.userId
                            currentFreelancer!!.userType = res.data?.userType
                            currentFreelancer!!.experience = res.data?.experince
                            currentFreelancer!!.about = res.data?.about





                            activityVM.currentFreelancerMLD.postValue(currentFreelancer)
                            findNavController().popBackStack()
                        }

                    })

                }
        }
    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE

        initViews(createdView)
        phone_number_TIL.editText?.setArabicNumberFormatter()
        graduationDateET
        enrollmentDateET
        freelancerVM.getFreelancersByUserId(mapOf("userId" to _user!!.userId.toString()))
            .observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, { freelancers ->
                    if (freelancers.isNotEmpty()) {
                        currentFreelancer = freelancers[0]
                        activityVM.currentFreelancerMLD.postValue(currentFreelancer)
                    }
                    updateUI()
                })
            }

        aboutUserCharMax.text = numberFormat.format(250)
        aboutUserChar.text = numberFormat.format(0)
        about_TIL.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar.text = p0?.length.toString()

                val noChars = p0?.length?:0
                aboutUserChar.text = numberFormat.format(noChars)
                if (noChars >=250){
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    about_TIL.editText?.text?.delete(250, noChars)
                } else {
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_font_gray))
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    private fun initViews(createdView: View) {
        generalInfoTV = createdView.findViewById(R.id.generalInfoTV)
        generalInfoTitleLL = createdView.findViewById(R.id.generalInfoTitleLL)
        generalInfoTitleIV = createdView.findViewById(R.id.generalInfoTitleIV)
        generalInfoLL = createdView.findViewById(R.id.generalInfoLL)
        domainOfExpertiseTV = createdView.findViewById(R.id.domainOfExpertiseTV)
        domainOfExpertiseTtlLL = createdView.findViewById(R.id.domainOfExpertiseTtlLL)
        domainOfExpertiseTtlIV = createdView.findViewById(R.id.domainOfExpertiseTtlIV)
        domainOfExpertiseLL = createdView.findViewById(R.id.domainOfExpertiseLL)
        educationTV = createdView.findViewById(R.id.educationTV)
        educationTtlLL = createdView.findViewById(R.id.educationTtlLL)
        educationTtlIV = createdView.findViewById(R.id.educationTtlIV)
        educationLL = createdView.findViewById(R.id.educationLL)



//        user_name_TIL = createdView.findViewById(R.id.user_name_TIL)
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
        countryCodeArrAdapter = CountryCodeArrayAdapter(countryList, requireContext())
        countryCodeSpnr.adapter = countryCodeArrAdapter

        email_TIL = createdView.findViewById(R.id.email_TIL)
        genderRG = createdView.findViewById(R.id.genderRG)
//        photoFileNameTV = createdView.findViewById(R.id.photoFileNameTV)
//        photoFileBtn = createdView.findViewById(R.id.photoFileBtn)
        userIV = createdView.findViewById(R.id.userIV)


        about_TIL = createdView.findViewById(R.id.about_TIL)
        experiences_TIL = createdView.findViewById(R.id.experiences_TIL)
        exp_years_TIL = createdView.findViewById(R.id.exp_years_TIL)
       this.domains_spinner = createdView.findViewById(R.id.domains_spinner)

//        services_TIL = createdView.findViewById(R.id.services_TIL)
        services_MACTV = createdView.findViewById(R.id.services_ACTV)


        specialityRV = createdView.findViewById(R.id.specialityRV)

        experienceRV = createdView.findViewById(R.id.experienceRV)
        addExperienceBtn = createdView.findViewById(R.id.addExperienceBtn)

//        experience_1_desc_IET = createdView.findViewById(R.id.experience_1_desc_IET)
//        expCertFile1NameTV = createdView.findViewById(R.id.expCertFile1NameTV)
//        addWorkExperienceFile1Btn = createdView.findViewById(R.id.addWorkExperienceFile1Btn)
//
//        experience_2_desc_IET = createdView.findViewById(R.id.experience_2_desc_IET)
//        expCertFile2NameTV = createdView.findViewById(R.id.expCertFile2NameTV)
//        addWorkExperienceFile2Btn = createdView.findViewById(R.id.addWorkExperienceFile2Btn)


        certificateTypeSpnr = createdView.findViewById(R.id.certificateTypeSpnr)
        spinnerLL = createdView.findViewById(R.id.spinnerFL)
        enrollmentDateET = createdView.findViewById(R.id.enrollmentDateET)
        universityNameET = createdView.findViewById(R.id.universityNameET)
        graduationDateET = createdView.findViewById(R.id.graduationDateET)
        addCertBtn = createdView.findViewById(R.id.addCertBtn)
        certFileTV = createdView.findViewById(R.id.certFileTV)
        aboutUserChar = createdView.findViewById(R.id.aboutUser_char_TV)
        aboutUserCharMax = createdView.findViewById(R.id.aboutUserMax)
        saveBtn = createdView.findViewById(R.id.saveBtn)
    }

    fun updateUI() {

//        user_name_TIL.editText?.setText(_user!!.username ?: currentFreelancer!!.username ?: "")
        first_name_TIL.editText?.setText(_user!!.firstName)
        second_name_TIL.editText?.setText(_user!!.lastName)
        phone_number_TIL.editText?.setText(convertArabicNumbersToString(_user!!.phone?.takeLast(9) ?: _user!!.phone))
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

        email_TIL.editText?.setText(_user!!.email)
        val fileName = currentFreelancer!!.image?.substring(
            currentFreelancer!!.image?.lastIndexOf('/')?.plus(1) ?: 0,
            currentFreelancer!!.image?.length ?: 0
        )
//        photoFileNameTV.setText(fileName)

//        setOtherUserImage(
//            currentFreelancer!!.image,
//            userIV,
//            NewUser.FREELANCER_USER_TYPE,
//            currentFreelancer!!.gender
//        )
//        photoFileNameTV.setOnClickListener {
//            photoFile = null
//            photoFileNameTV.text = ""
//
//            setOtherUserImage(
//                null,
//                userIV,
//                NewUser.FREELANCER_USER_TYPE,
//                currentFreelancer!!.gender
//            )
//        }
//        photoFileBtn.setOnClickListener {
//            photoAtivityResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                openFile(null, IMAGES_TYPE, photoAtivityResultLauncher)
////            }
//        }

        genderRG.check(
            when (currentFreelancer!!.gender) {
                NewUser.MALE_USER_GENDER -> R.id.maleRadio
                NewUser.FEMALE_USER_GENDER -> R.id.femaleRadio
                else -> R.id.maleRadio
            }
        )

        generalInfoTitleLL.setOnClickListener {
            generalInfoTitleIV.isSelected = !generalInfoTitleIV.isSelected
            togleVisisbilityAndGg(generalInfoLL, it)
        }
        //--------------------------------------------------------

        about_TIL.editText?.setText(_user!!.about ?: currentFreelancer!!.about ?: "")

        experiences_TIL.editText?.setText(currentFreelancer!!.getExperiences())
        exp_years_TIL.editText?.setText(convertArabicNumbersToString(currentFreelancer!!.yearOfExperience.toString()))
        exp_years_TIL.setEndIconOnClickListener {
            if (exp_years_TIL.editText?.text.isNullOrEmpty())
                exp_years_TIL.editText?.setText("0")
            else
                exp_years_TIL.editText?.setText(
                  convertArabicNumbersToString(exp_years_TIL.editText?.text.toString().trim().toInt().dec().toString())
                )

        }

        exp_years_TIL.setStartIconOnClickListener {
            if (exp_years_TIL.editText?.text.isNullOrEmpty())
                exp_years_TIL.editText?.setText("0")
            else
                exp_years_TIL.editText?.setText(
                 convertArabicNumbersToString( exp_years_TIL.editText?.text.toString().trim().toInt().inc().toString())
                )

        }

        exp_years_TIL.setEndIconOnClickListener {
            if (exp_years_TIL.editText?.text.isNullOrEmpty())
                exp_years_TIL.editText?.setText("0")
            else
                exp_years_TIL.editText?.setText(
                    convertArabicNumbersToString( exp_years_TIL.editText?.text.toString().trim().toInt().dec().toString())
                )

        }


        typeOfServicesAdapter = DomainsProject_Adapter(mutableListOf())
        specialityRV.adapter = typeOfServicesAdapter

        typeOfServicesAdapter.setList( currentFreelancer!!.listOfServices!!)









//        domains_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
////                if (parent.selectedItemPosition != 0)
//                p0?.let {
//                    typeOfServicesAdapter.addItem(it.selectedItem.toString().trim())
//                }
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//
//        }

        services_MACTV.setOnItemClickListener { adapterView, view, i, l ->
            val item = adapterView.getItemAtPosition(i).toString().trim()
            if (i != 0 ){

            }

        }

        services_MACTV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val item = p0.toString().trim()

                domains.add(item)

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


//        currentFreelancer!!.typeOfServices?.let {
//            typeOfServicesAdapter.addItem(it)
//        }

        experienceAdapter = ExperienceFilesRvAdapter(mutableListOf(), this, requireContext())
        experienceRV.adapter = experienceAdapter
        if (experienceAdapter.itemCount == 2 ){
            addExperienceBtn.visibility = View.GONE
        }else{
            addExperienceBtn.setOnClickListener {
                experienceAdapter.updateOrAdd(MyFile(name = "خبرة سابقة", size = 0,null,null,null,null))



                newExperienceFile()
            }
        }



//        experience_1_desc_IET.setText(currentFreelancer!!.previousWorkDesc0 ?: "")
//        val file0 = currentFreelancer!!.listOfFiles?.get(0)
//        expCertFile1NameTV.text = currentFreelancer!!.previousWorkfile0?.name
//            ?: file0?.fileName ?: MyFile.FILE_NAME_DISPLAY
//        expCertFile1NameTV.setOnClickListener {
////            workExperienceFile0 = null
////            expCertFile1NameTV.text = ""
////            experience_1_desc_IET.setText("")
//
//            val browserIntent =
//                Intent(Intent.ACTION_VIEW,  workExperienceFile0?.uri ?: Uri.parse(file0?.fileUrl))
//                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            startActivity(browserIntent)
//        }
//        addWorkExperienceFile1Btn.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                openFile(null, PDF_TYPE, expFile0AtivityResultLauncher)
//            }
//        }

//        experience_2_desc_IET.setText(currentFreelancer!!.previousWorkDesc1 ?: "")
//        val file1 = currentFreelancer!!.listOfFiles?.get(1)
//        expCertFile2NameTV.text = currentFreelancer!!.previousWorkfile1?.name
//            ?: file1?.fileName ?: MyFile.FILE_NAME_DISPLAY
//        expCertFile2NameTV.setOnClickListener {
////            workExperienceFile1 = null
////            expCertFile2NameTV.text = ""
////            experience_2_desc_IET.setText("")
//
//
//            val browserIntent =
//                Intent(Intent.ACTION_VIEW, workExperienceFile1?.uri ?: Uri.parse(file1?.fileUrl))
//                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            startActivity(browserIntent)
//        }
//        addWorkExperienceFile2Btn.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                openFile(null, PDF_TYPE, expFile1AtivityResultLauncher)
//            }
//        }


//        currentFreelancer!!.listOfFiles = currentFreelancer!!.listOfFiles?.filter {
//            it.fileUrl != null && it.fileUrl.endsWith(".tmp")
//        }

//        experienceAdapter.setList(
//            currentFreelancer!!.listOfFiles?.map { freelancerFile ->
//                val myFile = freelancerFile.toMyFile()
//                myFile.name = myFile.name ?: currentFreelancer!!.previousWorkfile0?.name
//                        ?: MyFile.FILE_NAME_DISPLAY
//                myFile.description =
//                    currentFreelancer!!.previousWorkDesc0 ?: myFile.description ?: ""
//                myFile
//            }
//        )

//        val file0 = currentFreelancer!!.listOfFiles?.get(0)
//        val myFile0 = file0?.toMyFile()
//        myFile0?.name =
//            myFile0?.name ?: currentFreelancer!!.previousWorkfile0?.name ?: MyFile.FILE_NAME_DISPLAY
//        myFile0?.description = currentFreelancer!!.previousWorkDesc0 ?: myFile0?.description ?: ""
//
//        val file1 = currentFreelancer!!.listOfFiles?.get(1)
//        val myFile1 = file1?.toMyFile()
//        myFile1?.name = myFile0?.name ?: currentFreelancer!!.previousWorkfile1?.name
//                ?: MyFile.FILE_NAME_DISPLAY
//        myFile1?.description = currentFreelancer!!.previousWorkDesc1 ?: myFile1?.description ?: ""

        val expFiles = currentFreelancer!!.listOfFiles?.filter {
            it.fileCategory == FreelancerFile.WORK_CERTIFICATE_CATEGORY
                    && it.fileUrl != null && !it.fileUrl.endsWith(".tmp", true)
        }?.map {
            it.toMyFile()
        }
        if (expFiles?.isNotEmpty() == true){
            val myFile0 = expFiles[0]
            myFile0.name =
                myFile0.name ?: currentFreelancer!!.previousWorkfile0?.name ?: MyFile.FILE_NAME_DISPLAY
            myFile0.description = currentFreelancer!!.previousWorkDesc0 ?: myFile0.description ?: ""
            myFile0.freelancerFile = currentFreelancer!!.listOfFiles?.get(0)!!
            if (expFiles.size > 1){
                val myFile1 = expFiles[1]
                myFile1.name = myFile0.name ?: currentFreelancer!!.previousWorkfile1?.name
                        ?: MyFile.FILE_NAME_DISPLAY
                myFile1.description = currentFreelancer!!.previousWorkDesc1 ?: myFile1.description
                        ?: ""
                myFile1.freelancerFile = currentFreelancer!!.listOfFiles?.get(1)!!
            }
        }

        experienceAdapter.addList(expFiles)

        domainOfExpertiseTtlLL.setOnClickListener {
            domainOfExpertiseTtlIV.isSelected = !domainOfExpertiseTtlIV.isSelected
            togleVisisbilityAndGg(domainOfExpertiseLL, it)
        }

        val cetServices = resources.getStringArray(R.array.domains_list)

        domains_spinner.setSelection(
            cetServices.indexOf(
                currentFreelancer!!.listOfServices!![0].typeOfServices?.trim()
            )
        )



        //----------------------------------------------------------

        val cetTypes = resources.getStringArray(R.array.edu_cert_type)

        certificateTypeSpnr.setSelection(
            cetTypes.indexOf(
                currentFreelancer!!.typeOfCertificate?.trim()
            )
        )
        enrollmentDateET.setText(convertArabicNumbersToString(convertArabicNumbersToString(currentFreelancer!!.enrollmentYear)) )

        enrollmentDateET.setOnFocusChangeListener { v, b ->
            if (b) showMaterialDatePicker(message = enrollmentDateET.hint.toString(),
                dateValidator = DateValidatorPointBackward.now(),
                onPositiveButtonClickListener = { selection ->
                    calendar.setTimeInMillis(selection)
                    currentFreelancer!!.enrollmentYear = calendar.get(Calendar.YEAR).toString()
                    enrollmentDateET.setText(convertArabicNumbersToString(currentFreelancer!!.enrollmentYear))
                })
        }

//        enrollmentDateET.setText(dateFormatAr.format(currentFreelancer!!.enrollmentDate ?: Date()))
        universityNameET.setText(currentFreelancer!!.universityName)

        graduationDateET.setText(convertArabicNumbersToString(convertArabicNumbersToString(currentFreelancer!!.graduationDate)) )

        graduationDateET.setOnFocusChangeListener { v, b ->
            if (b) {
                val dateValidatorList =
                    mutableListOf<CalendarConstraints.DateValidator>(DateValidatorPointBackward.now())
//                if (currentFreelancer!!.enrollmentDate != null) dateValidatorList.add(
//                    DateValidatorPointForward.from(currentFreelancer!!.enrollmentDate!!.time))

                showMaterialDatePicker(message = graduationDateET.hint.toString(),
                    dateValidator = CompositeDateValidator.allOf(dateValidatorList),
                    onPositiveButtonClickListener = { selection ->
                        calendar.setTimeInMillis(selection)
                        currentFreelancer!!.graduationDate = calendar.get(Calendar.YEAR).toString()
                        graduationDateET.setText(convertArabicNumbersToString(currentFreelancer!!.graduationDate))
                    })
            }
        }

//        certificateTV

        if (currentFreelancer!!.listOfFiles != null && currentFreelancer!!.listOfFiles!!.size > 2){
            val file2 = currentFreelancer!!.listOfFiles?.get(2)
            certFileTV.setText(
                currentFreelancer!!.certification?.educationCertificate0?.name
                    ?: file2?.fileName ?: MyFile.FILE_NAME_DISPLAY

            )


            certFileTV.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(file2?.fileUrl))
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(browserIntent)
            }
        }


        addCertBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                openFile(null, PDF_TYPE, certFileAtivityResultLauncher)
            }

        }
        educationTtlLL.setOnClickListener {
            educationTtlIV.isSelected = !educationTtlIV.isSelected
            togleVisisbilityAndGg(educationLL, it)
        }

        saveBtn.setOnClickListener(onClickListener)

    }

    lateinit var input: String
    fun isValidClient(): Boolean {
//        input = user_name_TIL.editText?.text.toString().trim()
//        if (input.isEmpty()) {
//            generalInfoTitleLL.performClick()
//            user_name_TIL.error = getString(R.string.this_field_is_required)
//            user_name_TIL.requestFocus()
//            return false
//        }
//        _user!!.username = input

        input = first_name_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            if (!generalInfoLL.isVisible) generalInfoTitleLL.performClick()
            first_name_TIL.error = getString(R.string.this_field_is_required)
            first_name_TIL.requestFocus()
            return false
        }
        _user!!.firstName = input

        input = second_name_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            if (!generalInfoLL.isVisible) generalInfoTitleLL.performClick()
            second_name_TIL.error = getString(R.string.this_field_is_required)
            second_name_TIL.requestFocus()
            return false
        }
        _user!!.lastName = input

        input = convertEnglishNumbersToString(phone_number_TIL.editText?.text.toString().trim())
        if (input.isEmpty()) {
            if (!generalInfoLL.isVisible) generalInfoTitleLL.performClick()
            phone_number_TIL.error = getString(R.string.this_field_is_required)
            phone_number_TIL.requestFocus()
            return false
        }

        if (!Patterns.PHONE.matcher(input).matches()) {
            if (!generalInfoLL.isVisible) generalInfoTitleLL.performClick()
            phone_number_TIL.error = getString(R.string.phone_number_is_not_valid)
            phone_number_TIL.requestFocus()
            return false
        }

        if (input.startsWith('0')) {
            input = input.substring(1)
        }

        if (input.length != 9) {
            if (!generalInfoLL.isVisible) generalInfoTitleLL.performClick()
            phone_number_TIL.error = getString(R.string.phone_number_is_not_valid)
            phone_number_TIL.requestFocus()
            return false
        }

//        if (!Patterns.PHONE.matcher(input).matches() ||
//            input.length < 10 ||
//            (input.length == 10 && !input.startsWith('0')) ||
//            input.length < 12 ||
//            (input.length == 12 && !input.startsWith("966")) ||
//            input.length > 12
//        ) {
//            generalInfoTitleLL.performClick()
//            phone_number_TIL.error = getString(R.string.phone_number_is_not_valid)
//            phone_number_TIL.requestFocus()
//            return false
//        }
//
//        if (input.length == 10)
//            input = "966${input.substring(1)}"

//        ------------- country list

        if (countryCodeSpnr.selectedItemPosition == 0) {
            if (!generalInfoLL.isVisible) generalInfoTitleLL.performClick()
            activitySnackbar.setText("يجب تحديد مفتاح الدولة في رقم الهاتف")
                .show()
            return false
        }

        input = (countryCodeSpnr.selectedItem as CountryItem).dialCode.substring(1) + input
        _user!!.phone = input

        input = email_TIL.editText?.text.toString().trim()
        if (email_TIL.editText?.text.isNullOrEmpty()) {
            if (!generalInfoLL.isVisible) generalInfoTitleLL.performClick()
            email_TIL.error = getString(R.string.this_field_is_required)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            if (!generalInfoLL.isVisible) generalInfoTitleLL.performClick()
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

        currentFreelancer!!.gender = _user!!.gender

        return true
    }

    fun isValidFreelancer(): Boolean {
//        "skills" to currentFreelancer!!.type_of_service!!,
//        "experience" to currentFreelancer!!.experience!!,

        if (domains_spinner.selectedItemPosition != 0) {
           domains.clear()
            domains.add(domains_spinner.selectedItem.toString() )
        }

        input = about_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            if (!domainOfExpertiseLL.isVisible) domainOfExpertiseTtlLL.performClick()
            about_TIL.error = getString(R.string.this_field_is_required)
            about_TIL.requestFocus()
            return false
        }
        currentFreelancer!!.about = input
        _user!!.about = input

        input = experiences_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            if (!domainOfExpertiseLL.isVisible) domainOfExpertiseTtlLL.performClick()

            experiences_TIL.error = getString(R.string.this_field_is_required)
            experiences_TIL.requestFocus()
            return false
        }
        currentFreelancer!!.experience = input

        input = exp_years_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            if (!domainOfExpertiseLL.isVisible) domainOfExpertiseTtlLL.performClick()
            exp_years_TIL.error = getString(R.string.this_field_is_required)
            exp_years_TIL.requestFocus()
            return false
        }
        var yearOfExperience: Int
        try {
            yearOfExperience = Integer.parseInt(input)
        } catch (nfe: NumberFormatException) {
            if (!domainOfExpertiseLL.isVisible) domainOfExpertiseTtlLL.performClick()
            exp_years_TIL.error = "العدد المدخل غير صحيح"
            exp_years_TIL.requestFocus()
            return false
        }
        if (yearOfExperience < 0) {
            if (!domainOfExpertiseLL.isVisible) domainOfExpertiseTtlLL.performClick()
            exp_years_TIL.error = "يجب ان تكون قيمة مطلقة"
            exp_years_TIL.requestFocus()
            return false
        }
        currentFreelancer!!.yearOfExperience = yearOfExperience

//        if (typeOfServicesAdapter.itemCount == 0) {
//            if (!domainOfExpertiseLL.isVisible) domainOfExpertiseTtlLL.performClick()
//
//            Snackbar.make(domains_spinner, "يرجى اضافة مجال", Snackbar.LENGTH_SHORT).show()
//            return false
//        }
//        currentFreelancer!!.listOfServices ?.let {
//                if (it.isNotEmpty())
//                    it[0].typeOfServices =  typeOfServicesAdapter.toString()
//            }
//        currentFreelancer!!.listOfServices!![0].typeOfServices = typeOfServicesAdapter.toString()

        val files = experienceAdapter.getList()
        if (files.isNotEmpty()) {
//            if (workExperienceFile0 != null) {
//                input = experience_1_desc_IET.text.toString().trim()
//                if (input.isEmpty()) {
//                    experience_1_desc_IET.error = getString(R.string.this_field_is_required)
//                    experience_1_desc_IET.requestFocus()
//                    return false
//                }
//                currentFreelancer!!.previousWorkDesc0 = input
//            }
            currentFreelancer!!.previousWorkDesc0 = files[0].description
            currentFreelancer!!.previousWorkfile0 = files[0]//workExperienceFile0

            if (files.size > 1) {
//                if (workExperienceFile1 != null) {
//                    input = experience_2_desc_IET.text.toString().trim()
//                    if (input.isEmpty()) {
//                        experience_2_desc_IET.error = getString(R.string.this_field_is_required)
//                        experience_2_desc_IET.requestFocus()
//                        return false
//                    }
//                    currentFreelancer!!.previousWorkDesc1 = input
//                }
                currentFreelancer!!.previousWorkDesc1 = files[1].description
                currentFreelancer!!.previousWorkfile1 = files[1] // workExperienceFile1
            }

        }


        // Education
        if (certificateTypeSpnr.selectedItemPosition == 0) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            spinnerLL.setBackgroundResource(R.drawable.input_bg_red_borders_curved_5_transpaent_filled)
            Snackbar.make(domains_spinner, "يرجى اضافة مجال", Snackbar.LENGTH_SHORT).show()
            return false
        }
        if (certificateTypeSpnr.selectedItem == null) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            spinnerLL.setBackgroundResource(R.drawable.input_bg_red_borders_curved_5_transpaent_filled)
            Snackbar.make(domains_spinner, "يرجى اضافة مجال", Snackbar.LENGTH_SHORT).show()
            return false
        }

        spinnerLL.setBackgroundResource(R.drawable.input_bg_gray_borders_curved_5_transparent_filled)

        currentFreelancer!!.typeOfCertificate = certificateTypeSpnr.selectedItem.toString()

        input = enrollmentDateET.text.toString().trim()
        if (input.isEmpty()) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            enrollmentDateET.error = getString(R.string.this_field_is_required)
            enrollmentDateET.requestFocus()
            return false
        }

        var enrollmentYear: Int
        try {
            enrollmentYear = Integer.parseInt(input)
        } catch (nfe: NumberFormatException) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            enrollmentDateET.error = "العدد المدخل غير صحيح"
            enrollmentDateET.requestFocus()
            return false
        }
        if (enrollmentYear < 0) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            enrollmentDateET.error = "يجب ان تكون قيمة مطلقة"
            enrollmentDateET.requestFocus()
            return false
        }
        currentFreelancer!!.enrollmentYear = enrollmentYear.toString()


        input = graduationDateET.text.toString().trim()
        if (input.isEmpty()) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            graduationDateET.error = getString(R.string.this_field_is_required)
            graduationDateET.requestFocus()
            return false
        }

        var graduationYear: Int
        try {
            graduationYear = Integer.parseInt(input)
        } catch (nfe: NumberFormatException) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            graduationDateET.error = "العدد المدخل غير صحيح"
            graduationDateET.requestFocus()
            return false
        }
        if (graduationYear < 0) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            graduationDateET.error = "يجب ان تكون قيمة مطلقة"
            graduationDateET.requestFocus()
            return false
        }
        currentFreelancer!!.graduationYear = graduationYear.toString()

        input = universityNameET.text.toString().trim()
        if (input.isEmpty()) {
            if (!educationTtlIV.isSelected)
                educationTtlLL.performClick()
            universityNameET.error = getString(R.string.this_field_is_required)
            universityNameET.requestFocus()
            return false
        }
        currentFreelancer!!.universityName = input
        return true
    }

    val expFile0AtivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object : FileListener {
                override fun onFileChosen(myFile: MyFile) {
//
//                    if (myFile.size > MAX_FILE_SIZE) {
//                        expCertFile1NameTV.setTextColor(DARK_RED_COLOR_STATE_LIST)
//                        expCertFile1NameTV.text = getString(R.string.exeeding_max_file_size)
//
//                    } else {
//                        expCertFile1NameTV.setTextColor(BLACK_COLOR_STATE_LIST)
//                        expCertFile1NameTV.text = myFile.name
//                        workExperienceFile0 = myFile
//                    }
                }

            })

        }
    }

    /*
    val photoAtivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            getImageFile(uri, object : FileListener {
                override fun onFileChosen(myFile: MyFile) {

                    Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
                    if (myFile.size > MAX_FILE_SIZE) {

                        activitySnackbar.setText(UserProfileFragment.EXCEEDING_MAX_FILE_SIZE_MSG)
                            .show()
                        CoroutineScope(Dispatchers.Default).launch {

                            Log.w(
                                javaClass.simpleName,
                                "onFileChosen: myFile.imageRealPath = ${myFile.imageRealPath} "
                            )
//                            val compressedImageFile = Compressor.compress(requireContext(), file) {
//                                size(MAX_FILE_SIZE) // 20 KB
//                            }
//                            if (compressedImageFile.length() > MAX_FILE_SIZE){
//                                activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
//                                    .show()
//                            } else {
//                                myFile.name = compressedImageFile.name
//                                myFile.size = compressedImageFile.length()
//                                myFile.inputStream = compressedImageFile.inputStream()
//                                myFile.uri = compressedImageFile.toUri()
//
//                                if (_user!!.isClient())
//                                    updateClientPhoto(myFile)
//                                else if (_user!!.isFreelancer())
//                                    updateFreelancerPhoto(myFile)
//                            }
                        }

                    } else {
                        photoFileNameTV.setTextColor(BLACK_COLOR_STATE_LIST)
                        photoFileNameTV.text = myFile.name
                        photoFile = myFile

                        setOtherUserImage(
                            myFile.uri,
                            userIV,
                            NewUser.FREELANCER_USER_TYPE,
                            currentFreelancer!!.gender
                        )
                    }
                }

            })
        }
    */

    //    var photoFile: MyFile? = null
    var workExperienceFile0: MyFile? = null
    var workExperienceFile1: MyFile? = null

    val expFile1AtivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object : FileListener {
                override fun onFileChosen(myFile: MyFile) {

//                    if (myFile.size > MAX_FILE_SIZE) {
//                        expCertFile2NameTV.setTextColor(DARK_RED_COLOR_STATE_LIST)
//                        expCertFile2NameTV.text = getString(R.string.exeeding_max_file_size)
//                    } else {
//                        expCertFile2NameTV.setTextColor(BLACK_COLOR_STATE_LIST)
//                        expCertFile2NameTV.text = myFile.name
//                        workExperienceFile1 = myFile
//                    }
                }

            })
        }
    }

    var eduCert: MyFile? = null
    val certFileAtivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object : FileListener {
                override fun onFileChosen(myFile: MyFile) {

                    if (myFile.size > MAX_FILE_SIZE) {
                        certFileTV.setTextColor(DARK_RED_COLOR_STATE_LIST)
                        certFileTV.text = getString(R.string.exeeding_max_file_size)
                    } else {
                        certFileTV.setTextColor(BLACK_COLOR_STATE_LIST)
                        certFileTV.text = myFile.name
                        eduCert = myFile
                    }
                }

            })
        }
    }

    override fun editExperienceFile(file: MyFile, pos: Int) {

        AddEditExperience(file, this)
            .show(childFragmentManager, AddEditExperience.tag)

//        activityVM.currentFileMLD.postValue(file)
//        activityVM.currentFileMLD.observe(viewLifecycleOwner) {
//            Log.w(javaClass.simpleName, "edit: it = $it")
//            if (file != it) {
//                experienceAdapter.update(it, pos)
//                activityVM.currentFileMLD.removeObservers(viewLifecycleOwner)
//            }
//        }
//        findNavController()
//            .navigate(R.id.action_freelancerPersonalInfoEditingFragment_to_addEditExperience)
    }

    fun newExperienceFile() {
        AddEditExperience(null, this)
            .show(childFragmentManager, AddEditExperience.tag)

//        activityVM.currentFileMLD.postValue(null)
//        activityVM.currentFileMLD.observe(viewLifecycleOwner) {
//            if (it != null) experienceAdapter.add(it)
//            activityVM.currentFileMLD.removeObservers(viewLifecycleOwner)
//        }
//        findNavController()
//            .navigate(R.id.action_freelancerPersonalInfoEditingFragment_to_addEditExperience)
    }


    override fun onExperienceFileSubmitted(file: MyFile) {
        if (experienceAdapter.getList().isEmpty()) {
//             fileId, fileForUpdate, fileDescription

        val params = mutableMapOf<String, String>(
            "fileId" to currentFreelancer!!.listOfFiles!![0].id.toString() ,

            "attachmentDesc" to "dkcmdk",
            "fileDescription" to "ldskmc"
        )
            freelancerVM.updateFreelancerExpFiles(
               params,
                file
            ).observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, {
                    experienceAdapter.update(file, experienceAdapter.getIndex(file))

                })

            }

        } else {
            val params = mutableMapOf<String, String>(
                "fileId" to currentFreelancer!!.listOfFiles!![1].id.toString() ,

                "attachmentDesc" to "dkcmdk",
                "fileDescription" to "ldskmc"
            )
            freelancerVM.updateFreelancerExpFiles(
                params,
                file
            ).observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, {
                    experienceAdapter.update(file, experienceAdapter.getIndex(file))

                })

            }
        }


    }




    override fun onExperienceFileSubmittedRemove(file: MyFile) {
//        if (experienceAdapter.getList().contains(file)) {
        // fileId, fileForUpdate, fileDescription
        val emptyFileData = ByteArray(0)
        val params = mutableMapOf<String, String>(
            "fileId" to currentFreelancer!!.listOfFiles!![1].id.toString(),

            "attachmentDesc" to "",
            "fileDescription" to ""
        )
        freelancerVM.updateFreelancerExpFilesremove(
            params,
            null
        ).observe(viewLifecycleOwner) {
            newHandleSuccessOrErrorResponse(it, {

            })

        }

//        } else {
//            experienceAdapter.add(file)
//        }


    }



    fun createMultiPartBodyFromFile(paramName: String, myFile: MyFile?): MultipartBody.Part {

        return MultipartBody.Part.createFormData(
            paramName,
            myFile?.name,
            RequestBody.create(myFile?.mimeType!!.toMediaTypeOrNull(), myFile.inputStream!!.readBytes())
        )
    }

    fun createMultipartBody(file: File?): MultipartBody? {
        val MEDIA_TYPE_JPEG = "image/jpeg".toMediaTypeOrNull()

        // Create a RequestBody for the file
        val requestFile = file?.let { RequestBody.create(MEDIA_TYPE_JPEG, it) }

        // Create a MultipartBody.Part to handle the file data
        val requestBody =
            requestFile?.let { MultipartBody.Part.createFormData("profileImage", file.name, it) }

        // Build the MultipartBody
        return requestBody?.let {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(it)
                .build()
        }
    }
    override fun onResume() {
        super.onResume()
        val serviceLD =

            serviceVM.getAllServices()



        serviceLD.observe(viewLifecycleOwner, requestObserver)





    }

    }


