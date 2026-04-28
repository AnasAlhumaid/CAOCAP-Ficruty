package sa.gov.ksaa.dal.ui.fragments.starter

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.adapters.ProjectsRVadapter
import sa.gov.ksaa.dal.ui.adapters.ServiceRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.ProjectsVM
import sa.gov.ksaa.dal.ui.viewModels.ServicesVM
import kotlin.properties.Delegates

class Slider5ServicesFragment() : BaseFragment(R.layout.fragment_slider_5_services),MyFragmentInterface {

    lateinit var front_animation: AnimatorSet
    lateinit var back_animation: AnimatorSet
    lateinit var serviceRVadapter: ServiceRVadapter
    lateinit var serviceList: List<ServicesModel>
    lateinit var recyclerView: RecyclerView
    val serviceVM: ServicesVM by viewModels()

    val requestObserver = { newResource: NewResource<List<ServicesModel>> ->
        newHandleSuccessOrErrorResponse(newResource,
            onSuccess = { services ->

                serviceList = services
                 currentServices = services
                serviceRVadapter.setList(serviceList)



            }){

        }
    }

    private var activityInterface: MyFragmentInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MyFragmentInterface) {
            activityInterface = context
        }
    }

    fun setAmin() {


//        val front = findViewById<TextView>(R.id.card_front) as TextView
//        val back = findViewById<TextView>(R.id.card_back) as TextView
//        val flip = findViewById<Button>(R.id.flip_btn) as Button

//        front.cameraDistance = 8000 * scale
//        back.cameraDistance = 8000 * scale
//
//         Now we will set the event listener
//        frontFabs[0].setOnClickListener {
//            isFront = if (isFront) {
//                front_animation.setTarget(front)
//                back_animation.setTarget(back)
//                front_animation.start()
//                back_animation.start()
//                false
//
//            } else {
//                front_animation.setTarget(back)
//                back_animation.setTarget(front)
//                back_animation.start()
//                front_animation.start()
//                true
//            }
//        }
    }





    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        recyclerView = createdView.findViewById(R.id.recyclerViewHome)

        serviceRVadapter =
            ServiceRVadapter(requireContext(), mutableListOf())
        recyclerView.adapter = serviceRVadapter













    }

    override fun onResume() {
        super.onResume()
        val serviceLD =

                serviceVM.getAllServices()



        serviceLD.observe(viewLifecycleOwner, requestObserver)





    }

    override fun someFragmentMethod() {
        val serviceLD =

            serviceVM.getAllServices()



        serviceLD.observe(viewLifecycleOwner, requestObserver)


    }
}
//
//    private val frontCardIds = listOf(R.id.service1FrontCard, R.id.service2FrontCard, R.id.service3FrontCard,
//        R.id.service4FrontCard, R.id.service5FrontCard, R.id.service6FrontCard, R.id.service7FrontCard,
//        R.id.service8FrontCard, R.id.service9FrontCard
//    )
//    lateinit var frontCards: List<CardView>
//
//    private val frontFabIds = listOf(R.id.front1Fab, R.id.front2Fab, R.id.front3Fab, R.id.front4Fab,
//        R.id.front5Fab, R.id.front6Fab, R.id.front7Fab, R.id.front8Fab, R.id.front9Fab)
//    lateinit var frontFabs: List<FloatingActionButton>
//
//    private val backCardIds = listOf(R.id.service1BackCard, R.id.service2BackCard, R.id.service3BackCard,
//        R.id.service4BackCard, R.id.service5BackCard, R.id.service6BackCard, R.id.service7BackCard,
//        R.id.service8BackCard, R.id.service9BackCard
//    )
//    lateinit var backCards: List<CardView>
//
//    private val backFabIds = listOf(R.id.fab1, R.id.naqrahaFab, R.id.lingTransFab, R.id.specializedTransFab,
//        R.id.tashkeelFab, R.id.contentFormaulationFab, R.id.qafiahFab, R.id.tafreegFab, R.id.taaleegFab)
//    lateinit var backFabs: List<FloatingActionButton>
//
//    var scale by Delegates.notNull<Float>()
//    private fun initViews(createdView: View) {
//        scale = resources.displayMetrics.density
//        frontCards = frontCardIds.map { id -> createdView.findViewById(id) }
//        frontFabs = frontFabIds.map { id -> createdView.findViewById(id) }
//
//        backCards = backCardIds.map { id -> createdView.findViewById(id) }
//        backFabs = backFabIds.map { id -> createdView.findViewById(id) }
//
//    }
//
//    private fun animate() {
//
//        card_flip_right_in_anim_set = AnimatorInflater.loadAnimator(
//            requireContext().applicationContext,
//            R.animator.card_flip_right_in
//        ) as AnimatorSet
//
//        card_flip_right_out_anim_set = AnimatorInflater.loadAnimator(
//            requireContext().applicationContext,
//            R.animator.card_flip_right_out
//        ) as AnimatorSet
//
//        card_flip_left_in_anim_set = AnimatorInflater.loadAnimator(
//            requireContext().applicationContext,
//            R.animator.card_flip_left_in
//        ) as AnimatorSet
//
//        card_flip_left_out_anim_set = AnimatorInflater.loadAnimator(
//            requireContext().applicationContext,
//            R.animator.card_flip_left_out
//        ) as AnimatorSet
//
//
//        frontCards.forEachIndexed { index, card ->
//            card.cameraDistance = 8000 * scale
//            setFrontAnim(card, index)
//        }
//
//        backCards.forEachIndexed { index, card ->
//            card.cameraDistance = 8000 * scale
//            setBackAnim(card, index)
//        }
//
////        backFabs.forEachIndexed { index, fab ->
////            val card = backCards[index]
////            card.cameraDistance = 8000 * scale
////            fab.setOnClickListener {
////                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////
////                setBackAnim(card, index)
////            }
////        }
//    }
//
//    private fun setBackAnim(card: CardView, index: Int) {
//        card.setOnClickListener {
//
//            val frontCard = frontCards[index]
//            card_flip_left_in_anim_set = AnimatorInflater.loadAnimator(
//                requireContext().applicationContext,
//                R.animator.card_flip_left_in
//            ) as AnimatorSet
//
//            card_flip_left_in_anim_set.setTarget(frontCard)
//            card_flip_left_in_anim_set.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(p0: Animator) {
//                    frontCard.visibility = View.VISIBLE
//                }
//
//                override fun onAnimationEnd(p0: Animator) {
//                }
//
//                override fun onAnimationCancel(p0: Animator) {
//                    //
//                }
//
//                override fun onAnimationRepeat(p0: Animator) {//
//                }
//
//            })
//            card_flip_left_in_anim_set.start()
//
//
//            card_flip_right_out_anim_set = AnimatorInflater.loadAnimator(
//                requireContext().applicationContext,
//                R.animator.card_flip_right_out
//            ) as AnimatorSet
//            card_flip_right_out_anim_set.setTarget(it)
//            card_flip_right_out_anim_set.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(p0: Animator) {
//                    it.visibility = View.VISIBLE
//                }
//
//                override fun onAnimationEnd(p0: Animator) {
//                    it.visibility = View.GONE
//                }
//
//                override fun onAnimationCancel(p0: Animator) {
//                    //
//                }
//
//                override fun onAnimationRepeat(p0: Animator) {//
//                }
//
//            })
//            card_flip_right_out_anim_set.start()
//        }
//    }
//    private fun setFrontAnim(card: CardView, index: Int) {
//        card.setOnClickListener {
//            val backCard = backCards[index]
//
//            card_flip_right_in_anim_set = AnimatorInflater.loadAnimator(
//                requireContext(),
//                R.animator.card_flip_right_in
//            ) as AnimatorSet
//            card_flip_right_in_anim_set.setTarget(backCard)
//            card_flip_right_in_anim_set.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(p0: Animator) {
//                    backCard.visibility = View.VISIBLE
//                }
//
//                override fun onAnimationEnd(p0: Animator) {
//                }
//
//                override fun onAnimationCancel(p0: Animator) {
//                    //
//                }
//
//                override fun onAnimationRepeat(p0: Animator) {//
//                }
//
//            })
//            card_flip_right_in_anim_set.start()
//
//            card_flip_left_out_anim_set = AnimatorInflater.loadAnimator(
//                requireContext().applicationContext,
//                R.animator.card_flip_left_out
//            ) as AnimatorSet
//            card_flip_left_out_anim_set.setTarget(it)
//            card_flip_left_out_anim_set.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(p0: Animator) {
//                    it.visibility = View.VISIBLE
//                }
//
//                override fun onAnimationEnd(p0: Animator) {
//                    it.visibility = View.GONE
//                }
//
//                override fun onAnimationCancel(p0: Animator) {
//                    //
//                }
//
//                override fun onAnimationRepeat(p0: Animator) {//
//                }
//
//            })
//            card_flip_left_out_anim_set.start()
//        }
//    }
//
//    lateinit var card_flip_right_in_anim_set: AnimatorSet
//    lateinit var card_flip_right_out_anim_set: AnimatorSet
//    lateinit var card_flip_left_in_anim_set: AnimatorSet
//    lateinit var card_flip_left_out_anim_set: AnimatorSet
//
////    var showingBack = false
////    private fun flipCard() {
////        if (showingBack) {
////            childFragmentManager.popBackStack()
////            return
////        }
////
////        // Flip to the back.
////
////        showingBack = true
////
////        // Create and commit a new fragment transaction that adds the fragment for
////        // the back of the card, uses custom animations, and is part of the fragment
////        // manager's back stack.
////
////        childFragmentManager.beginTransaction()
////
////            // Replace the default fragment animations with animator resources
////            // representing rotations when switching to the back of the card, as
////            // well as animator resources representing rotations when flipping
////            // back to the front (e.g. when the system Back button is pressed).
////            .setCustomAnimations(
////                R.animator.card_flip_right_in,
////                R.animator.card_flip_right_out,
////                R.animator.card_flip_left_in,
////                R.animator.card_flip_left_out
////            )
////
////            // Replace any fragments currently in the container view with a
////            // fragment representing the next page (indicated by the
////            // just-incremented currentPage variable).
////            .replace(R.id.container, Fragment(R.layout.fragment_service_card_back))
////
////            // Add this transaction to the back stack, allowing users to press
////            // Back to get to the front of the card.
////            .addToBackStack(null)
////
////            // Commit the transaction.
////            .commit()
////    }
//}

interface MyFragmentInterface {
    fun someFragmentMethod()
}