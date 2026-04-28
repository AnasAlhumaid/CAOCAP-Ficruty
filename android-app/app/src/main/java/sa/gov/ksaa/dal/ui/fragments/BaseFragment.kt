package sa.gov.ksaa.dal.ui.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.Window
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.soundcloud.android.crop.Crop
import com.soundcloud.android.crop.CropImageActivity
import okhttp3.internal.http2.Http2Reader
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.MainActivity
import sa.gov.ksaa.dal.ui.fragments.profiles.UserProfileFragment
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.MainActivityVM
import sa.gov.ksaa.dal.ui.viewModels.UserVM
import java.io.File
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.logging.Handler
import kotlin.system.exitProcess


open class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {
    val activityVM: MainActivityVM by activityViewModels()
    val freelancerVM: FreelancersVM by viewModels()
    val userVM: UserVM by viewModels()

    lateinit var mainActivity: MainActivity
    lateinit var activityToolbar: Toolbar
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    protected var _user: NewUser? = null
    protected var currentFreelancer: NewFreelancer? = null
    protected var currentServices: List<ServicesModel>? = null
    private lateinit var cropIntent:Intent

    lateinit var appBarLayout: AppBarLayout
    lateinit var bottomNavigationView: BottomNavigationView

    //    lateinit var snackbar: Snackbar
    lateinit var activitySnackbar: Snackbar

    lateinit var DARK_RED_COLOR_STATE_LIST: ColorStateList
    lateinit var BLACK_COLOR_STATE_LIST: ColorStateList

    var incommingSimpleDateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.ENGLISH)
    var simpleDateFormatEn = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    val arabicLocale = Locale("ar", "SA")
    var dateFormatAr = DateFormat.getDateInstance(DateFormat.DEFAULT, arabicLocale)
    var incommingSimpleDateFormatAr = SimpleDateFormat("d MMM yyyy", arabicLocale)
    var dateFormatEn = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH)
    var numberFormat = NumberFormat.getInstance(arabicLocale)

    val calendar = Calendar.getInstance()



    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showAlertDialog(message = "هل ترغب في اغلاق التطبيق؟",
                title = "إغلاق التطبيق!",
                positiveText = "نعم",
                positiveOnClick = { dialogInterface, i ->
                    dialogInterface.dismiss()
                    exitProcess(0)
                },
                negativeText = "لا",
                negativeOnClick = { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
        }

    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        DARK_RED_COLOR_STATE_LIST = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.holo_red_dark
            )
        )
        BLACK_COLOR_STATE_LIST =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black))







        _user = activityVM.userMLD.value
        currentFreelancer = activityVM.currentFreelancerMLD.value

        if (_user == null) {
            val sharedPreferences = mainActivity.getPreferences(Context.MODE_PRIVATE)
            val userStr = sharedPreferences.getString("user", null)
            if (!userStr.isNullOrEmpty()) {
                try {
                    _user = Gson().fromJson(userStr, NewUser::class.java)
                    if (_user!!.isFreelancer() && currentFreelancer == null) {
                        // userId=5
                        val freelancerStr = mainActivity.getPreferences(Context.MODE_PRIVATE)
                            .getString("freelancer", null)
                        if (!freelancerStr.isNullOrEmpty()) {
                            currentFreelancer =
                                Gson().fromJson(freelancerStr, NewFreelancer::class.java)
                            activityVM.userMLD.postValue(_user)
                            activityVM.currentFreelancerMLD.postValue(currentFreelancer)
                        }
                    } else {
                        activityVM.userMLD.postValue(_user)
                    }
                } catch (_: Exception) {
                    sharedPreferences.edit()
                        .clear()
                        .apply()
                    _user = null
                    currentFreelancer = null
                    activityVM.userMLD.postValue(_user)
                    activityVM.currentFreelancerMLD.postValue(currentFreelancer)
                }

            }
        }
        Log.w(javaClass.simpleName, "onViewCreated: _user = $_user")
        Log.w(javaClass.simpleName, "onViewCreated: currentFreelancer = $currentFreelancer")


