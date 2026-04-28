package sa.gov.ksaa.dal.ui.fragments.freelancers

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.FreelancerFilter
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.adapters.DomainsListAdapter
import sa.gov.ksaa.dal.ui.adapters.FilterServiceAdapter
import sa.gov.ksaa.dal.ui.adapters.FreelancersRVadapter
import sa.gov.ksaa.dal.ui.adapters.ProjectsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseBottomSheetDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.ServicesVM

class FreelancersFilterBottomSheetModal(
    val onFilterSetListener: OnFilterSetListener,
    var filter: FreelancerFilter?,

    ) : BaseBottomSheetDialogFragment(R.layout.modal_bottom_sheet_freelancers_filter),
    FilterServiceAdapter.OnClickListener{
    val requestObserver = { newResource: NewResource<List<ServicesModel>> ->
        newHandleSuccessOrErrorResponse(newResource,
            onSuccess = { services ->

                serviceList = services
                domainsAdapter.setList(serviceList.map { it.name ?: "" })



            }){

        }
    }

    lateinit var clearBtn: Button

    lateinit var domainTV: TextView
    lateinit var serviceTitleLL: LinearLayout
    lateinit var domainsLL: LinearLayout
    lateinit var serviceTitleIV: ImageView
    lateinit var specialityRV: RecyclerView
    lateinit var domainsAdapter: FilterServiceAdapter
    lateinit var serviceList: List<ServicesModel>
    val serviceTypes = mutableListOf<String?>()
    val serviceSwitch = mutableListOf<String?>()

    val freelancersVM: FreelancersVM by viewModels()
    val serviceVM: ServicesVM by viewModels()
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)


        initViews(createdView)
        domainsAdapter =
            FilterServiceAdapter(mutableListOf(),this,serviceTypes.firstOrNull() ?: "",filter,null,requireContext())
        specialityRV.adapter = domainsAdapter
        updateUI()


    }



