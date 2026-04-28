package sa.gov.ksaa.dal.ui.fragments.starter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.adapters.ViewPagerAdapter2
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.HomeFragment
import sa.gov.ksaa.dal.ui.fragments.auth.UserLoginFragment
import sa.gov.ksaa.dal.ui.viewModels.ServicesVM


class ViewPagerFragment : BaseFragment(R.layout.fragment_slider_view_pager),ViewPagerAdapter2.FragmentLifecycle,MyFragmentInterface
   {
    private lateinit var viewPager: ViewPager2
    private lateinit var v0: View
//    private lateinit var v1: View
    private lateinit var v2: View
    private lateinit var v3: View
    private lateinit var v4: View
    private lateinit var v5: View
    private lateinit var v6: View

    private lateinit var vS: List<View>

    lateinit var skipBtn: Button

    val serviceVM: ServicesVM by viewModels()



    val requestObserver = { newResource: NewResource<List<ServicesModel>> ->
        newHandleSuccessOrErrorResponse(newResource,
            onSuccess = { services ->

//                serviceList = services
            }){

        }
    }




    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        val userStr = mainActivity.getPreferences(Context.MODE_PRIVATE)
            .getString("user", null)
        val fragmetContext: Context = requireContext()

        if (!userStr.isNullOrEmpty()) {
            _user = Gson().fromJson(userStr, NewUser::class.java)
            if (_user != null && _user!!.userId != null && _user!!.userId != 0) {
                if(_user!!.isFreelancer() && currentFreelancer == null){
                    // userId=5
                    val freelancerStr = mainActivity.getPreferences(Context.MODE_PRIVATE)
                        .getString("freelancer", null)
                    if (!freelancerStr.isNullOrEmpty()) {
                        currentFreelancer = Gson().fromJson(freelancerStr, NewFreelancer::class.java)
                        if (currentFreelancer != null){
                            activityVM.currentFreelancerMLD.postValue(currentFreelancer)
                            activityVM.setUser(_user)
                            findNavController()
                                .navigate(R.id.action_viewPagerFragment2_to_homeFragment)
                        }

                    }
                }
                else {
                    activityVM.setUser(_user)
                    findNavController()
                        .navigate(R.id.action_viewPagerFragment2_to_homeFragment)
                }
            }
        }
        else {
            viewPager = createdView.findViewById(R.id.viewPager2)

            val fragmentList = arrayListOf(
                Slider1LogoFragment(),
//                Fragment(R.layout.fragment_slider_2_intro_1),
                Fragment(R.layout.fragment_slider_2_intro_2),  // Slider2IntroFragment(),
                Slider3GaolsFragment(),
                Fragment(R.layout.fragment_slider_4_features),
            Slider5ServicesFragment(), // Fragment()
                UserLoginFragment(),

            )
            val viewPagerAdapter =
                ViewPagerAdapter2(fragmentList,childFragmentManager, lifecycle,this) // requireActivity().supportFragmentManager
            viewPager.adapter = viewPagerAdapter

            skipBtn = createdView.findViewById(R.id.skipBtn)

            v0 = createdView.findViewById(R.id.v0)
//            v1 = createdView.findViewById(R.id.v1)
            v2 = createdView.findViewById(R.id.v2)
            v3 = createdView.findViewById(R.id.v3)
            v4 = createdView.findViewById(R.id.v4)
            v5 = createdView.findViewById(R.id.v5)
            v6 = createdView.findViewById(R.id.v6)

            vS = listOf(v0, v2, v3, v4,v5,v6) // v5



            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    Log.i(
                        TAG,
                        "onPageScrollStateChanged: viewPager.currentItem = ${viewPager.currentItem}, state = $state"
                    )
                    if (viewPager.currentItem >= fragmentList.size - 1 && state == 1) {
                        val userJSON = mainActivity.getPreferences(Context.MODE_PRIVATE)
                            .getString("user", null)


                        if (userJSON != null) {
                            activityVM.setUser(Gson().fromJson(userJSON, NewUser::class.java))
                        }
//                        findNavController().navigate(R.id.action_viewPagerFragment2_to_homeFragment)
                    }
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    Log.i(
                        TAG,
                        "onPageScrolled: position = $position, positionOffset = $positionOffset, positionOffsetPixels = $positionOffset"
                    )
//                if(position == fragmentList.size-1)
//                    findNavController().navigate(R.id.action_viewPagerFragment2_to_slide5Fragment3)

                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.i(TAG, "onPageSelected: position = $position")
                    when (viewPager.currentItem) {
                        0, 1-> {
                            appBarLayout.visibility = View.GONE
                            createdView.setBackgroundResource(R.color.accent)
                            skipBtn.setTextColor(resources.getColor(android.R.color.white))
                        }

                        2,3 -> {
                            createdView.setBackgroundResource(R.color.fragment_bg_color)
                            skipBtn.setTextColor(resources.getColor(android.R.color.darker_gray))
                            appBarLayout.visibility = View.VISIBLE
                            setTitle("أهدافنا")
                        }

                        4 -> {
                            createdView.setBackgroundResource(R.color.fragment_bg_color)

                            skipBtn.setTextColor(resources.getColor(android.R.color.darker_gray))
                            setTitle("مميزات منصة دال")
                        }

                    5 -> {
                        createdView.setBackgroundResource(R.color.fragment_bg_color)

                        skipBtn.setTextColor(resources.getColor(android.R.color.darker_gray))
                        setTitle("الخدمات")

                        someFragmentMethod()



                    }
                        6 -> {
                            createdView.setBackgroundResource(R.color.fragment_bg_color)
                            skipBtn.setTextColor(resources.getColor(android.R.color.darker_gray))
                            setTitle("تسجيل الدخول")
                        }

                    }

                    when (viewPager.currentItem) {
                        0 -> {
                            v0.setBackgroundResource(R.drawable.bg_5_curved_borders_white_filled)
                            v2.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                        }

                        1 -> {
                            v0.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
//                            v1.setBackgroundResource(R.drawable.bg_5_curved_borders_white_filled)
                            v2.setBackgroundResource(R.drawable.bg_5_curved_borders_white_filled)
                            v3.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                        }

                        2 -> {
                            v2.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                            v3.setBackgroundResource(R.drawable.bg_5_curved_accent_filled)
                            v4.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                        }

                        3 -> {
                            v3.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                            v4.setBackgroundResource(R.drawable.bg_5_curved_accent_filled)
                            v5.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                        }

                        4 -> {
                            v4.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                            v5.setBackgroundResource(R.drawable.bg_5_curved_accent_filled)
                            v6.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                        }

                    5 -> {
                        v5.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
                        v6.setBackgroundResource(R.drawable.bg_5_curved_accent_filled)
//                        v6.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)

                    }
//                        6 -> {
//                            v5.setBackgroundResource(R.drawable.bg_5_curved_gray_dark_white_filled)
//                            v6.setBackgroundResource(R.drawable.bg_5_curved_accent_filled)
//
//                        }

                    }

//                    if (viewPager.currentItem > 0) {
//                        ivS[viewPager.currentItem - 1]
//                            .setBackgroundResource(R.color.current_slide_gray)
//                    }
//                    ivS[viewPager.currentItem].setBackgroundResource(R.color.green)
//                    if (viewPager.currentItem < ivS.size-1)
//                        ivS[viewPager.currentItem+1].setBackgroundResource(R.color.current_slide_gray)

                }
            })

            skipBtn.setOnClickListener {
                findNavController().navigate(R.id.action_viewPagerFragment2_to_homeFragment)
//                val intent = Intent (this@ViewPagerFragment, HomeFragment::class.java)
//                navigateToHomeFragment()
//                val intent = Intent(this, HomeFragment::class.java)
//                startActivity(intent)


            }
        }



    }


    fun navigateToHomeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current fragment with the home fragment
        val homeFragment = HomeFragment()
        fragmentTransaction.replace(R.id.homeFragment, homeFragment)

        // Optionally, add the transaction to the back stack
