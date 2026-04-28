package sa.gov.ksaa.dal.ui.fragments.freelancers

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
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FreelancerFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.ui.adapters.FreelancerAccomplishedProjectsAdapter
import sa.gov.ksaa.dal.ui.adapters.FreelancerFilesRvAdapter
import sa.gov.ksaa.dal.ui.adapters.ReviewsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.client.ClientProfileFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.ProjectDetailsFragment
import sa.gov.ksaa.dal.ui.fragments.projects.details.SpamDialog
import sa.gov.ksaa.dal.ui.fragments.reviews.ReviewReport
import sa.gov.ksaa.dal.ui.viewModels.CompletedProjectsVM
import sa.gov.ksaa.dal.ui.viewModels.FavouriteFreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM
import sa.gov.ksaa.dal.ui.viewModels.ReviewsVM

class FreelancerProfileFragment : BaseFragment(R.layout.fragment_profiles_freelancer),
    FreelancerAccomplishedProjectsAdapter.OnClickListener,
    FreelancerFilesRvAdapter.OnClickListener,ReviewsRVadapter.OnSubmissionListener {
    companion object {
        const val freelancerUserIdKey = "freelancerUserId"
    }

    lateinit var backBtn: ImageView
    lateinit var favouriteIV: ImageView
    lateinit var nameTV: TextView
    lateinit var userIV: ImageView
//    lateinit var ratingAvgTV: TextView
    lateinit var ratingBar: RatingBar
    lateinit var reviewCountTV: TextView
    lateinit var completedProCountTV: TextView
    lateinit var serviceDomainsRV: RecyclerView
    lateinit var certificatesRV: RecyclerView
    lateinit var servicesRV: RecyclerView
    lateinit var aboutTV: TextView
    lateinit var completedProRV: RecyclerView
    lateinit var serviceDomainsTV: TextView
    lateinit var servicesTV: TextView
    lateinit var accomplishedProjectsTV: TextView
    lateinit var reviewsTV: TextView
    lateinit var reviewsRV: RecyclerView
    lateinit var noCompletedProTV: TextView
    lateinit var certsTV: TextView



    val freelancersVM: FreelancersVM by viewModels()

    val completedProjectsVM: CompletedProjectsVM by viewModels()
    val reviewsVM: ReviewsVM by viewModels()
    val projectsVM: ProjectsVM by viewModels()
    val favouriteFreelancersVM: FavouriteFreelancersVM by viewModels()

    lateinit var reviewsRVadapter: ReviewsRVadapter
    lateinit var accompishedProjectsAdapter: FreelancerAccomplishedProjectsAdapter
    lateinit var experienceFilesRV_Adapter: FreelancerFilesRvAdapter
    lateinit var eduCertsFilesRV_Adapter: FreelancerFilesRvAdapter
    var freelancerUserId: Int? = null

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        appBarLayout.visibility = View.GONE

        initViews(createdView)

        eduCertsFilesRV_Adapter = FreelancerFilesRvAdapter(mutableListOf(), this)
        certificatesRV.adapter = eduCertsFilesRV_Adapter

//        experienceFilesRV_Adapter = FreelancerFilesRvAdapter(mutableListOf(), this)
//        attatchmentsRV.adapter = experienceFilesRV_Adapter

        accompishedProjectsAdapter = FreelancerAccomplishedProjectsAdapter(mutableListOf(), this)
        completedProRV.layoutManager = GridLayoutManager(context, 2)
        completedProRV.adapter = accompishedProjectsAdapter

        reviewsRVadapter =
            ReviewsRVadapter(mutableListOf(), R.drawable.client_cercular, requireContext(),this)
        reviewsRV.adapter = reviewsRVadapter

        backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_freelancerProfileFragment_to_exploreFragment)
        }

        val freelancer = activityVM.freelancerMLD.value

        if (freelancer == null) {
            freelancerUserId = arguments?.getInt(freelancerUserIdKey, 0)
            if (freelancerUserId != null && freelancerUserId != 0) {
                freelancersVM.getFreelancersByUserId(mapOf("userId" to freelancerUserId.toString()))
                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { freelancersItems ->
                            if (freelancersItems.isNotEmpty()) {
                                val freelancer = freelancersItems[0]
                                updateUI(freelancer)
                                getAccomplishedProjects(freelancer)
                                getReviews(freelancer)
                            }

                        }) { errors ->
                            Snackbar.make(
                                createdView,
                                errors?.message!!,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        } else {
            updateUI(freelancer)
            getAccomplishedProjects(freelancer)
        }


    }

    fun getAccomplishedProjects(freelancer: NewFreelancer) {
        completedProjectsVM.getAllByUserTypeAndUserId(
            mapOf(
                "user_id" to freelancer.userId.toString(),
                "user_type" to NewUser.FREELANCER_USER_TYPE.lowercase()
            )
        )
            .observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, { completedProjects ->
                    reviewsRV.visibility = View.GONE
                    if (completedProjects.isEmpty()) {
                        completedProRV.visibility = View.GONE
                        noCompletedProTV.visibility = View.VISIBLE
                    } else {
                        noCompletedProTV.visibility = View.GONE
                        accompishedProjectsAdapter.setList(completedProjects)
                        completedProCountTV.text = numberFormat.format(completedProjects.size)
                    }

                })
            }
    }

    fun getReviews(freelancer: NewFreelancer) {

        reviewsVM.getAllReviewsByUserId(mapOf("userId" to freelancer.userId.toString()))
            .observe(viewLifecycleOwner) {
                newHandleSuccessOrErrorResponse(it, {
                    Log.i(javaClass.simpleName, "getReviews: it = $it")
                    completedProRV.visibility = View.GONE
                    if (it.isEmpty()) {
                        reviewsRV.visibility = View.GONE
                        noCompletedProTV.visibility = View.VISIBLE
                    } else {
                        noCompletedProTV.visibility = View.GONE
                        reviewsRV.visibility = View.VISIBLE

                        if (it.isEmpty() ){
                            val review = currentFreelancer!!.listOfRating ?: listOf()
                            reviewsRVadapter.setList(review)

                        }else {
                            reviewsRVadapter.setList(it)
                        }
                    }
                })
            }
    }


    fun initViews(createdView: View) {
        backBtn = createdView.findViewById(R.id.backBtn)
        favouriteIV = createdView.findViewById(R.id.favouriteIV)
        nameTV = createdView.findViewById(R.id.nameTV)
        userIV = createdView.findViewById(R.id.userIV)
//        ratingAvgTV = createdView.findViewById(R.id.ratingAvgTV)
        ratingBar = createdView.findViewById(R.id.ratingBar)
        reviewCountTV = createdView.findViewById(R.id.reviewNumberTV)
        completedProCountTV = createdView.findViewById(R.id.completedProCountTV)
        serviceDomainsRV = createdView.findViewById(R.id.serviceDomainsRV)

        certificatesRV = createdView.findViewById(R.id.certificatesRV)
        certsTV = createdView.findViewById(R.id.certsTV)


//        attatchmentsTV = createdView.findViewById(R.id.attatchmentsTV)
//        attatchmentsRV = createdView.findViewById(R.id.attatchmentsRV)

        servicesRV = createdView.findViewById(R.id.servicesRV)
        aboutTV = createdView.findViewById(R.id.aboutTV)
        completedProRV = createdView.findViewById(R.id.completedProRV)

        reviewsRV = createdView.findViewById(R.id.reviewsRV)
        serviceDomainsTV = createdView.findViewById(R.id.serviceDomainsTV)
        servicesTV = createdView.findViewById(R.id.servicesTV)

        accomplishedProjectsTV = createdView.findViewById(R.id.accomplishedProjectsTV)
        accomplishedProjectsTV.isSelected = true
        accomplishedProjectsTV.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        noCompletedProTV = createdView.findViewById(R.id.noCompletedProTV)

        reviewsTV = createdView.findViewById(R.id.reviewsTV)
        reviewsTV.isSelected = false

    }

    //    var freelancer: NewFreelancer? = null
    private fun updateUI(freelancer: NewFreelancer) {

        Log.i(javaClass.simpleName, "updateUI: freelancer = $freelancer")

        setProfileImage(freelancer.image, userIV, NewUser.FREELANCER_USER_TYPE, freelancer.gender)

        nameTV.text = freelancer.getFullName()
//        ratingAvgTV.text = numberFormat.format(freelancer.rating ?: 0.0f)
        ratingBar.rating = freelancer.rating ?: 0.0f
        reviewCountTV.text = numberFormat.format(freelancer.reviewCount ?: 0.0f)

        completedProCountTV.text = numberFormat.format(freelancer.noOfProjectDone ?: 0)
        serviceDomainsTV.text = freelancer.listOfServices?.firstOrNull()?.typeOfServices
        if (_user?.isClient() == true) {
            favouriteIV.visibility = View.VISIBLE
            favouriteIV.setOnClickListener {
                favouriteIV.isSelected = freelancer.favourite == true
                if (freelancer.favourite == true){


                // clientId=6&isFavourite=false&freelancerId=2
                    favouriteFreelancersVM.favouriteFreelancer(
                        mapOf(
                            "clientId" to _user!!.userId.toString(),
                            "freelancerId" to (freelancer.userId).toString(), // freelancer.freelancerId?: freelancer.id?:
                            "isFavourite" to "false"
                        )
                    )
                        .observe(viewLifecycleOwner) {
                            newHandleSuccessOrErrorResponse(it, {
//                                freelancersRVadapter.setFavorite(position, false)
                                activitySnackbar.setText( "تمت إضاقة مقدم الخدمة الى المفضلة")
                                    .show()
                                favouriteIV.isSelected = it.favourite == false
                            })
                        }
            }else{
                    favouriteFreelancersVM.favouriteFreelancer(
                        mapOf(
                            "clientId" to _user!!.userId.toString(),
                            "freelancerId" to (freelancer.userId).toString(), // freelancer.freelancerId?: freelancer.id?:
                            "isFavourite" to "true"
                        )
                    )
                        .observe(viewLifecycleOwner) {
                            newHandleSuccessOrErrorResponse(it, {
//                                freelancersRVadapter.setFavorite(position, false)
                                activitySnackbar.setText( "تمت إضاقة مقدم الخدمة الى المفضلة")
                                    .show()
                                favouriteIV.isSelected = it.favourite == true
                            })
                        }
                }
            }
        }else{
            favouriteIV.visibility = View.GONE
        }

        aboutTV.text = freelancer.about

        if (freelancer.typeOfCertificate.isNullOrEmpty()) {
            certsTV.visibility = View.VISIBLE
        } else {
            certsTV.text = freelancer.typeOfCertificate
//            certificationsRV_Adapter.setList(freelancer.typeOfCertificate?: listOf())
        }

        servicesTV.text = freelancer.listOfServices?.firstOrNull()?.typeOfServices

//        if (!freelancer.listOfFiles.isNullOrEmpty()) {
//            val expFiles =
//                freelancer.listOfFiles!!.filter { file -> file.fileCategory.equals(FreelancerFile.WORK_CERTIFICATE_CATEGORY) }
//            if (expFiles.isNotEmpty()) {
//                attatchmentsTV.visibility = View.GONE
//                experienceFilesRV_Adapter.setList(expFiles)
//            } else {
//                attatchmentsTV.visibility = View.VISIBLE
//            }
//
//            val eduFiles =
//                freelancer.listOfFiles!!.filter { file -> file.fileCategory.equals(FreelancerFile.EDUCATION_CERTIFICATE_CATEGORY) }
//            if (eduFiles.isNotEmpty()) {
//                eduCertsFilesRV_Adapter.setList(eduFiles)
//            } else {
//
//            }
//        } else {
//            attatchmentsTV.visibility = View.VISIBLE
//        }


        accomplishedProjectsTV.setOnClickListener {

            it.isSelected = !it.isSelected
            reviewsTV.isSelected = !reviewsTV.isSelected
            reviewsRV.visibility = View.GONE
            completedProRV.visibility = View.VISIBLE
            getAccomplishedProjects(freelancer)
        }
        reviewsTV.setOnClickListener {
            it.isSelected = !it.isSelected
            accomplishedProjectsTV.isSelected = !accomplishedProjectsTV.isSelected
            completedProRV.visibility = View.GONE
            reviewsRV.visibility = View.VISIBLE

            getReviews(freelancer)
        }
    }

    override fun onClicked(project: ClosedProject) {
        activityVM.newProjectMLD.postValue(project.toProject())
        findNavController().navigate(R.id.action_freelancerProfileFragment_to_projectDetailsFragment, bundleOf(ProjectDetailsFragment.projectIdArgKey to project.projectId))
    }

    override fun onClientIvClicked(user: Int) {
        findNavController().navigate(
            R.id.action_freelancerProfileFragment_to_clientProfileFragment,
            bundleOf(ClientProfileFragment.userIdKey to user)
        )
    }

    override fun onFreelancerIvClicked(freelancer: Int) {

    }

    override fun onClicked(project: NewProject) {

    }

    override fun onSpamClicked(comment: RatingAndReview?, commentId: Int?, reportText: String?) {
        activityVM.postRatingAndReview.postValue(comment)
        ReviewReport().show(parentFragmentManager, SpamDialog.tag)
    }
}
