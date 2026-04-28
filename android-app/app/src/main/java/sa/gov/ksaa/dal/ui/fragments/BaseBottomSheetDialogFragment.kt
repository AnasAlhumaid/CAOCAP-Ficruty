package sa.gov.ksaa.dal.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.appcompat.widget.Toolbar
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.ui.MainActivity
import sa.gov.ksaa.dal.ui.viewModels.MainActivityVM

open class BaseBottomSheetDialogFragment(layoutId: Int): BottomSheetDialogFragment(layoutId) {
    val activityVM: MainActivityVM by activityViewModels()
    lateinit var mainActivity: MainActivity
    var toolbar: Toolbar? = null
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

//        if (toolbar != null){
//            NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
//        }


    }

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
    fun showAlertDialog(
        message: String,
        positiveText: String? = null,
        positiveOnClick: DialogInterface.OnClickListener? = null,
        negativeText: String? = null,
        negativeOnClick: DialogInterface.OnClickListener? = null
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .apply {
                setMessage(message)
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
            }.
            create()
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

    fun <T> handleSuccessOrErrorResponse(
        res: Resource<T>,
        onSuccess: ((data: T) -> Unit)? = null,
        onError: ((errors: Errors) -> Unit)? = null
    ) {
        when (res) {
            is Resource.Success -> {
                Log.i(TAG, "getResponseData : Resource.Success : response = ${res.response}")
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
                Log.i(TAG, "getResponseData : Resource.Error : errors= ${res.response}")
                Log.i(TAG, "getResponseData : Resource.Error : message= ${res.message}")
                val errors = res.response?.errors
                if (errors != null && onError != null) onError(errors)
            }

            else -> {}
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
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
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

    fun showDatePicker() {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.please_select_a_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()
            .show(mainActivity.supportFragmentManager, "DatePicker");
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


}