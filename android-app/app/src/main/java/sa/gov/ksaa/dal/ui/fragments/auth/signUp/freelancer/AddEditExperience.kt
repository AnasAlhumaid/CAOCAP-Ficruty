package sa.gov.ksaa.dal.ui.fragments.auth.signUp.freelancer

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.DeliverablesVM
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM

class AddEditExperience(var file: MyFile?, val onSubmitLister: OnSubmitLister) :
    BaseMaterialDialogFragment(R.layout.fragment_dialog_add_edit_experience) {

    companion object {
        const val tag = "AddExperience"
    }
    val vm: FreelancersVM by viewModels()

    //    var file: MyFile? =null
    val deliverableFileAtivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object : FileListener {
                override fun onFileChosen(myFile: MyFile) {
                    if (myFile.size > 15000000) {
                        fileNameTV.setTextColor(DARK_RED_COLOR_STATE_LIST)
                        fileNameTV.text = "حجم الملف تجاوز الحد المسوح به (15 م.ب)"
                        Log.i(
                            TAG,
                            "onPageScrollStateChanged: viewPager.currentItem = ${file}, state = true"
                        )


                    } else {
                        fileNameTV.setTextColor(WHITE_COLOR_STATE_LIST)
                        fileNameTV.text = myFile.name
                        file = myFile
                    }
                    Log.i(
                        TAG,
                        "onPageScrollStateChanged: viewPager.currentItem = ${file?.freelancerFile?.id}, state = false"
                    )
                }

            })

        }
    }


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

//        val myFile = activityVM.currentFileMLD.value
//        if (myFile != null) {
//            file = MyFile(myFile.name, myFile.size, myFile.inputStream, myFile.mimeType, myFile.uri, myFile.imageRealPath)
//            file!!.description = myFile.description
//        }

        updateUI()

        cancelBtn.setOnClickListener {
            dismiss()
        }

        addFileBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                openFile(null, BaseFragment.PDF_TYPE, deliverableFileAtivityResultLauncher)
            }
        }
        submitBtn.setOnClickListener {
            if (isValid()) {
//                activityVM.currentFileMLD.postValue(file)
//                findNavController()
//                    .popBackStack()
                onSubmitLister.onExperienceFileSubmitted(file!!)



                dismiss()

            }
        }
    }


    lateinit var input: String
    lateinit var description: String
    private fun isValid(): Boolean {
        if (file == null) {
            addFileBtn.error = getString(R.string.this_field_is_required)
            addFileBtn.requestFocus()
            return false
        }
        input = descriptionTV.text.toString().trim()
        if (input.isEmpty()) {
            descriptionTV.error = getString(R.string.this_field_is_required)
            descriptionTV.requestFocus()
            return false
        }
        file!!.description = input
        return true
    }

    lateinit var descriptionTV: TextView
    lateinit var cancelBtn: ImageView
    lateinit var submitBtn: MaterialButton
    lateinit var addFileBtn: MaterialButton
    lateinit var fileNameTV: TextView
    override fun initViews(createdView: View) {
        descriptionTV = createdView.findViewById(R.id.descriptionTV)
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
        addFileBtn = createdView.findViewById(R.id.addFileBtn)
        fileNameTV = createdView.findViewById(R.id.fileNameTV)
    }

    fun updateUI() {
        descriptionTV.text = file?.description ?: ""
        fileNameTV.text = file?.name ?: ""

        fileNameTV.setOnClickListener {
            if (file?.uri != null) {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, file?.uri)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(browserIntent)
            }

        }
    }

    interface OnSubmitLister {
        fun onExperienceFileSubmitted(myFile: MyFile)

    }
}