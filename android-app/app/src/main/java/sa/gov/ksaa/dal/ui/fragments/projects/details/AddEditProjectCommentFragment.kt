package sa.gov.ksaa.dal.ui.fragments.projects.details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.CommentOnProject
import sa.gov.ksaa.dal.data.models.Enquiry
import sa.gov.ksaa.dal.data.webservices.dal.responses.error.Errors
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.CommentsOnProjectVM


class AddEditProjectCommentFragment: BaseFragment(R.layout.fragment_dialog_project_enquiry){ // (val projectId: Int, val onSubmissionListener: OnSubmissionListener, val user: User)

    companion object {
        const val TAG = "ProjectEnquiry"
    }

    val vm: CommentsOnProjectVM by viewModels()

//    lateinit var enquiry: Enquiry
    lateinit var comment: CommentOnProject
    private fun isValid(): Boolean {
        if (commentET.text.trim().isEmpty()){
            commentET.error = getString(R.string.this_field_is_required)
            commentET.requestFocus()
            return false
        }
        comment.comment = commentET.text.toString().trim()
        comment.user_id = _user!!.userId
        comment.project_id = activityVM.newProjectMLD.value!!.projectId ?: activityVM.newProjectMLD.value!!.id
        return true
    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        initViews(createdView)
//        cancelBtn.setOnClickListener {
//            dismiss()
//        }


        aboutUserCharMax.text = numberFormat.format(300)
        aboutUserChar.text = numberFormat.format(0)



        commentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar.text = p0?.length.toString()



                val noChars = p0?.length?:0


                aboutUserChar.text = numberFormat.format(noChars)
                if (noChars >=300){
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    commentET.text?.delete(300, noChars)
                } else {
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_font_gray))



                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        submitBtn.setOnClickListener {
            if (isValid()){
                vm.add_aProjectComment(mapOf("userId" to _user!!.userId.toString(),
                    "comments" to comment.comment!!, "projectId" to comment.project_id.toString()))
                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { newComment ->
                            Snackbar.make(requireView(), "تم ارسال الاستفسار بنجاح", Snackbar.LENGTH_SHORT)
                                .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>(){

                                })
                                .show()
                        }) {errors: Errors? ->
                            errors?.errorsList?.forEach { error ->
                                when (error?.path) {
                                    Enquiry::message.name -> {
                                        commentET.error = error.message
                                        commentET.requestFocus()
                                    }
                                    Enquiry::name.name, Enquiry::email.name, Enquiry::user_id.name -> {
                                        Snackbar.make(requireView(), "الرجاء تسجيل الدخول و المحاولة مرة اخرى", Snackbar.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        Snackbar.make(requireView(), R.string.something_went_wrong_please_try_again_later, Snackbar.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
            }
        }
        comment = CommentOnProject()
    }

//    private fun onCommentSubmitted(newComment: CommentOnProject) {
//        onSubmissionListener.onCommentSubmitted(newComment)
//    }

    lateinit var commentET: EditText
//    lateinit var cancelBtn: ImageButton
    lateinit var submitBtn: Button
    lateinit var aboutUserChar: TextView
    lateinit var aboutUserCharMax: TextView
    fun initViews(createdView: View) {
        commentET = createdView.findViewById(R.id.commentET)
//        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
        aboutUserChar = createdView.findViewById(R.id.aboutUser_char_TV)
        aboutUserCharMax = createdView.findViewById(R.id.aboutUserMax)
    }


    interface OnSubmissionListener{
        fun onCommentSubmitted(newComment: CommentOnProject)
    }
}
