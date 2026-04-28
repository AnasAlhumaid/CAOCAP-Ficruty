package sa.gov.ksaa.dal.ui.fragments.projects.client.addEditProject

import android.app.Activity
import android.graphics.Color
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
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.AddProjectFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.adapters.DomainsListAdapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment

import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM
import sa.gov.ksaa.dal.ui.viewModels.ServicesVM
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

class AddEditProject1Fragment : BaseFragment(R.layout.fragment_projects_add_edit),
    AdapterView.OnItemSelectedListener {
    companion object {
        const val projectIdArgKey = "projectId"
    }

    lateinit var textFile : TextView
    lateinit var projectTitle: EditText
    lateinit var ttl_char_TV: TextView
    lateinit var ttlMax: TextView
    lateinit var domainsSpinnerFL: FrameLayout
    lateinit var aboutProjectET: EditText
    lateinit var about_char_TV: TextView
    lateinit var aboutMax: TextView
//    lateinit var detailedDescriptionET: EditText
    lateinit var expectedPriceET: EditText
//    lateinit var noQuotaionsET: EditText
    lateinit var nextBtn: MaterialButton
    lateinit var domainsAdapter: DomainsListAdapter
    lateinit var specialityRV: RecyclerView
    lateinit var domainsSpinner: Spinner
    val serviceVM: ServicesVM by viewModels()
    lateinit var serviceList: List<ServicesModel>
    lateinit var project: NewProject
    lateinit var uploadButton : Button
    var projectId: Int? = null
private lateinit var fileAdd : AddProjectFile
    val vm: ProjectsVM by viewModels()

    val requestObserver = { newResource: NewResource<List<ServicesModel>> ->
        newHandleSuccessOrErrorResponse(newResource,
            onSuccess = { services ->
                val servicesListdd: MutableList<ServicesModel> = mutableListOf()
                servicesListdd.add(0,ServicesModel(0,"","l","المجال"))
                servicesListdd.addAll(services)
                serviceList = services




                val arrayAdapter = ArrayAdapter(requireContext(),R.layout.fragment_custom_spinner,R.id.AutoCompleteTextView,servicesListdd.map { it.name })



                this.domainsSpinner.onItemSelectedListener = this
                domainsSpinner.adapter = arrayAdapter


            }){

        }
    }

    val addFileActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object: FileListener{
                override fun onFileChosen(myFile: MyFile) {
                    fileAdd.addFile = myFile
                    textFile.text = myFile.name

                }

            })

        }
    }
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        mainActivity.appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE

        initViews(createdView)
        fileAdd = AddProjectFile()


        domainsAdapter = DomainsListAdapter(mutableListOf())
        specialityRV.adapter = domainsAdapter

        projectId = arguments?.getInt(ProjectDetailsFragment.projectIdArgKey, 0)

        expectedPriceET.setArabicNumberFormatter()
        expectedPriceET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                val noChars = p0?.length?:0


                if (noChars >=5){

                    expectedPriceET.text?.delete(5, noChars)
                }
            }

            override fun afterTextChanged(p0: Editable?) {


            }

        })

        Log.i(TAG, "onViewCreated: vm = $vm")
        if (projectId != null && projectId != 0)
