package sa.gov.ksaa.dal.ui.fragments.client

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClientItem
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.ui.adapters.FreelancerAccomplishedProjectsAdapter
import sa.gov.ksaa.dal.ui.adapters.ReviewsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.freelancers.FreelancerProfileFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment
import sa.gov.ksaa.dal.ui.viewModels.ClientVM
import sa.gov.ksaa.dal.ui.viewModels.CompletedProjectsVM
import sa.gov.ksaa.dal.ui.viewModels.ReviewsVM

class ClientProfileFragment : BaseFragment(R.layout.fragment_profiles_client),
    FreelancerAccomplishedProjectsAdapter.OnClickListener,ReviewsRVadapter.OnSubmissionListener {

    companion object {
        const val userIdKey = "userIdKey"
    }

    val vm: ClientVM by viewModels()
    val completedProjectsVM: CompletedProjectsVM by viewModels()
    val reviewsVM: ReviewsVM by viewModels()

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.GONE

        initViews(createdView)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        accompishedProjectsAdapter = FreelancerAccomplishedProjectsAdapter(mutableListOf(), this)

        completedProRV.layoutManager = GridLayoutManager(context, 2)
        completedProRV.adapter = accompishedProjectsAdapter

        reviewsRVadapter = ReviewsRVadapter(mutableListOf(), R.drawable.client_cercular, requireContext(),this)
        reviewsRV.adapter = reviewsRVadapter

        userId = arguments?.getInt(userIdKey, 0)
        userId?.let {
            if (it != 0) {
                // clientId=2
                vm.getClientsById(it)
                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { clients ->
                            if (clients.isNotEmpty()){
                                updateClient(clients[0])
                                getAccomplishedProjects()
                                setProfileImage(clients[0].imageUrl, userIV,_user!!.userType ,"userGender" )
                            }

                        }) { errors ->
                            Snackbar.make(
                                createdView,
                                errors.message!!,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }



    }

    lateinit var reviewsRVadapter: ReviewsRVadapter
    var userId: Int?= null

    lateinit var accompishedProjectsAdapter: FreelancerAccomplishedProjectsAdapter
    fun getAccomplishedProjects(){
        userId?.let {
            // user_type=freelancer&user_id=1
            completedProjectsVM.getAllByUserTypeAndUserId(mapOf("user_id" to it.toString(), "user_type" to NewUser.INDIVIDUAL_CLIENT_USER_TYPE.lowercase()))
                .observe(viewLifecycleOwner){
                    newHandleSuccessOrErrorResponse(it, {completedProjects ->

                        if (completedProjects.isEmpty()){
                            completedProRV.visibility = View.GONE
                            noDateTV.visibility = View.VISIBLE
                        } else {
                            accompishedProjectsAdapter.setList(completedProjects)
                        }

                    })
                }
        }
    }

    fun getReviews(){
        // userId=2
        reviewsVM.getAllReviewsByUserId(mapOf("userId" to activityVM.userMLD.value!!.userId.toString()))
            .observe(viewLifecycleOwner){res ->
                newHandleSuccessOrErrorResponse(res, {
                    Log.i(javaClass.simpleName, "getReviews: it = $it")
                    if (it.isNotEmpty()){
                        reviewsRV.visibility = View.GONE
                        noDateTV.visibility = View.VISIBLE
                    } else {
                        reviewsRVadapter.setList(it)
                    }
                })
            }
    }

    lateinit var backBtn: ImageView
    lateinit var nameTV: TextView
    lateinit var userIV: ImageView
    lateinit var ratingAvgTV: TextView
    lateinit var ratingBar: RatingBar
    lateinit var reviewCountTV: TextView
    lateinit var aboutTV: TextView
    lateinit var completedProRV: RecyclerView
    lateinit var accomplishedProjectsTV: TextView
    lateinit var reviewsTV: TextView
    lateinit var reviewsRV: RecyclerView
    lateinit var noDateTV: TextView

    fun initViews(createdView: View) {
        backBtn = createdView.findViewById(R.id.backBtn)
        nameTV = createdView.findViewById(R.id.nameTV)
        userIV = createdView.findViewById(R.id.userIV)
        ratingAvgTV = createdView.findViewById(R.id.ratingAvgTV)
        ratingBar = createdView.findViewById(R.id.ratingBar)
        reviewCountTV = createdView.findViewById(R.id.reviewNumberTV)

        aboutTV = createdView.findViewById(R.id.aboutTV)
        completedProRV = createdView.findViewById(R.id.completedProRV)

        reviewsRV = createdView.findViewById(R.id.reviewsRV)

        accomplishedProjectsTV = createdView.findViewById(R.id.accomplishedProjectsTV)
        accomplishedProjectsTV.isSelected = true
        accomplishedProjectsTV.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        noDateTV = createdView.findViewById(R.id.noDateTV)
        reviewsTV = createdView.findViewById(R.id.reviewsTV)
        reviewsTV.isSelected = false
        accomplishedProjectsTV.setOnClickListener {

            it.isSelected = !it.isSelected
            reviewsTV.isSelected = !reviewsTV.isSelected
            reviewsRV.visibility = View.GONE
            completedProRV.visibility = View.VISIBLE
            getAccomplishedProjects()
        }
        reviewsTV.setOnClickListener {
            it.isSelected = !it.isSelected
            accomplishedProjectsTV.isSelected = !accomplishedProjectsTV.isSelected
            completedProRV.visibility = View.GONE
            reviewsRV.visibility = View.VISIBLE

            getReviews()
        }

    }

    lateinit var user: NewUser
    private fun updateClient(newUser: ClientItem) {
        this.user = newUser.toUser()
        nameTV.text = newUser.getFullName()

//        newUser.reviews_he_gets?.let {
//            if (it.isNotEmpty()) {
//                var ratingSum = 0f
//                it.forEach { r -> ratingSum += r.rating!! }
//                val ratingAvg =  ratingSum / it.size
//                ratingAvgTV.text = numberFormat.format(ratingAvg)
//                ratingBar.rating = ratingAvg
//            }
//
//        }
        aboutTV.text = newUser.about
    }

    override fun onClicked(project: ClosedProject) {



        findNavController().navigate(R.id.action_clientProfileFragment_to_projectDetailsFragment,
        bundleOf(ProjectDetailsFragment.projectIdArgKey to project.projectId)
        )
    }

    override fun onClientIvClicked(user: Int) {

    }

    override fun onFreelancerIvClicked(freelancer: Int) {
        findNavController().navigate(R.id.action_clientProfileFragment_to_freelancerProfileFragment,
        bundleOf(FreelancerProfileFragment.freelancerUserIdKey to freelancer)
        )
    }

    override fun onSpamClicked(comment: RatingAndReview?, commentId: Int?, reportText: String?) {
        TODO("Not yet implemented")
    }
}
