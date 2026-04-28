package sa.gov.ksaa.dal.ui.fragments.auth.signUp.freelancer

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Certification
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.adapters.DomainsListAdapter
import sa.gov.ksaa.dal.ui.adapters.ExperienceFilesRvAdapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.ServicesVM

class FreelancerRegistration2Experience : BaseFragment(R.layout.fragment_sign_up_4_freelancer_1),
    ExperienceFilesRvAdapter.OnClickListener, AddEditExperience.OnSubmitLister,
    AdapterView.OnItemSelectedListener {

    val MAX_FILE_SIZE = 2_000_000

    lateinit var about_TIL: EditText
    lateinit var experiences_TIL: TextInputLayout
    lateinit var exp_years_TIL: TextInputLayout
    private lateinit var certification: Certification

        lateinit var domains_spinner: Spinner
    lateinit var specialityRV: RecyclerView
    lateinit var services_ACTV: MaterialAutoCompleteTextView
    lateinit var experienceRV: RecyclerView
    lateinit var addExperienceBtn: MaterialButton
//    lateinit var experience_1_desc_IET: EditText
//    lateinit var experience_2_desc_IET: EditText
    lateinit var next_btn: Button
    lateinit var add_btn: Button
    lateinit var prevBtn: MaterialButton

    //    lateinit var specialitiesList: List<String>
    lateinit var domainsAdapter: DomainsListAdapter
    lateinit var experienceAdapter: ExperienceFilesRvAdapter
    lateinit var aboutUserCharMax: TextView
    lateinit var aboutUserChar: TextView
    lateinit var f1text: TextView
    lateinit var aboutET: EditText
    lateinit var serviceList: List<ServicesModel>
//    lateinit var addWorkExperienceFile1Btn: MaterialButton
//    lateinit var addWorkExperienceFile2Btn: MaterialButton

//    lateinit var f0Name: MaterialTextView
//    lateinit var f1Name: MaterialTextView
//    var workExperienceFile0: MyFile? = null
//    var workExperienceFile1: MyFile? = null
    val vm: FreelancersVM by viewModels()
    val serviceVM: ServicesVM by viewModels()
    val eduCert0ActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object: FileListener{
                override fun onFileChosen(myFile: MyFile) {
                    certification.educationCertificate1 = myFile
                    f1text.text = myFile.name
                }

            })

        }
    }

    val requestObserver = { newResource: NewResource<List<ServicesModel>> ->
        newHandleSuccessOrErrorResponse(newResource,
            onSuccess = { services ->
                val servicesListdd: MutableList<ServicesModel> = mutableListOf()
                servicesListdd.add(0, ServicesModel(0,"","l","المجال"))
                servicesListdd.addAll(services)
                serviceList = services




                val arrayAdapter = ArrayAdapter(requireContext(),R.layout.fragment_custom_spinner,R.id.AutoCompleteTextView,servicesListdd.map { it.name })



                this.domains_spinner.onItemSelectedListener = this
                domains_spinner.adapter = arrayAdapter


            }){

        }
    }
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)

        currentFreelancer = NewFreelancer()
        exp_years_TIL.editText?.setArabicNumberFormatter()

//        addWorkExperienceFile1Btn.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                openFile(null, PDF_TYPE, expFile0AtivityResultLauncher)
//            }
//        }
//
//        f0Name.setOnClickListener {
//            workExperienceFile0 = null
//            f0Name.text = ""
//            experience_1_desc_IET.setText("")
//
//        }
//
//        addWorkExperienceFile2Btn.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                openFile(null, PDF_TYPE, expFile1AtivityResultLauncher)
//            }
//        }
//
//        f1Name.setOnClickListener {
//            workExperienceFile1 = null
//            f1Name.text = ""
//            experience_2_desc_IET.setText("")
//        }

        next_btn.setOnClickListener {
            if (isValidFreelancer()) {
                activityVM.setUser(_user)
                activityVM.currentFreelancerMLD.postValue(currentFreelancer)
                findNavController().navigate(R.id.action_freelancerRegisterationFragment2_to_freelancerRegisterationFragment3)
            }
        }
