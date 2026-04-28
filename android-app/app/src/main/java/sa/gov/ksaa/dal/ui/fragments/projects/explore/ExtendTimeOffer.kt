package sa.gov.ksaa.dal.ui.fragments.projects.explore

import android.app.DatePickerDialog
import android.icu.util.ULocale
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import sa.gov.ksaa.dal.ui.fragments.BaseFragment

import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM
import java.lang.Error
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ExtendTimeOffer : BaseFragment(R.layout.fragment_extent_time_offring),
DatePickerDialog.OnDateSetListener
{

    private lateinit var project: NewProject
    lateinit var titleProject: TextView
    lateinit var clientName: TextView
    lateinit var timeProject: TextView
    lateinit var userIV : ImageView
    lateinit var timeEdit: TextView
    lateinit var expectedTime: Date
    lateinit var editBtn : Button
    val tomorrowCalendar = Calendar.getInstance()
    val vm : ProjectsVM by viewModels()

    var selected : Long? = 1234567890123456L

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)

        appBarLayout.visibility = View.VISIBLE

        this@ExtendTimeOffer.project = activityVM.newProjectMLD.value!!

        titleProject.text = project.projectTitle
        clientName.text =   project.clientName

        setOtherUserImage(project.image, userIV, NewUser.CLIENT_USER_TYPE, project.gender)

//        val inputDate = LocalDate.parse(project.durationOfProject)

        // Define the desired output format
        val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("ar", "SA"))

        // Format the date using the desired format
//        val formattedDate = inputDate.format(outputFormat)
//            }\




        editBtn.setOnClickListener {

            Log.e("calender", formatLongToDateString(selected ?: 12L))
if (selected != null) {
    vm.editeDuration(mapOf("projectId" to project.id.toString() ,"duration" to  formatLongToDateString(selected!!)))
        .observe(viewLifecycleOwner){
            newHandleSuccessOrErrorResponse(it ,{it
                Snackbar.make(
                    requireView(),
                    "تم تعديل الوقت بنجاح",
                    Snackbar.LENGTH_SHORT
                ).show()
            })
        }
    }


        }



    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews(createdView: View) {
        titleProject = createdView.findViewById(R.id.ProjectName)
        timeProject = createdView.findViewById(R.id.RecentDate)
        timeEdit = createdView.findViewById(R.id.quotationsDeadlineET)
        editBtn = createdView.findViewById(R.id.EditTimeBtn)
        clientName = createdView.findViewById(R.id.clientNameTV)
        userIV = createdView.findViewById(R.id.userIV)

//

        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1) // Move to tomorrow
        val tomorrowInMillis = tomorrowCalendar.timeInMillis

        timeEdit.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {

      val popDate =  DatePickerDialog(requireContext(),this,
          tomorrowCalendar.get(Calendar.YEAR),
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
        timeEdit.setText(incommingSimpleDateFormatAr.format(timeStrmp))
        selected = timeStrmp




    }






}
fun formatLongToDateString(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH)
    val date = Date(timestamp)
    return dateFormat.format(date)
}