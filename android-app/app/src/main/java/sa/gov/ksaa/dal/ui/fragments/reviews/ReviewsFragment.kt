package sa.gov.ksaa.dal.ui.fragments.reviews

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewCommentOnProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ReplyTo_aComment
import sa.gov.ksaa.dal.data.webservices.newDal.responses.SpamCommint
import sa.gov.ksaa.dal.ui.adapters.ReviewsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.SpamDialog
import sa.gov.ksaa.dal.ui.viewModels.ReviewsVM

class ReviewsFragment : BaseFragment(R.layout.fragment_reviews),ReviewsRVadapter.OnSubmissionListener {

    lateinit var userIV: ImageView
    lateinit var nameTV: TextView
    lateinit var reviewCountTV: TextView
    lateinit var reviewsRVadapter: ReviewsRVadapter
    lateinit var reviewsRV: RecyclerView
    lateinit var noDataTV: TextView
    lateinit var summaryLL: LinearLayout
    lateinit var backBtn: ImageView

    lateinit var ratingBar: RatingBar
    lateinit var reviewNumberTV: TextView

    val reviewsVM: ReviewsVM by viewModels()

    private fun initViews(createdView: View) {
        nameTV = createdView.findViewById(R.id.nameTV)
//        ratingAvgTV = createdView.findViewById(R.id.ratingAvgTV)
        ratingBar = createdView.findViewById(R.id.ratingBar)
        reviewNumberTV = createdView.findViewById(R.id.reviewNumberTV)
        userIV = createdView.findViewById(R.id.userIV)
        reviewCountTV = createdView.findViewById(R.id.reviewCountTV)
        reviewsRV = createdView.findViewById(R.id.recyclerView)
        backBtn = createdView.findViewById(R.id.backBtn)
        noDataTV = createdView.findViewById(R.id.noDataTV)
        summaryLL = createdView.findViewById(R.id.summaryLL)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        initViews(createdView)

        val reviewerImageResId = if (_user!!.isFreelancer()){
            R.drawable.client_cercular

        }
        else if (_user!!.isClient()) R.drawable.freelancer_male_cercular_100
        else 0



        reviewsRVadapter = ReviewsRVadapter(mutableListOf(), reviewerImageResId, requireContext(),this)
        reviewsRV.adapter = reviewsRVadapter
        bottomNavigationView.visibility = View.GONE

        updateUI()

        getReviews()

    }

    fun getReviews() {
        // userId=2
        reviewsVM.getAllReviewsByUserId(mapOf("userId" to _user!!.userId.toString()))
            .observe(viewLifecycleOwner) { res ->
                newHandleSuccessOrErrorResponse(res, {
                    Log.i(javaClass.simpleName, "getReviews: it = $it")
                    var reviews = it
                    if (it.isEmpty() && _user!!.isFreelancer())
                        reviews = currentFreelancer!!.listOfRating ?: listOf()

                    updateUI(reviews)
                })
            }
    }

    private fun updateUI(reviewAndRatings: List<RatingAndReview>) {
        Log.i(javaClass.simpleName, "updateUI: it = $reviewAndRatings")
        if (reviewAndRatings.isEmpty()) {
            noDataTV.visibility = View.VISIBLE
            summaryLL.visibility = View.GONE
        } else {
            noDataTV.visibility = View.GONE
            summaryLL.visibility = View.VISIBLE
        }
        reviewsRVadapter.setList(reviewAndRatings)
        updateSummary(reviewsRVadapter.getAvg(), reviewsRVadapter.itemCount)


    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        nameTV.text = _user!!.getFullName()

        updateSummary()

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

       if (_user!!.isClient()){
          setProfileImage( _user!!.image   , userIV, _user!!.userType, _user!!.gender?: currentFreelancer!!.gender)
       }
        else{

           setProfileImage(_user!!.image  , userIV, _user!!.userType, _user!!.gender?: currentFreelancer!!.gender)
       }


    }

    private fun updateSummary(rating: Float = 0.0f, reviewCount: Int = 0) {
//        ratingAvgTV.text =
//            String.format(arabicLocale, "%.1f", rating) // numberFormat.format(_user.rating)
        ratingBar.rating = rating
        reviewNumberTV.text = numberFormat.format(reviewCount) // reviewsRVadapter.itemCount
        reviewCountTV.text = numberFormat.format(reviewCount)
    }



    override fun onSpamClicked(
        comment: RatingAndReview?,
        commentId: Int?,
        reportText: String?
    ) {


            activityVM.postRatingAndReview.postValue(comment)
            ReviewReport().show(parentFragmentManager, SpamDialog.tag)

    }


}