//        specialitiesList = mutableListOf()
        domainsAdapter = DomainsListAdapter(mutableListOf())
        specialityRV.adapter = domainsAdapter
        domains_spinner.onItemSelectedListener = this

        services_ACTV.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.w(javaClass.simpleName, "services_ACTV.onItemSelectedListener.onItemSelected: adapterView?.selectedItem = ${adapterView?.selectedItem}")
                domainsAdapter.addItem(adapterView?.selectedItem.toString())
                services_ACTV.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        experienceAdapter = ExperienceFilesRvAdapter(mutableListOf(), this, requireContext())
        experienceRV.adapter = experienceAdapter
        addExperienceBtn.setOnClickListener {
            newExperienceFile()
        }
        certification = Certification()
        add_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                openFile(null, PDF_TYPE, eduCert0ActivityResultLauncher )
            }
        }

    }

//    val expFile0AtivityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { activityResult ->
//        if (activityResult.resultCode == Activity.RESULT_OK) {
//            gettingFile(activityResult, object : FileListener {
//                override fun onFileChosen(myFile: MyFile) {
//
//                    if (myFile.size > MAX_FILE_SIZE) {
//                        f0Name.setTextColor(DARK_RED_COLOR_STATE_LIST)
//                        f0Name.text = getString(R.string.exeeding_max_file_size)
//                    } else {
//                        f0Name.setTextColor(BLACK_COLOR_STATE_LIST)
//                        f0Name.text = myFile.name
//                        workExperienceFile0 = myFile
//                    }
//
//                }
//
//            })
//
//        }
//    }

