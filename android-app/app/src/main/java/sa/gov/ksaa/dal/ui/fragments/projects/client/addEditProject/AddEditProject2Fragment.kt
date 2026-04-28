package sa.gov.ksaa.dal.ui.fragments.projects.client.addEditProject

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.models.ProjectsFilter
import sa.gov.ksaa.dal.data.webservices.dal.requests.FreelancersInvitation
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.AddProjectFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.adapters.CertificationsRV_Adapter
import sa.gov.ksaa.dal.ui.adapters.DomainsListAdapter
import sa.gov.ksaa.dal.ui.adapters.FreelancersSelectionRV_Adapter
import sa.gov.ksaa.dal.ui.adapters.InvitedFreelancer_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.profiles.safeNavigateWithArgs
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment

import sa.gov.ksaa.dal.ui.fragments.projects.explore.formatLongToDateString
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditProject2Fragment : BaseFragment(R.layout.fragment_projects_add_edit2),DatePickerDialog.OnDateSetListener{

    companion object {
        const val projectKey = "project"
    }

    lateinit var noQuotaionsET: EditText
//    lateinit var expectedTimeET: EditText
    lateinit var durationSpnrFL: FrameLayout
    lateinit var durationSpnr: Spinner
    lateinit var freelancerLevelSpnr: Spinner
    lateinit var levelSpnrFL: FrameLayout
//    lateinit var freelancerLevelsRV: RecyclerView
    lateinit var levelsRV_Adapter : DomainsListAdapter

    lateinit var pubTypeRG: RadioGroup
    lateinit var quotationsDeadlineET: EditText
    lateinit var nextBtn: Button
    lateinit var prevBtn: Button
    lateinit var publicRadio: MaterialRadioButton
    lateinit var privateRadio: MaterialRadioButton
    lateinit var addFreelancerBtn : Button
    lateinit var recyclerView : RecyclerView
    lateinit var freelancersRVadapter: InvitedFreelancer_Adapter
    private lateinit var fileAdd : AddProjectFile

    val tomorrowCalendar = Calendar.getInstance()
    val projectsVM: ProjectsVM by viewModels()



    lateinit var serviceList: List<ServicesModel>
    lateinit var project: NewProject
    var selectedDate : Long? = 0L

    lateinit var selectedFreelancer: Set<Int>


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)

//        project = activityVM.projectMLD.value
//        updateUi()

        addFreelancerBtn.visibility = View.GONE
        recyclerView.visibility = View.GONE

        fileAdd = activityVM.addProjectFileMLD.value!!

        noQuotaionsET.setArabicNumberFormatter()

        pubTypeRG.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.publicRadio -> {
                    nextBtn.text = "إضافة"
                    addFreelancerBtn.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }
                R.id.privateRadio -> {
                    addFreelancerBtn.visibility = View.VISIBLE
                    recyclerView.visibility = View.VISIBLE
                    nextBtn.text = "التالي"}
//                else -> true
            }
        }

        recyclerView.layoutManager = GridLayoutManager(context, 1)
        freelancersRVadapter = InvitedFreelancer_Adapter(mutableListOf(), _user, requireContext())
        recyclerView.adapter = freelancersRVadapter

