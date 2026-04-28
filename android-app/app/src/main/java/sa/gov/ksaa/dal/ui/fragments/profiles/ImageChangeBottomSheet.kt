package sa.gov.ksaa.dal.ui.fragments.profiles

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.soundcloud.android.crop.Crop
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.ui.fragments.BaseBottomSheetDialogFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancersFilterBottomSheetModal
import java.io.File

class ImageChangeBottomSheet(
    val onChaneImageListener: OnChangeImage ,

    ) : BaseBottomSheetDialogFragment(R.layout.model_bottom_sheet_change_image) {

        lateinit var changeImageBTN : MaterialTextView
    lateinit var deleteImageBTN : MaterialTextView

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)
        updateUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val createdView = inflater.inflate(R.layout.model_bottom_sheet_change_image, container, false)
//        initViews(createdView)
//        animate()
        changeImageBTN =  createdView.findViewById(R.id.changeImageNew)
        changeImageBTN.setOnClickListener{
            onChaneImageListener.onImageChange()
        }
        return createdView
    }

    private fun updateUI(){

        deleteImageBTN.setOnClickListener{
            onChaneImageListener.deleteImageProfile()
        }

    }
    private fun initViews(createdView: View){
        changeImageBTN =  createdView.findViewById(R.id.changeImageNew)
        deleteImageBTN =  createdView.findViewById(R.id.deleteImageProfile)
    }
    companion object {
        const val TAG = "ChangeImageBottomSheet"
    }


    interface OnChangeImage{
        fun onClickImgeChange()
        fun onImageChange()
        fun deleteImageProfile()
    }
}