//    val expFile1AtivityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { activityResult ->
//        if (activityResult.resultCode == Activity.RESULT_OK) {
//            gettingFile(activityResult, object : FileListener {
//                override fun onFileChosen(myFile: MyFile) {
//
//                    if (myFile.size > MAX_FILE_SIZE) {
//                        f1Name.setTextColor(DARK_RED_COLOR_STATE_LIST)
//                        f1Name.text = getString(R.string.exeeding_max_file_size)
//                    } else {
//                        f1Name.setTextColor(BLACK_COLOR_STATE_LIST)
//                        f1Name.text = myFile.name
//                        workExperienceFile1 = myFile
//                    }
//                }
//
//            })
//        }
//    }

    lateinit var input: String
    private fun isValidFreelancer(): Boolean {
        input = about_TIL.text.toString().trim()
        if (input.isEmpty()) {
            _user!!.about = "لا يوجد"
        }else {
            _user!!.about = input
        }
        input = experiences_TIL.editText?.text.toString().trim()
        if (input.isEmpty()) {
            experiences_TIL.error = getString(R.string.this_field_is_required)
            experiences_TIL.requestFocus()
            return false
        }
        currentFreelancer!!.experience = input

        input = convertEnglishNumbersToString(exp_years_TIL.editText?.text.toString().trim())
        if (input.isEmpty()) {
            exp_years_TIL.error = getString(R.string.this_field_is_required)
            exp_years_TIL.requestFocus()
            return false
        }
        var yearOfExperience = 0
        try {
            yearOfExperience = Integer.parseInt(input)
        } catch (nfe: NumberFormatException) {
            exp_years_TIL.error = "العدد المدخل غير صحيح"
            exp_years_TIL.requestFocus()
            return false
        }
        if (yearOfExperience < 0) {
            exp_years_TIL.error = "يجب ان تكون قيمة مطلقة"
            exp_years_TIL.requestFocus()
            return false
        }
        if (yearOfExperience > 40) {
            exp_years_TIL.error = "يجب ادخال قيمة صحيحة"
            exp_years_TIL.requestFocus()
            return false
        }

        currentFreelancer!!.yearOfExperience = yearOfExperience

        if (domainsAdapter.itemCount == 0) {
            activitySnackbar.setText("يرجى اضافة مجال")
                .show()
            return false
        }
        currentFreelancer!!.typeOfServices = domainsAdapter.toString()


        if (certification.educationCertificate1 == null){
            add_btn.error = "يجب رفع ملف الشهادة"
            add_btn.requestFocus()
            return false
        }

        val files = experienceAdapter.getList()
        if (files.isNotEmpty()){
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

            if (files.size > 1){
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
        currentFreelancer!!.userId = _user!!.userId

        Log.i(javaClass.simpleName, "isValidFreelancer: freelancer = $currentFreelancer")
        return true
    }

    private fun initViews(createdView: View) {
        about_TIL = createdView.findViewById(R.id.about_TIL)
        experiences_TIL = createdView.findViewById(R.id.experiences_TIL)
        exp_years_TIL = createdView.findViewById(R.id.exp_years_TIL)
        domains_spinner = createdView.findViewById(R.id.domains_spinner)
        specialityRV = createdView.findViewById(R.id.specialityRV)
        aboutUserChar = createdView.findViewById(R.id.aboutUser_char_TV)
        aboutUserCharMax = createdView.findViewById(R.id.aboutUserMax)
        add_btn = createdView.findViewById(R.id.add_file_btn1)
        f1text = createdView.findViewById(R.id.f11Name)


        services_ACTV = createdView.findViewById(R.id.services_ACTV)
//        services_ACTV.setAdapter(
//            ArrayAdapter(
//                requireContext(), android.R.layout.select_dialog_item,
//                serviceList
//            )
//        )
//        services_ACTV.threshold = 0 //will start working from first character

        experienceRV = createdView.findViewById(R.id.experienceRV)
        addExperienceBtn = createdView.findViewById(R.id.addExperienceBtn)
//        experience_1_desc_IET = createdView.findViewById(R.id.experience_1_desc_IET)
//        experience_2_desc_IET = createdView.findViewById(R.id.experience_2_desc_IET)
        next_btn = createdView.findViewById(R.id.next_btn)

//        addWorkExperienceFile1Btn = createdView.findViewById(R.id.addWorkExperienceFile1Btn)
//        addWorkExperienceFile2Btn = createdView.findViewById(R.id.addWorkExperienceFile2Btn)
//        f0Name = createdView.findViewById(R.id.f0Name)
//        f1Name = createdView.findViewById(R.id.f1Name)

        aboutUserCharMax.text = numberFormat.format(250)
        aboutUserChar.text = numberFormat.format(0)
        about_TIL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar.text = p0?.length.toString()

                val noChars = p0?.length?:0
                aboutUserChar.text = numberFormat.format(noChars)
                if (noChars >=250){
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    aboutET.text?.delete(250, noChars)
                } else {
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_font_gray))
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }


    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
//        parent.getItemAtPosition(pos)
        Log.w(
            javaClass.simpleName,
            "onItemSelected: parent.selectedItemPosition = ${parent.selectedItemPosition}"
        )
        if (parent.selectedItemPosition != 0)
            domainsAdapter.addItem(parent.selectedItem.toString())
        Log.w(this.javaClass.simpleName, "onFileChosen: gender = ${parent.selectedItem.toString()}")
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    override fun editExperienceFile(file: MyFile, pos: Int) {

        AddEditExperience(file, this)
            .show(childFragmentManager, AddEditExperience.tag)

//        activityVM.currentFileMLD.postValue(file)
//        activityVM.currentFileMLD.observe(viewLifecycleOwner) {
//            experienceAdapter.update(it, pos)
//            activityVM.currentFileMLD.removeObservers(viewLifecycleOwner)
//        }
//        findNavController()
//            .navigate(R.id.action_freelancerRegisterationFragment2_to_addEditExperience)
    }

    override fun onExperienceFileSubmittedRemove(file: MyFile) {
        TODO("Not yet implemented")
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
//            .navigate(R.id.action_freelancerRegisterationFragment2_to_addEditExperience)
    }

    override fun onExperienceFileSubmitted(myFile: MyFile) {
        experienceAdapter.updateOrAdd(myFile)
    }
    override fun onResume() {
        super.onResume()
        val serviceLD =

            serviceVM.getAllServices()



        serviceLD.observe(viewLifecycleOwner, requestObserver)





    }


}