//        fragmentTransaction.addToBackStack(null)

        // Commit the transaction
        fragmentTransaction.commit()
        fragmentManager.popBackStack()

    }

//    fun changeColor(){
//        when(viewPager.currentItem){
//            0 -> {
////                iv1.setBackgroundColor()
//                iv1.setBackgroundColor(resources.getColor(R.color.green))
//                iv2.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv3.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv4.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv5.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv6.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//            }
//            1 -> {
//                iv1.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv2.setBackgroundColor(resources.getColor(R.color.green))
//                iv3.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv4.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv5.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv6.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//            }
//            2 -> {
//                iv1.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv2.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv3.setBackgroundColor(resources.getColor(R.color.green))
//                iv4.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv5.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv6.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//            }
//            3 -> {
//                setTitle("أهدافنا")
//                appBarLayout.visibility = View.VISIBLE
//                iv1.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv2.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv3.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv4.setBackgroundColor(resources.getColor(R.color.green))
//                iv5.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv6.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//            }
//            4 -> {
//                iv1.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv2.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv3.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv4.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv5.setBackgroundColor(resources.getColor(R.color.green))
//                iv6.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//            }
//            5 -> {
//                iv1.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv2.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv3.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv4.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv5.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_grey_50))
//                iv6.setBackgroundColor(resources.getColor(R.color.green))
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
        mainActivity.bottomNavigationView.visibility = View.GONE

        val serviceLD =

            serviceVM.getAllServices()



        serviceLD.observe(viewLifecycleOwner, requestObserver)
    }

       override fun onResumeFragment() {

       }

       override fun someFragmentMethod() {

                   val fragment = mainActivity.supportFragmentManager.findFragmentById(R.id.slider_5)
                   if (fragment is MyFragmentInterface) {
                       fragment.someFragmentMethod()
                   }

       }


   }