//    lateinit var lingTranslationS: SwitchMaterial
//    lateinit var specialTranslationS: SwitchMaterial
//
//    lateinit var voiceoverS: SwitchMaterial
//    lateinit var audioTranscriptionS: SwitchMaterial
//
//    lateinit var qafiahS: SwitchMaterial
//    lateinit var contentWritingS: SwitchMaterial
//
////    lateinit var tashkeelS: SwitchMaterial
//    lateinit var tadqeeqS: SwitchMaterial
//
//    lateinit var naqrahaS: SwitchMaterial


    lateinit var levelTV: TextView
    lateinit var levelTitleLL: LinearLayout
    lateinit var levelTitleIV: ImageView
    lateinit var freelancerLevel_LL: LinearLayout
    lateinit var freelancerRating_LL: LinearLayout
    lateinit var activeSW: SwitchMaterial
    lateinit var distinguishedtSW: SwitchMaterial
    lateinit var experiencedSW: SwitchMaterial
    lateinit var professionalSW: SwitchMaterial

    lateinit var ratingBarBtn: ImageView
    lateinit var ratingBarTitle: LinearLayout
    lateinit var searchBtn: Button
    lateinit var filterSwitches: List<SwitchMaterial>


    val freelancerLevels = mutableListOf<String>()


    fun initViews(createdView: View){

        ratingBarTitle = createdView.findViewById(R.id.ratingTitleFilterLL)
        freelancerRating_LL = createdView.findViewById(R.id.freelancerRatinglinear)
        ratingBarBtn = createdView.findViewById(R.id.ratingfreelancerIV)
        domainTV = createdView.findViewById(R.id.serviceTV)
        domainsLL = createdView.findViewById(R.id.servicesLL)
        serviceTitleIV = createdView.findViewById(R.id.serviceTitleIV)
        serviceTitleLL = createdView.findViewById(R.id.serviceTitleLL)
        specialityRV  = createdView.findViewById(R.id.recyclerView)

//        lingTranslationS = createdView.findViewById(R.id.lingTranslationS)
//        specialTranslationS = createdView.findViewById(R.id.specialTranslationS)
//        voiceoverS = createdView.findViewById(R.id.voiceoverS)
//        audioTranscriptionS = createdView.findViewById(R.id.audioTranscriptionS)
//        qafiahS = createdView.findViewById(R.id.qafiahS)
//        contentWritingS = createdView.findViewById(R.id.contentWritingS)
////        tashkeelS = createdView.findViewById(R.id.tashkeelS)
//        tadqeeqS = createdView.findViewById(R.id.tadqeeqS)
//        naqrahaS = createdView.findViewById(R.id.naqrahaS)

        levelTV = createdView.findViewById(R.id.levelTV)
        freelancerLevel_LL = createdView.findViewById(R.id.freelancerLevel_LL)
        levelTitleLL = createdView.findViewById(R.id.levelTitleLL)
        levelTitleIV = createdView.findViewById(R.id.levelTitleIV)

        activeSW = createdView.findViewById(R.id.activeSW)
        distinguishedtSW = createdView.findViewById(R.id.distinguishedtSW)
        experiencedSW = createdView.findViewById(R.id.experiencedSW)
        professionalSW = createdView.findViewById(R.id.professionalSW)


//        ratingBar = createdView.findViewById(R.id.ratingBar)


        filterSwitches = listOf(
//            lingTranslationS, specialTranslationS, voiceoverS, audioTranscriptionS, qafiahS,
//             tadqeeqS, contentWritingS, naqrahaS,
            activeSW, distinguishedtSW, experiencedSW, professionalSW)

        clearBtn = createdView.findViewById(R.id.clearBtn)
        searchBtn = createdView.findViewById(R.id.searchBtn)

    }

    private fun updateUI() {
        if (filter == null)
            filter = FreelancerFilter(mutableListOf(), mutableListOf(),
                mutableListOf(), null, mutableListOf(),
            )
        else {
//            lingTranslationS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_LINGUISTIC_TRANSLATION)
//            specialTranslationS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_SPECIAL_TRANSLATION)
//            voiceoverS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_VOICEOVER)
//            audioTranscriptionS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_AUDIO_TRANSCRIPTION)
//            qafiahS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_QAFIAH)
////            tashkeelS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_TASHKEEL)
//            tadqeeqS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_TADQUEEQ)
//            contentWritingS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_CONTENT_CRAFTING)
//            naqrahaS.isChecked = filter!!.serviceTypes!!.contains(SearchFilter.SERVICE_NAQRAHA)

            activeSW.isChecked = filter!!.freelancerLevels!!.contains(SearchFilter.LEVEL_ACTIVE)
            distinguishedtSW.isChecked = filter!!.freelancerLevels!!.contains(SearchFilter.LEVEL_DISTINGUISHED)
            experiencedSW.isChecked = filter!!.freelancerLevels!!.contains(SearchFilter.LEVEL_EXPERT)
            professionalSW.isChecked = filter!!.freelancerLevels!!.contains(SearchFilter.LEVEL_PROFESSIONAL)





//            ratingBar.rating = filter!!.freelancerRating?:0.0f
        }

        serviceTitleLL.setOnClickListener{
            serviceTitleIV.isSelected = !serviceTitleIV.isSelected
            togleVisisbilityAndGg(domainsLL, it)
        }
        levelTitleLL.setOnClickListener{
            levelTitleIV.isSelected = !levelTitleIV.isSelected
            togleVisisbilityAndGg(freelancerLevel_LL, it)
        }
        ratingBarTitle.setOnClickListener{
            ratingBarBtn.isSelected = !ratingBarBtn.isSelected
            togleVisisbilityAndGg(freelancerRating_LL, it)
        }

        clearBtn.setOnClickListener {
            clearAll()
        }

        levelTitleLL.setOnClickListener {
            levelTitleIV.isSelected = !levelTitleIV.isSelected
            togleVisisbilityAndGg(freelancerLevel_LL, it)
        }


        clearBtn.setOnClickListener {
            clearAll()
        }
        searchBtn.setOnClickListener {
            filter?.let {
//                if (lingTranslationS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_LINGUISTIC_TRANSLATION)
//
//
//                if (specialTranslationS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_SPECIAL_TRANSLATION)
//
//
//                if (voiceoverS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_VOICEOVER)
//
//
//                if (audioTranscriptionS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_AUDIO_TRANSCRIPTION)
//
//
//                if (qafiahS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_QAFIAH)
//
//
////                if (tashkeelS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_TASHKEEL)
////                else it.serviceTypes!!.remove(SearchFilter.SERVICE_TASHKEEL)
//
//                if (tadqeeqS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_TADQUEEQ)
//
//
//                if (contentWritingS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_CONTENT_CRAFTING)
//
//
//                if (naqrahaS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_NAQRAHA)

//                for (i in serviceTypes){
//                    if (i != null) {
//                        it.serviceTypes!!.add(i.toString())
//                        Log.w(javaClass.simpleName, "onViewCreated: 00000_user = ${i}")
//                    }
//                }
                if (serviceSwitch.firstOrNull()  != null){
                    it.services?.add(serviceSwitch.firstOrNull() ?: "" )

                }




                if (activeSW.isChecked) it.freelancerLevels!!.add(SearchFilter.LEVEL_ACTIVE)
                else it.freelancerLevels!!.remove(SearchFilter.LEVEL_ACTIVE)



                if (distinguishedtSW.isChecked) it.freelancerLevels!!.add(SearchFilter.LEVEL_DISTINGUISHED)
                else it.freelancerLevels!!.remove(SearchFilter.LEVEL_DISTINGUISHED)


                if (experiencedSW.isChecked) it.freelancerLevels!!.add(SearchFilter.LEVEL_EXPERT)
                else it.freelancerLevels!!.remove(SearchFilter.LEVEL_EXPERT)


                if (professionalSW.isChecked) it.freelancerLevels!!.add(SearchFilter.LEVEL_PROFESSIONAL)
                else it.freelancerLevels!!.remove(SearchFilter.LEVEL_PROFESSIONAL)


//                it.freelancerRating = if(ratingBar.rating <= 0) null else ratingBar.rating

//                Log.w(javaClass.simpleName, "onViewCreated: 00000_user = ${it.serviceTypes}")
            }

//            Log.w(javaClass.simpleName, "updateUI: filter = $filter")
//            Log.w(javaClass.simpleName, "onViewCreated: 00000_user = ${it.}")


            onFilterSetListener.onFilterSubmitted(filter)

            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        val serviceLD =

            serviceVM.getAllServices()



        serviceLD.observe(viewLifecycleOwner, requestObserver)





    }

    private fun clearAll() {
        filterSwitches.forEach{
            it.isChecked = false
        }
//        ratingBar.rating = 0f
        filter = null
    }

    companion object {
        const val TAG = "FreelancersModalBottomSheet"
    }
    interface OnFilterSetListener{
        fun onFilterSubmitted(searchFilter: FreelancerFilter?)
        fun onSwitchClicked(name: String)
    }

    override fun onSwitchClicked(name: String?) {
//onFilterSetListener.onSwitchClicked(name)
        serviceSwitch.add(name)

    }
}