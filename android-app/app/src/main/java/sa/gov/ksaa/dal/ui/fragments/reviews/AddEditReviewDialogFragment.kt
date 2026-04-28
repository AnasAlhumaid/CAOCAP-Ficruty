package sa.gov.ksaa.dal.ui.fragments.reviews

import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.ProjectDeliverable
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.ui.fragments.BaseMaterialDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.ReviewsVM

class AddEditReviewDialogFragment // , val onSubmitLister: OnSubmitLister
    : BaseMaterialDialogFragment(R.layout.fragment_dialog_add_edit_a_review){

    companion object {
        const val tag = "AddEditReviewDialogFragment"
        const val completedProjectIdKey = "completedProjectId"
            const val revieweeIdKey = "revieweeId"
    }

    val reviewsVM: ReviewsVM by viewModels()

//    lateinit var review: Review
//    var completedProjectId = 0
//    var revieweeId = 0
    // lateinit var closedProject: ClosedProject
    lateinit var postRatingAndReview: RatingAndReview


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        if (_user!!.isClient()){
            userType1TV.text = "مقدم الخدمة"
            userType2TV.text = "مقدم الخدمة"
            commentTV.hint = "التعليق على مقدم الخدمة"
        } else if (_user!!.isFreelancer()){
            userType1TV.text = "طالب الخدمة"
            userType2TV.text =  "طالب الخدمة"
            commentTV.hint =  "التعليق على طالب الخدمة"
        }

//        layout.outlineProvider = object : ViewOutlineProvider() {
//            override fun getOutline(view: View, outline: Outline) {
//                outline.setRoundRect(0, 0, view.width, view.height, 20f) // Set corner radius in dp (converted to float)
//            }
//        }

//        closedProject = activityVM.closedProject.value!!
        postRatingAndReview = activityVM.postRatingAndReview.value!!

//        arguments?.let{
//            completedProjectId = it.getInt(completedProjectIdKey, 0)
//            revieweeId = it.getInt(revieweeIdKey, 0)
//        }

        cancelBtn.setOnClickListener {
            findNavController().popBackStack()
//            dismiss()
        }

        submitBtn.setOnClickListener {



            if (isValid()){
//                val params = mutableMapOf<String, String>(
//                    "projectId" to closedProject.projectId!!.toString(),
//                    "rating" to review.rating!!.toInt().toString(),
//                    "review" to review.review!!,
//                    "clientId" to closedProject.userId!!.toString(),
////                (if(_user!!.isClient()) _user!!.userId!!
////                else
//                    // _user!!.userId.toString()
//                    "freelancerId" to closedProject.freelanceId!!.toString()
//                )


//                https://dal.ksaa.gov.sa/api/jsonws/languageServicePlatform.giveRatingReviewToFreelancer?projectId=18&clientId=7&freelancerId=4&rating=4&review=%D8%AC%D9%8A%D8%AF&websiteRating=%D9%84%D8%A7%D8%A8%D8%A3%D8%B3&websiteReview=5

                if (_user!!.isClient()){
                    // projectId=49&clientId=6&freelancerId=3&rating=4&review=جيدجيد
//                    params["clientId"] = completedProject.clientId.toString() // _user!!.userId.toString()
//                    params["freelancerId"] = completedProject.freelanceId.toString()
                    val params = mutableMapOf<String, String>(
                        "projectId" to postRatingAndReview.projectId!!.toString(),
                        "rating" to postRatingAndReview.userRating!!.toInt().toString(),
                        "review" to postRatingAndReview.userReview!!,
                        "clientId" to postRatingAndReview.clientUserId!!.toString(),
//                (if(_user!!.isClient()) _user!!.userId!!
//                else
                        // _user!!.userId.toString()
                        "freelancerId" to postRatingAndReview.freelancerUserId!!.toString(),
                        "websiteRating" to postRatingAndReview.userRatingWebsite!!.toInt().toString(),
                        "websiteReview" to postRatingAndReview.userReviewWebsite!!

                    )
                    reviewsVM.rate_aFreelancer(params)
                        .observe(viewLifecycleOwner, responseObserver)

                } else if (_user!!.isFreelancer()){
                    // projectId=49&clientId=5&freelancerId=2&rating=4&review=ممتاز
//                    params["clientId"] = completedProject.clientId.toString()
//                    params["freelancerId"] = activityVM.currentFreelancerMLD.value!!.userId.toString()
                    val params = mutableMapOf<String, String>(
                        "projectId" to postRatingAndReview.projectId!!.toString(),
                        "rating" to postRatingAndReview.userRating!!.toInt().toString(),
                        "review" to postRatingAndReview.userReview!!,
                        "clientId" to postRatingAndReview.clientUserId!!.toString(),
//                (if(_user!!.isClient()) _user!!.userId!!
//                else
                        // _user!!.userId.toString()
                        "freelancerId" to postRatingAndReview.freelancerUserId!!.toString(),
                        "websiteRating" to postRatingAndReview.userRatingWebsite!!.toInt().toString(),
                        "websiteReview" to postRatingAndReview.userReviewWebsite!!

                    )
                    reviewsVM.rate_aClient(params)
                        .observe(viewLifecycleOwner, responseObserver)
                }

            }
        }
    }

    val responseObserver = Observer<NewResource<RatingAndReview>>{ res ->
        newHandleSuccessOrErrorResponse(res, {
            if (_user!!.isClient()){
                activityVM.ongoingProjectLD.value?.isReviewed = true
                findNavController().navigate(R.id.action_addEditReviewDialogFragment_to_completedProjectsClientFragment)
            } else if(_user!!.isFreelancer()){
                activityVM.closedProject.value!!.isReviewed = true
            }

            Snackbar.make(requireView(), "تم حفظ التقييم بنجاح", Snackbar.LENGTH_SHORT)
                .show()
            findNavController().popBackStack()
        })
    }
    lateinit var input: String
    private fun isValid(): Boolean {
//        if (completedProjectId == 0 || revieweeId == 0) {
//            Snackbar.make(requireView(), R.string.something_went_wrong_please_try_again_later, Snackbar.LENGTH_SHORT)
//                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>(){
//                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
//                        super.onDismissed(transientBottomBar, event)
//                        findNavController().popBackStack()
//                    }
//                })
//                .show()
//
//            return false
//        }

        input = commentTV.text.toString().trim()


        postRatingAndReview.userReview = input
        postRatingAndReview.userRating = ratingBar.rating
        postRatingAndReview.userRatingWebsite = ratingWebsite.rating


        input = commentToWebsite.text.toString().trim()
        postRatingAndReview.userReviewWebsite = input
        return true
    }
    lateinit var cancelBtn: ImageView
    lateinit var submitBtn: MaterialButton
    lateinit var ratingBar: RatingBar
    lateinit var userType1TV: TextView
    lateinit var userType2TV: TextView
    lateinit var commentTV: TextView
    lateinit var ratingWebsite : RatingBar
    lateinit var commentToWebsite : TextView


    override fun initViews(createdView: View) {
        cancelBtn = createdView.findViewById(R.id.cancelBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)
        ratingBar = createdView.findViewById(R.id.ratingBar)
        userType1TV = createdView.findViewById(R.id.userType1TV)
        userType2TV = createdView.findViewById(R.id.userType2TV)
        commentTV = createdView.findViewById(R.id.commentTV)
        ratingWebsite = createdView.findViewById(R.id.ratingBar2Website)
        commentToWebsite = createdView.findViewById(R.id.commentToWebsiteTV)

    }

    interface OnSubmitLister{
        fun onSubmit(projectDeliverable: ProjectDeliverable)
    }


}