addFreelancerBtn.setOnClickListener {
//    activityVM.newProjectMLD.postValue(project)


    val action = AddEditProject2FragmentDirections.actionAddEditProject2FragmentToFreelancersToBeInvitedFragment()
    findNavController().safeNavigateWithArgs(action, null)

//    findNavController().navigate(R.id.action_addEditProject2Fragment_to_freelancersToBeInvitedFragment)
}
        nextBtn.setOnClickListener {


            if (isValidProject()) {
//                project.status = Project.newProjectStatus


                if (project.request == NewProject.GENERAL_REQUEST_TYPE) {

                    val output = project.proejctCategory?.joinToString(separator = ",") { "\"$it\"" }

                    //    addProject?user_id=1&projectTitle=مشروع ترجمة جديد&category=التدقيق&aboutProject=نبذة&
//    descriptionofProject=وصف&projectValue=555&numberOfOffers=7&expectedTime=أسبوع 1&freelancerLevel=نشط&
//    durationOfOffer=2023-10-14&request=general&freelancerIds=
                    val params = mutableMapOf(
                        "user_id" to project.userId.toString(),
                        "projectTitle" to project.projectTitle!!,
                        "category" to "${output}",
                        "aboutProject" to project.aboutProject!!,
                        "descriptionofProject" to "لا يوجد ",
                        "projectValue" to project.projectValue!!,
                        "numberOfOffers" to convertEnglishNumbersToString(project.numberOfOffers.toString()) ,
                        "expectedTime" to project.expectedTime.toString(),
                        "freelancerLevel" to project.freelancerLevel!!,
                        "durationOfOffer" to project.durationOfOffer.toString(),
                        "request" to project.request!!,
                        "freelancerIds" to ""
                    )
                    Log.w(TAG, "onViewCreated: params = $params")
                    projectsVM.addNewProject(params,fileAdd.addFile)
                        .observe(viewLifecycleOwner) {
                            newHandleSuccessOrErrorResponse(it,
                                onSuccess = { newProject ->
                                    project.clientName = project.clientName ?: _user!!.getFullName()
                                    project.createdDate = project.createdDate ?: Date()


                                    project = newProject
                                    activityVM.newProjectMLD.postValue(project)

                                    Snackbar.make(
                                        requireView(),
                                        "تم إنشاء المشروع بنجاح",
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                    if (view != null)
                                        viewLifecycleOwner.lifecycleScope.launch {
                                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                                                findNavController().navigate(
//                                                    R.id.action_addEditProject2Fragment_to_projectDetailsFragment,
//                                                    bundleOf(ProjectDetailsFragment.projectIdArgKey to project.id)
//                                                )


                                                val action = AddEditProject2FragmentDirections.actionAddEditProject2FragmentToProjectDetailsFragment(project.id!!)
                                                findNavController().safeNavigateWithArgs(action,null)
                                            }
                                        }

                                },
                                onError = {
                                    showAlertDialog(
                                        message = it.message
                                            ?: getString(R.string.something_went_wrong_please_try_again_later)
                                    )
                                    Log.i(
                                        TAG,
                                        "onViewCreated: vm.updateProjectById: onError : errorsList =  ${it.errorsList.toString()}"
                                    )
                                })
                        }
                } else {

                    val freelancerInvite = activityVM.invitedFreelancer.value

                    if (freelancerInvite != null){
                    val output =
                        project.proejctCategory?.joinToString(separator = ",") { "\"$it\"" }

                    //    addProject?user_id=1&projectTitle=مشروع ترجمة جديد&category=التدقيق&aboutProject=نبذة&
//    descriptionofProject=وصف&projectValue=555&numberOfOffers=7&expectedTime=أسبوع 1&freelancerLevel=نشط&
//    durationOfOffer=2023-10-14&request=general&freelancerIds=
                    val params = mutableMapOf(
                        "user_id" to project.userId.toString(),
                        "projectTitle" to project.projectTitle!!,
                        "category" to "${output}",
                        "aboutProject" to project.aboutProject!!,
                        "descriptionofProject" to "لا يوجد ",
                        "projectValue" to project.projectValue!!,
                        "numberOfOffers" to convertEnglishNumbersToString(project.numberOfOffers.toString()),
                        "expectedTime" to project.expectedTime.toString(),
                        "freelancerLevel" to project.freelancerLevel!!,
                        "durationOfOffer" to project.durationOfOffer.toString(),
                        "request" to project.request!!,
                        "freelancerIds" to freelancerInvite.freelancers_ids.toString()
                        .removeSurrounding("[", "]")


                    )
                    Log.w(TAG, "onViewCreated: params = $params")
                    projectsVM.addNewProject(params,project.addFile)
                        .observe(viewLifecycleOwner) {
                            newHandleSuccessOrErrorResponse(it,
                                onSuccess = { newProject ->
                                    project.clientName = project.clientName ?: _user!!.getFullName()
                                    project.createdDate = project.createdDate ?: Date()


                                    project = newProject
                                    activityVM.newProjectMLD.postValue(project)
                                    activityVM.invitedFreelancer.postValue(null)

                                    Snackbar.make(
                                        requireView(),
                                        "تم إنشاء المشروع بنجاح",
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                    if (view != null)
                                        viewLifecycleOwner.lifecycleScope.launch {
                                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                                                findNavController().navigate(
//                                                    R.id.action_addEditProject2Fragment_to_projectDetailsFragment,
//                                                    bundleOf(ProjectDetailsFragment.projectIdArgKey to project.id)
//                                                )


                                                val action =
                                                    AddEditProject2FragmentDirections.actionAddEditProject2FragmentToProjectDetailsFragment(
                                                        project.id!!
                                                    )
                                                findNavController().safeNavigateWithArgs(
                                                    action,
                                                    null
                                                )
                                            }
                                        }

                                },
                                onError = {
                                    showAlertDialog(
                                        message = it.message
                                            ?: getString(R.string.something_went_wrong_please_try_again_later)
                                    )
                                    Log.i(
                                        TAG,
                                        "onViewCreated: vm.updateProjectById: onError : errorsList =  ${it.errorsList.toString()}"
                                    )
                                })
                        }
                }
                }
//                    vm.updateProjectById(project!!)
//                        .observe(viewLifecycleOwner, Observer {
//                            handleSuccessOrErrorResponse(it,
//                                onSuccess = {
//                                    showAlertDialog(getString(R.string.the_project_is_added_successfully),
//                                        getString(R.string.ok), positiveOnClick = {
//                                            dialogInterface, _ ->
//                                            run {
//                                                mainActivity.appBarLayout.visibility = View.GONE
//                                                findNavController().popBackStack(R.id.userProfileFragment, false)
//                                                dialogInterface.dismiss()
//                                            }
//                                        })
//
//                                },
//                                onError = {
//                                    showAlertDialog(
//                                        message = it?.message
//                                            ?: getString(R.string.something_went_wrong_please_try_again_later)
//                                    )
//                                    Log.i(
//                                        TAG,
//                                        "onViewCreated: vm.updateProjectById: onError : errorsList =  ${it?.errorsList.toString()}"
//                                    )
//                                })
//                        })
            }


        }
        prevBtn.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    lateinit var input: String

    @SuppressLint("SimpleDateFormat")
    private fun isValidProject(): Boolean {
        project = activityVM.newProjectMLD.value!!
        input = noQuotaionsET.text.toString().trim()
        if (input == "") {

            project.numberOfOffers = 100
        }else {
            project.numberOfOffers = input.toInt()
        }


        if (durationSpnr.selectedItemPosition == 0){
            durationSpnrFL.setBackgroundResource(R.drawable.input_bg_red_borders_curved_5_transpaent_filled)
            activitySnackbar.setText("يجب تحديد الوقت المتوقع لإنجاز المشروع")
                .show()
            return false
        } else {
            durationSpnrFL.setBackgroundResource(R.drawable.input_bg_gray_borders_curved_5_transparent_filled)
            when ( durationSpnr.selectedItemPosition){
                1->{
                    project.expectedTime = "1-3 أيام"
                }
                2->{
                    project.expectedTime = "1-5 أيام"
                }
                3->{
                    project.expectedTime = "1 أسبوع"
                }
                4->{
                    project.expectedTime = "1-3 أسابيع"
                }
                5->{
                    project.expectedTime = "1 شهر"
                }
                6->{
                    project.expectedTime = "2-3 أشهر"
                }
                7->{
                    project.expectedTime = "6 أشهر"
                }
                8->{
                    project.expectedTime = "أقل من سنة"
                }

            }


        }

//        if (levelsRV_Adapter.getList().isEmpty()){
//            levelSpnrFL.setBackgroundResource(R.drawable.input_bg_red_borders_curved_5_transpaent_filled)
//            Snackbar.make(
//                freelancerLevelSpnr,
//                "يجب إضافة تصنيف لمقدم الخدمة",
//                Snackbar.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            levelSpnrFL.setBackgroundResource(R.drawable.input_bg_gray_borders_curved_5_transparent_filled)
//            project.freelancerLevel = freelancerLevelSpnr.selectedItem.toString()
//        }
        if (freelancerLevelSpnr.selectedItemPosition == 0) {
            Snackbar.make(
                freelancerLevelSpnr,
                "يجب إضافة تصنيف لمقدم الخدمة",
                Snackbar.LENGTH_SHORT
            ).show()
            return false
        } else {

            when (freelancerLevelSpnr.selectedItemPosition){
                1 -> {
                    project.freelancerLevel = "نشط"
                    Log.w(TAG, "onViewCreated: params = ${project.freelancerLevel}")
                }
                2 -> {
                    project.freelancerLevel = "متمرس"
                    Log.w(TAG, "onViewCreated: params = ${project.freelancerLevel}")
                }
                3 ->{
                    project.freelancerLevel = "متميز"
                    Log.w(TAG, "onViewCreated: params = ${project.freelancerLevel}")
                }
                4 -> {
                    project.freelancerLevel = "محترف"
                    Log.w(TAG, "onViewCreated: params = ${project.freelancerLevel}")
                }
            }

        }

        project.request = when (pubTypeRG.checkedRadioButtonId) {
            R.id.publicRadio -> {
                addFreelancerBtn.visibility = View.GONE
                NewProject.GENERAL_REQUEST_TYPE }
            R.id.privateRadio -> {
                addFreelancerBtn.visibility = View.VISIBLE
                NewProject.PRIVATE_REQUEST_TYPE}
            else -> {
                addFreelancerBtn.visibility = View.GONE
                NewProject.GENERAL_REQUEST_TYPE }
        }

        input = quotationsDeadlineET.text.toString().trim()
        if (input.isEmpty()) {
            quotationsDeadlineET.error = getString(R.string.this_field_is_required)
            quotationsDeadlineET.requestFocus()
            return false
        }else{
            project.durationOfOffer = formatLongToDateString(selectedDate ?: 0L)
        }



        return true
    }

    lateinit var expectedTime: Date
    private fun initViews(createdView: View) {
        noQuotaionsET = createdView.findViewById(R.id.noQuotaionsET)
        durationSpnrFL = createdView.findViewById(R.id.durationSpnrFL)
        durationSpnr = createdView.findViewById(R.id.durationSpnr)
        addFreelancerBtn = createdView.findViewById(R.id.addFreelancer)
        recyclerView = createdView.findViewById(R.id.recyclerViewFreelancer)



//        expectedTimeET.setOnClickListener {
//            showMaterialDatePicker(getString(R.string.please_select_the_deadline_for_receiving_bids), { selection ->
//                expectedTime = Date(selection)
//                expectedTimeET.setText(arabicDateFormat.format(expectedTime))
//            })
//        }
        incommingSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//        expectedTimeET.setOnFocusChangeListener { view, b ->
//            if (b) {
//                showMaterialDatePicker(
//                    message = getString(R.string.please_select_the_deadline_for_receiving_bids),
//                    dateValidator = DateValidatorPointForward.now(),
//                    onPositiveButtonClickListener = { selection ->
//                        expectedTime = Date(selection)
//                        expectedTimeET.setText(incommingSimpleDateFormat.format(expectedTime))
//                    })
//            }
//
//        }

        freelancerLevelSpnr = createdView.findViewById(R.id.freelancerLevelSpnr)

        levelSpnrFL = createdView.findViewById(R.id.levelSpnrFL)
//        freelancerLevelsRV = createdView.findViewById(R.id.freelancerLevelsRV)
//        levelsRV_Adapter = DomainsListAdapter(mutableListOf())
//        freelancerLevelsRV.adapter = levelsRV_Adapter

        pubTypeRG = createdView.findViewById(R.id.pubTypeRG)
        quotationsDeadlineET = createdView.findViewById(R.id.quotationsDeadlineET)

        val tomorrowCalendar = Calendar.getInstance()
        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrowInMillis = tomorrowCalendar.timeInMillis
        quotationsDeadlineET.setOnClickListener {
            showDatePicker()
        }
//        quotationsDeadlineET.setOnClickListener {
//            showMaterialDatePicker(getString(R.string.please_select_the_deadline_for_receiving_bids), { selection ->
//                quotationsDeadlineET.setText(dateFormat.format(Date(selection)))
//            })
//        }
//        quotationsDeadlineET.setOnClickListener {
//            showMaterialDatePicker(getString(R.string.please_select_the_deadline_for_receiving_bids), { selection ->
//                quotationsDeadlineET.setText(arabicDateFormat.format(Date(selection)))
//            })
//        }
        publicRadio = createdView.findViewById(R.id.publicRadio)
        privateRadio = createdView.findViewById(R.id.privateRadio)

        nextBtn = createdView.findViewById(R.id.nextBtn)
        prevBtn = createdView.findViewById(R.id.prevBtn)



    }

    private fun showDatePicker() {

        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1)
        val popDate =  DatePickerDialog(requireContext(),this, tomorrowCalendar.get(Calendar.YEAR),
            tomorrowCalendar.get(Calendar.MONTH),
            tomorrowCalendar.get(Calendar.DAY_OF_MONTH)



        )
        popDate.show()
        popDate.datePicker.minDate = tomorrowCalendar.timeInMillis
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.e("calender","$year, --- $month")

        tomorrowCalendar.set(year,month,dayOfMonth)
        onDisplay(tomorrowCalendar.timeInMillis)
    }
    private fun onDisplay(timeStrmp:Long){
        val dateFormatter = dateFormatAr
        val simpleDateFormatEn = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        quotationsDeadlineET.setText(incommingSimpleDateFormatAr.format(timeStrmp))

selectedDate = timeStrmp


    }



    override fun onResume() {
        super.onResume()
        val freelancerInvite = activityVM.invitedFreelancer.value

        if (freelancerInvite != null) {
freelancersRVadapter.clearData()

            for (i in freelancerInvite.freelancers_userIds!!) {

                freelancerVM.getFreelancersByUserId(mapOf("userId" to i.toString()))
                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { freelancersList ->



                            val free = freelancersList[0]

                            freelancersRVadapter.addList(free)
                        })
                    }
            }

            selectedFreelancer = freelancerInvite?.freelancers_ids!!
        }





    }





}