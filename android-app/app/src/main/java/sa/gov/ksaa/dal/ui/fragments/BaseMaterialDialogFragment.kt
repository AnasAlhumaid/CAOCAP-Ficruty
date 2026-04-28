package sa.gov.ksaa.dal.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.MainActivity
import sa.gov.ksaa.dal.ui.viewModels.MainActivityVM

abstract class BaseMaterialDialogFragment(layout: Int): AppCompatDialogFragment(layout) {


    val activityVM: MainActivityVM by activityViewModels()
    lateinit var mainActivity: MainActivity
    protected var _user: NewUser? = null

    lateinit var DARK_RED_COLOR_STATE_LIST: ColorStateList
    lateinit var WHITE_COLOR_STATE_LIST : ColorStateList

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        mainActivity = activity as MainActivity
        DARK_RED_COLOR_STATE_LIST = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
        WHITE_COLOR_STATE_LIST = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

        _user = activityVM.userMLD.value
        Log.i(TAG, "onViewCreated: _user = $_user")
        initViews(createdView)
        dialog?.window?.setBackgroundDrawable(context?.getDrawable(R.drawable.corner_raduis_framelayout))

    }

    abstract fun initViews(createdView : View)

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
        }
    }

    fun <T> newHandleSuccessOrErrorResponse(
        res: sa.gov.ksaa.dal.data.webservices.newDal.NewResource<T>,
        onSuccess: ((data: T) -> Unit)? = null,
        onError: ((errors: Errors) -> Unit)? = null
    ) {
        when (res) {
            is sa.gov.ksaa.dal.data.webservices.newDal.NewResource.Success -> {
                Log.i(TAG, "getResponseData : Resource.Success : response = ${res}")
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
                Log.i(TAG, "getResponseData : Resource.Error : message= ${res.message}")
//                val errors = res.response?.errors
//                if (errors != null && onError != null) onError(errors)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openFile(pickerInitialUri: Uri?, fileType: String, activityResultLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = fileType

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            if (pickerInitialUri != null) putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

//        startActivityForResult(intent, PICK_PDF_FILE)
        activityResultLauncher.launch(intent)
    }

    @SuppressLint("Recycle")
    fun gettingFile (activityResult: ActivityResult, fileListener: FileListener){
        val uri = activityResult.data?.data
        if (uri == null){
            Toast.makeText(requireContext(), R.string.something_went_wrong_please_try_again_later, Toast.LENGTH_SHORT).show()
            Log.w(TAG, "gettingFile: uri == null" )
        } else {
            val contentResolver = requireActivity().contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor == null) {
                Toast.makeText(requireContext(), R.string.something_went_wrong_please_try_again_later, Toast.LENGTH_SHORT).show()
                Log.w(TAG, "gettingFile: uri == null" )
            } else {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                fileListener.onFileChosen(MyFile(cursor.getString(nameIndex), cursor.getLong(sizeIndex),
                    contentResolver.openInputStream(uri), contentResolver.getType(uri), uri, null))

            }

        }


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

    @FunctionalInterface
    interface FileListener {
        fun onFileChosen(myFile: MyFile)
    }
}