//            vm.editProjectById(projectId!!)
//                .observe(viewLifecycleOwner, Observer {
//                    // updateUI()
//                    when (it) {
//                        is Resource.Success -> {
//                            Log.i(TAG, "projectsObserver : it.response = ${it.response}")
//                            mainActivity.progress_bar.visibility = View.INVISIBLE
//                            val data = it.response!!.data
//                            if (data != null) {
//                                project = data
//                                updateUI()
//                            }
//                        }
//
//                        is Resource.Loading -> {
//                            mainActivity.progress_bar.visibility = View.VISIBLE
//                        }
//
//                        is Resource.Error -> {
//                            mainActivity.progress_bar.visibility = View.INVISIBLE
//                            Log.i(TAG, "message: ${it.message}")
//                        }
//
//                        else -> {
//                            mainActivity.progress_bar.visibility = View.INVISIBLE
//                        }
//                    }
//                })
        else {
            project = NewProject()
        }

        nextBtn.setOnClickListener{

            activityVM.addProjectFileMLD.postValue(fileAdd)

            if (validateInput()){
                activityVM.newProjectMLD.value = project
                findNavController().navigate(R.id.action_addEditProjectFragment_to_addEditProject2Fragment)
            }
        }


        uploadButton.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                openFile(null, BaseFragment.All_MIME_TYPES, addFileActivityResultLauncher)
            }
        }

    }

    lateinit var input: String
    private fun validateInput(): Boolean{
        project.userId = activityVM.userMLD.value!!.userId
        input = projectTitle.text.toString().trim()
        if (input.isEmpty()){
            projectTitle.error = getString(R.string.this_field_is_required)
            projectTitle.requestFocus()
            return false
        }
        project.projectTitle = input
        // project.project_category
//        project.project_category = domainsSpinner.selectedItem as String
        Log.i(TAG, "updateProject: domainsSpinner.selectedItem = ${this.domainsSpinner.selectedItem}")

        if (domainsAdapter.itemCount == 0){
            domainsSpinnerFL.setBackgroundResource(R.drawable.input_bg_red_borders_curved_5_transpaent_filled)
            Snackbar.make(domainsSpinnerFL, "Please select a domain", Snackbar.LENGTH_SHORT)
                .show()
            return false
        }else {
            domainsSpinnerFL.setBackgroundResource(R.drawable.input_bg_gray_borders_curved_5_transparent_filled)
        }
        project.proejctCategory = domainsAdapter.getList()

        input = aboutProjectET.text.toString().trim()
        if (input.isEmpty()){
            aboutProjectET.error = getString(R.string.this_field_is_required)
            aboutProjectET.requestFocus()
            return false
        }
        project.aboutProject = input

//        input = detailedDescriptionET.text.toString().trim()
//        if (input.isEmpty()){
//            detailedDescriptionET.error = getString(R.string.this_field_is_required)
//            detailedDescriptionET.requestFocus()
//            return false
//        }
        project.projectDescription = input

        input = expectedPriceET.text.toString().trim()
        if (input.isEmpty()){
            expectedPriceET.error = getString(R.string.this_field_is_required)
            expectedPriceET.requestFocus()
            return false
        }
        project.projectValue = convertEnglishNumbersToString(input)
        Log.i(TAG, "updateProject: domainsSpinner.selectedItem = ${project.projectValue}")

        project.numberOfOffers = input.toInt()

//        activityVM.projectMLD.value = project
        return true
    }

    private fun updateUI() {
        projectTitle.setText(project.projectTitle)
        // project.project_category
        this.domainsSpinner.setSelection(1, true)
        aboutProjectET.setText(project.aboutProject)
//        detailedDescriptionET.setText(project.projectDescription)
        expectedPriceET.setText(project.amount)
//        noQuotaionsET.setText(project.biddingCount.toString())

    }

    private fun initViews(createdView: View) {
        projectTitle = createdView.findViewById(R.id.projectTitle)
        ttl_char_TV = createdView.findViewById(R.id.ttl_char_TV)
        ttlMax = createdView.findViewById(R.id.ttlMax)
        ttl_char_TV.text = numberFormat.format(0)
        ttlMax.text = numberFormat.format(30)
        projectTitle.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val noChars = p0?.length?:0
                ttl_char_TV.text = numberFormat.format(noChars)
                if (noChars >=30){
                    ttl_char_TV.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    projectTitle.text?.delete(30, noChars)
                } else {
                    ttl_char_TV.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_font_gray))

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        textFile = createdView.findViewById(R.id.fileNameTextTV)
        uploadButton = createdView.findViewById(R.id.uploadBtn)
        specialityRV = createdView.findViewById(R.id.specialityRV)
        this.domainsSpinner = createdView.findViewById(R.id.domains_spinner)
        domainsSpinnerFL = createdView.findViewById(R.id.domainsSpinnerFL)
        aboutProjectET = createdView.findViewById(R.id.aboutProjectET)
        about_char_TV = createdView.findViewById(R.id.about_char_TV)
        aboutMax = createdView.findViewById(R.id.aboutMax)
        about_char_TV.text = numberFormat.format(0)
        aboutMax.text = numberFormat.format(300)
        aboutProjectET.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                about_char_TV.text = p0?.length.toString()

                val noChars = p0?.length?:0
                about_char_TV.text = numberFormat.format(noChars)
                if (noChars >=300){
                    about_char_TV.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    aboutProjectET.text?.delete(300, noChars)
                } else {
                    about_char_TV.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_font_gray))
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

//        detailedDescriptionET = createdView.findViewById(R.id.detailedDescriptionET)
        expectedPriceET = createdView.findViewById(R.id.expectedPriceET)
//        noQuotaionsET = createdView.findViewById(R.id.noQuotaionsET)
        nextBtn = createdView.findViewById(R.id.nextBtn)
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // An item was selected. You can retrieve the selected item using
//        parent.getItemAtPosition(pos)
        Log.w(javaClass.simpleName, "onItemSelected: parent.selectedItemPosition = ${parent?.selectedItemPosition}")
        if (parent?.selectedItemPosition != 0)
            domainsAdapter.addItem(parent?.selectedItem.toString().trim())
        

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
    override fun onResume() {
        super.onResume()
        val serviceLD =

            serviceVM.getAllServices()



        serviceLD.observe(viewLifecycleOwner, requestObserver)





    }


}