//        dateFormat = android.text.format.DateFormat.getDateFormat(requireContext().applicationContext)
//        if (toolbar != null){
//            NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
//        }


    }

    lateinit var activityProgressBar: ImageView
    open fun onActivityCreated() {

        appBarLayout = mainActivity.appBarLayout
        activityProgressBar = mainActivity.progress_bar
        activityToolbar = mainActivity.toolbar
        bottomNavigationView = mainActivity.bottomNavigationView
        activitySnackbar = mainActivity.snackbar

    }

    fun showProgressIndicator() {
        activityProgressBar.visibility = View.VISIBLE
    }

    fun hideProgressIndicator() {
        activityProgressBar.visibility = View.GONE
    }

    fun setTitle(title: String?) {
        activityToolbar.title = title
    }

    val onActivityCreatedObserver = object : DefaultLifecycleObserver {
        override fun onCreate(lifecycleOwner: LifecycleOwner) {
            super.onCreate(lifecycleOwner)
            onActivityCreated()


        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
        mainActivity.lifecycle.addObserver(onActivityCreatedObserver)
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity.lifecycle.removeObserver(onActivityCreatedObserver)
    }


    fun showAlertDialog(
        message: String?,
        title: String? = null,
        positiveText: String? = null,
        positiveOnClick: DialogInterface.OnClickListener? = null,
        negativeText: String? = null,
        negativeOnClick: DialogInterface.OnClickListener? = null
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .apply {
                setMessage(message)
//                setIcon(R.drawable.animated_check)
                if (title != null) setTitle(title)
                if (positiveOnClick == null)
                    setPositiveButton(
                        getString(R.string.ok)
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                else
                    setPositiveButton(
                        positiveText,
                        positiveOnClick
                    )
                if (negativeOnClick != null)
                    setNegativeButton(negativeText, negativeOnClick)
            }.create()
            .show()


//        val alertDialog: AlertDialog = mainActivity.let { fragmentActivity ->
//            val builder = AlertDialog.Builder(fragmentActivity)
//            builder.apply {
//                setMessage(message)
//                if (positiveOnClick == null)
//                    setPositiveButton(
//                        getString(R.string.ok)
//                    ) { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                else
//                    setPositiveButton(
//                        positiveText,
//                        positiveOnClick
//                    )
//                if (negativeOnClick != null)
//                    setNegativeButton(negativeText, negativeOnClick)
//            }
//            builder.create()
//        }
//        alertDialog.show()
    }

    fun togleVisisbility(targetView: View) {
        if (targetView.isVisible) targetView.visibility = View.GONE
        else targetView.visibility = View.VISIBLE
    }

    //    .observe(viewLifecycleOwner) {
//        getResponseData(it, { data ->
//
//        }) { errors ->
//
//        }
//    }
    fun <T> newHandleSuccessOrErrorResponse(
        res: sa.gov.ksaa.dal.data.webservices.newDal.NewResource<T>,
        onSuccess: ((data: T) -> Unit)? = null,
        onError: ((errors: Errors) -> Unit)? = null
    ) {
        when (res) {
            is sa.gov.ksaa.dal.data.webservices.newDal.NewResource.Success -> {
                Log.w(TAG, "getResponseData : Resource.Success : response = ${res}")
                mainActivity.progress_bar.visibility = View.INVISIBLE
                val data = res.data
//                if (data == null) {
//                    val errors = res.errors
//                    if (errors != null && onError != null) onError(errors)
//                } else if (onSuccess != null)
//                    onSuccess(data!!)

                if (data != null && onSuccess != null)
                    onSuccess(data)
            }

            is sa.gov.ksaa.dal.data.webservices.newDal.NewResource.Loading -> {
                mainActivity.progress_bar.visibility = View.VISIBLE
            }

            is sa.gov.ksaa.dal.data.webservices.newDal.NewResource.Error -> {
                mainActivity.progress_bar.visibility = View.INVISIBLE

//                Log.i(TAG, "getResponseData : Resource.Error : errors= ${res.response}")
                Log.w(TAG, "getResponseData : Resource.Error : message= ${res.message}")
//                val errors = res.response?.errors
//                if (errors != null && onError != null) onError(errors)
            }
        }
    }

    fun <T> handleSuccessOrErrorResponse(
        res: Resource<T>,
        onSuccess: ((data: T) -> Unit)? = null,
        onError: ((errors: Errors) -> Unit)? = null
    ) {
        when (res) {
            is Resource.Success -> {
                Log.w(TAG, "getResponseData : Resource.Success : response = ${res.response}")
                mainActivity.progress_bar.visibility = View.INVISIBLE
                val data = res.response!!.data
                if (data == null) {
                    val errors = res.response!!.errors
                    if (errors != null && onError != null) onError(errors)
                } else if (onSuccess != null)
                    onSuccess(data!!)
            }

            is Resource.Loading -> {
                mainActivity.progress_bar.visibility = View.VISIBLE
            }

            is Resource.Error -> {
                mainActivity.progress_bar.visibility = View.INVISIBLE
                Log.w(TAG, "getResponseData : Resource.Error : errors= ${res.response}")
                Log.w(TAG, "getResponseData : Resource.Error : message= ${res.message}")
                val errors = res.response?.errors
                if (errors != null && onError != null) onError(errors)
            }

            else -> {

            }
        }
    }

    fun togleVisisbilityAndGg(targetView: TextView, label: TextView) {
        if (targetView.isVisible) {
//            targetView.visibility = View.GONE
            collapse(targetView)
//            label.setBackgroundColor(resources.getColor(android.R.color.transparent))
            label.isSelected = false
        } else {
//            targetView.visibility = View.VISIBLE
            expand(targetView)
//            label.setBackgroundColor(resources.getColor(R.color.light_gray))
            label.isSelected = true
        }
    }

    fun togleVisisbilityAndGg(targetView: View, label: View) {
        if (targetView.isVisible) {
//            targetView.visibility = View.GONE
            collapse(targetView)
//            label.setBackgroundColor(resources.getColor(android.R.color.transparent))
            label.isSelected = false
        } else {
//            targetView.visibility = View.VISIBLE
            expand(targetView)
//            label.setBackgroundColor(resources.getColor(R.color.light_gray))
            label.isSelected = true
        }
    }


    fun showMaterialDatePicker(
        message: String? = null,
        selectableStartDate: Long? = null,
        selectableEndDate: Long? = null,
        openAtDate: Long? = null,
        dateValidator: CalendarConstraints.DateValidator? = null,
        onPositiveButtonClickListener: MaterialPickerOnPositiveButtonClickListener<Long>? = null,
        onNegativeButtonClickListener: View.OnClickListener? = null,
        onCancelListener: DialogInterface.OnCancelListener? = null,
        onDismissListener: DialogInterface.OnDismissListener? = null
    ) {
//        // To constrain the calendar to the beginning to the end of this year:
//        val today = MaterialDatePicker.todayInUtcMilliseconds()
//        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//        calendar.timeInMillis = today
//        calendar[Calendar.MONTH] = Calendar.JANUARY
//        val janThisYear = calendar.timeInMillis
//        calendar.timeInMillis = today
//        calendar[Calendar.MONTH] = Calendar.DECEMBER
//        val decThisYear = calendar.timeInMillis
//        // Build constraints.
//        var constraintsBuilder =
//            CalendarConstraints.Builder()
//                .setStart(janThisYear)
//                .setEnd(decThisYear)
//
//        // To open picker at a default month
//        calendar[Calendar.MONTH] = Calendar.FEBRUARY
//        val february = calendar.timeInMillis
//        constraintsBuilder =
//            CalendarConstraints.Builder()
//                .setOpenAt(february)
//
//        // Makes only dates from today forward selectable.
//        constraintsBuilder =
//            CalendarConstraints.Builder()
//                .setValidator(DateValidatorPointForward.now)
//        // Makes only dates from February forward selectable.
//        constraintsBuilder =
//            CalendarConstraints.Builder()
//                .setValidator(DateValidatorPointForward.from(february))

// Build constraints.
        val constraintsBuilder =
            CalendarConstraints.Builder()
        if (selectableStartDate != null)
            constraintsBuilder.setStart(selectableStartDate)
        if (selectableEndDate != null)
            constraintsBuilder.setEnd(selectableEndDate)
        if (openAtDate != null)
            constraintsBuilder.setOpenAt(openAtDate)
        if (dateValidator != null)
            constraintsBuilder.setValidator(dateValidator)

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(message ?: getString(R.string.please_select_a_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        if (onPositiveButtonClickListener != null) picker.addOnPositiveButtonClickListener(
            onPositiveButtonClickListener
        )
        if (onNegativeButtonClickListener != null) picker.addOnNegativeButtonClickListener(
            onNegativeButtonClickListener
        )
        if (onCancelListener != null) picker.addOnCancelListener(onCancelListener)
        if (onDismissListener != null) picker.addOnDismissListener(onDismissListener)
        picker.show(mainActivity.supportFragmentManager, "DatePicker")
    }

    fun showRangeDatePicker() {
        MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select date")
            .setSelection(
                Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()
            .show(mainActivity.supportFragmentManager, "RangeDatePicker");
    }


    fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun notImplemented() {
        Snackbar.make(requireView(), "Not Implemented Yet", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityProgressBar.visibility = View.GONE
    }

//    fun setRoundedStartIcon(textView: TextView){
//
////        val drawable = ContextCompat.getDrawable(requireContext(), iconResId)
//
//        val et_iconLayerDrawable = (ContextCompat.getDrawable(requireContext(), R.drawable.et_icon) as LayerDrawable?)!!
//        var startDrawable = textView.compoundDrawablesRelative[0]
//        et_iconLayerDrawable!!.setDrawableByLayerId(R.id.incideIcon, startDrawable)
//        startDrawable = et_iconLayerDrawable
//
//        textView.setCompoundDrawables(startDrawable, null, null, null)
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openFile(
        pickerInitialUri: Uri?,
        fileType: String,
        activityResultLauncher: ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = fileType
            flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            if (pickerInitialUri != null) putExtra(
                DocumentsContract.EXTRA_INITIAL_URI,
                pickerInitialUri
            )
        }

//        startActivityForResult(intent, PICK_PDF_FILE)
        activityResultLauncher.launch(intent)
    }


    @SuppressLint("Recycle")
    fun gettingFile(activityResult: ActivityResult, fileListener: FileListener) {
        val uri = activityResult.data?.data
        if (uri == null) {
            Toast.makeText(
                requireContext(),
                R.string.something_went_wrong_please_try_again_later,
                Toast.LENGTH_SHORT
            ).show()
            Log.w(TAG, "gettingFile: uri == null")
        } else {
            val contentResolver = requireActivity().contentResolver
            contentResolver?.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE,
                MediaStore.Images.Media.DATA), null, null, null)
            if (cursor == null) {
                Toast.makeText(
                    requireContext(),
                    R.string.something_went_wrong_please_try_again_later,
                    Toast.LENGTH_SHORT
                ).show()
                Log.w(TAG, "gettingFile: uri == null")
            } else {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                cursor.moveToFirst()

                var realpath: String? = null
                realpath = try {
                    val imageDataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.getString(imageDataIndex)
                } catch (e: Exception){
                    null
                }
                fileListener.onFileChosen(
                    MyFile(
                        cursor.getString(nameIndex),
                        cursor.getLong(sizeIndex),
                        contentResolver.openInputStream(uri),
                        contentResolver.getType(uri),
                        uri,
                        realpath
                    )
                )
            }
            cursor?.close()
        }

    }

    @SuppressLint("Recycle")
    fun uriToMyFile(uri: Uri): MyFile?{
        val contentResolver = requireActivity().contentResolver
        contentResolver?.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            Toast.makeText(
                requireContext(),
                R.string.something_went_wrong_please_try_again_later,
                Toast.LENGTH_SHORT
            ).show()
            Log.w(TAG, "gettingFile: uri == null")
        } else {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            return MyFile(
                cursor.getString(nameIndex),
                cursor.getLong(sizeIndex),
                contentResolver.openInputStream(uri),
                contentResolver.getType(uri),
                uri,
                null
            )

        }
        cursor?.close()
        return null

    }


     fun cropImages(uri:Uri?){
        /**set crop image*/
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra("crop",true)
            cropIntent.putExtra("outputX",180)
            cropIntent.putExtra("outputY",180)
            cropIntent.putExtra("aspectX",3)
            cropIntent.putExtra("aspectY",4)
            cropIntent.putExtra("scaleUpIfNeeded",true)
            cropIntent.putExtra("return-data",true)
            startActivityForResult(cropIntent,1)

        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
        }
    }

    @SuppressLint("Recycle")
    fun getImageFile(uri: Uri?, fileListener: FileListener) {
        if (uri == null) {
            Toast.makeText(
                requireContext(),
                R.string.something_went_wrong_please_try_again_later,
                Toast.LENGTH_SHORT
            ).show()
            Log.w(TAG, "gettingFile: uri == null")
            Log.w(javaClass.simpleName, "updateClientPhoto: uri == nulll")
        } else {

            val contentResolver = requireActivity().applicationContext.contentResolver

//            val docId = DocumentsContract.getDocumentId(uri)
//            val docIdArr = docId.split(":")
//            val type = docIdArr[0]

            val selection = "_id=?"
//            val selectionArgs = arrayOf(docIdArr[1] )

            if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                // Perform content provider query if it's a content URI
                val cursor = contentResolver.query(
                    uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Images.Media.DATA),

                null, null, null)
                // ... process cursor
            }

            val cursor = contentResolver.query(
                uri,
                arrayOf(
                    OpenableColumns.DISPLAY_NAME,
                    OpenableColumns.SIZE,
                    MediaStore.Images.Media.DATA
                ), null, null, null
            )
            // arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE,
            //                MediaStore.Images.Media.DATA)
            //MediaStore.Images.ImageColumns.DATA)
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
            }

            if (cursor == null) {
                Toast.makeText(
                    requireContext(),
                    "R.string.something_went_wrong_please_try_again_later",
                    Toast.LENGTH_SHORT
                ).show()
                Log.w(TAG, "gettingFile: --------uri == null")
            } else {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val imagePathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                Log.w(TAG, "getImageFile: imagePath = ${cursor.getString(imagePathIndex)}")
                fileListener.onFileChosen(
                    MyFile(
                        cursor.getString(nameIndex),
                        cursor.getLong(sizeIndex),
                        contentResolver.openInputStream(uri),
                        contentResolver.getType(uri),
                        uri,
                        cursor.getString(imagePathIndex)
                    )
                )
            }
            cursor?.close()
        }

    }

    @FunctionalInterface
    interface FileListener {
        fun onFileChosen(myFile: MyFile)
    }

    val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //  you will get result here in result.data
        }
    }

    companion object {
        // Request code for selecting a PDF document.
        const val PICK_PDF_FILE = 2
        const val PDF_TYPE = "application/pdf"
        const val IMAGES_TYPE = "image/*"
        const val All_MIME_TYPES = "*/*"
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val REQUEST_CODE_PICK_IMAGE = 102
    }

    fun isUserClient(userType: String?): Boolean {
        if (userType == null)
            return false
        if (userType.equals(NewUser.INDIVIDUAL_CLIENT_USER_TYPE, true))
            return true
        if (userType.equals(NewUser.ORG_CLIENT_USER_TYPE, true))
            return true
        if (userType.equals(NewUser.CLIENT_USER_TYPE, true))
            return true
        return false
    }

    fun isUserFreelancer(userType: String?): Boolean {
        val isFreelancer = userType != null && (userType.equals(NewUser.FREELANCER_USER_TYPE, true))
        Log.w(javaClass.simpleName, "isFreelancer: isFreelancer = $isFreelancer")
        return isFreelancer
    }

    fun isUserFemale(gender: String?): Boolean {
        if (gender == null)
            return false
        return gender == NewUser.FEMALE_USER_GENDER || gender == NewUser.FEMALE__USER_GENDER
    }
    fun isClientCompany(clientType: String?): Boolean {
        if (clientType == null)
            return false
        return clientType == NewUser.ORG_CLIENT_USER_TYPE
    }


    fun setProfileImage(uri: Any?, imageView: ImageView, userType: String?, userGender: String?, clientType: String?=null) {
        val imageId = if (isUserClient(userType) && isClientCompany(clientType)) R.drawable.client_cercular
        else if (isUserFreelancer(userType) || !isClientCompany(clientType)) {
            if (isUserFemale(userGender)) R.drawable.freelancer_female_cercular_100
            else R.drawable.freelancer_male_cercular_100
        } else R.drawable.freelancer_male_cercular_100

        if (uri == null || (uri is String && uri.endsWith(".tmp")))
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        else imageView.imageTintList = null

        val url = if (uri is String? && uri != null) {
            uri.replace("\\", "", true)
        } else uri

        Log.w(TAG, "setProfileImage: url = $url")
        Glide.with(this)
            .load(uri)
            .placeholder(imageId)
            .centerCrop()

            .transform(
                CenterCrop(),
                CircleCrop(),

            )//  RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius))
            .into(imageView)
    }

    fun setOtherUserImage(uri: Any?, imageView: ImageView, userType: String, gender: String?) {
        val imageId = if (isUserClient(userType)) R.drawable.client_cercular
        else if (isUserFemale(gender)) R.drawable.freelancer_female_cercular_100
        else R.drawable.freelancer_male_cercular_100

        if (uri == null || (uri is String && uri.endsWith(".tmp")))
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.accent))
        else imageView.imageTintList = null

        Glide.with(this)
            .load(uri)
            .placeholder(imageId)
            .centerCrop()
            .transform(
                CenterCrop(),
                CircleCrop()
            )//  RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius))
            .into(imageView)
    }


     fun showCustomAlert(message: String?){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_done_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvMassage : TextView = dialog.findViewById(R.id.TvMassage)
        tvMassage.text = message
         val handler = android.os.Handler()
         val runnable = Runnable {
             dialog.dismiss()
         }
         handler.postDelayed(runnable, 2000)
        dialog.show()

    }

    fun convertArabicNumbersToString(text: String?): String {
        val arabicToWesternMap = mapOf(
            '0' to '٠' , '1' to '١'   , '2' to  '٢','3'  to '٣', '4' to  '٤',
            '5' to  '٥', '6'  to '٦', '7' to  '٧', '8' to  '٨','9' to  '٩'
        )
        val builder = text?.let { StringBuilder(it.length) }
        if (text != null) {
            for (char in text) {
                if (builder != null) {
                    builder.append(arabicToWesternMap[char] ?: char)
                }
            }
        }
        return builder.toString()
    }
    fun convertEnglishNumbersToString(text: String?): String {
        val arabicToEnglishMap = mapOf(
            '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
            '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
        )
        val builder = text?.let { StringBuilder(it.length) }
        if (text != null) {
            for (char in text) {
                if (builder != null) {
                    builder.append(arabicToEnglishMap[char] ?: char)
                }
            }
        }
        return builder.toString()
    }
    fun EditText.setArabicNumberFormatter() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val formattedNumber = convertArabicNumbersToString(s.toString())
                this@setArabicNumberFormatter.removeTextChangedListener(this)
                this@setArabicNumberFormatter.setText(formattedNumber)
                this@setArabicNumberFormatter.setSelection(formattedNumber.length)
                this@setArabicNumberFormatter.addTextChangedListener(this)
            }
        })
    }

    fun convertDateFormat(dateString: String): String {
        val inputFormat = SimpleDateFormat("d MMM yyyy", arabicLocale)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH)

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }
}