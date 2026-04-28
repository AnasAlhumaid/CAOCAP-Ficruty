package sa.gov.ksaa.dal.ui.fragments.projects.explore

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.ProjectsFilter
import sa.gov.ksaa.dal.data.models.SearchFilter
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.ui.adapters.FilterServiceAdapter
import sa.gov.ksaa.dal.ui.fragments.BaseBottomSheetDialogFragment
import sa.gov.ksaa.dal.ui.viewModels.ServicesVM

class ProjectsFilterBottomSheetModal(val onFilterSetListener: OnFilterSetListener,
var filter: ProjectsFilter?): BaseBottomSheetDialogFragment(R.layout.modal_bottom_sheet_projects_filter),FilterServiceAdapter.OnClickListener {



    val requestObserver = { newResource: NewResource<List<ServicesModel>> ->
        newHandleSuccessOrErrorResponse(newResource,
            onSuccess = { services ->

                serviceList = services
                domainsAdapter.setList(serviceList.map { it.name ?: "" })



            }){

        }
    }
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
        initViews(createdView)
        domainsAdapter =
            FilterServiceAdapter(mutableListOf(),this, serviceTypes.firstOrNull() ?: "",null,filter,requireContext())
        specialityRV.adapter = domainsAdapter
        updateUI()
    }

    val serviceVM: ServicesVM by viewModels()
    lateinit var specialityRV: RecyclerView
    lateinit var domainsAdapter: FilterServiceAdapter
    lateinit var serviceList: List<ServicesModel>
//    lateinit var serviceTV: TextView
    lateinit var serviceTitleLL: LinearLayout
    lateinit var serviceTitleIV: ImageView
    lateinit var servicesLL: LinearLayout


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

//    lateinit var levelTV: TextView
    lateinit var levelTitleLL: LinearLayout
    lateinit var levelTitleIV: ImageView
    lateinit var freelancerLevel_LL: LinearLayout
    lateinit var activeSW: SwitchMaterial
    lateinit var distinguishedtSW: SwitchMaterial
    lateinit var experiencedSW: SwitchMaterial
    lateinit var professionalSW: SwitchMaterial

//    lateinit var durationTV: TextView
    lateinit var durationTitleLL: LinearLayout
    lateinit var durationTitleIV: ImageView
    lateinit var milestoneLL: LinearLayout

    lateinit var dur_1_3_days_SW: SwitchMaterial
    lateinit var dur_1_5_days_SW: SwitchMaterial
    lateinit var dur_1_week_SW: SwitchMaterial
    lateinit var dur_1_3_week_SW: SwitchMaterial
    lateinit var dur_1_month_SW: SwitchMaterial
    lateinit var dur_2_3_monthes_SW: SwitchMaterial
    lateinit var dur_6_monthes_SW: SwitchMaterial
    lateinit var dur_les_year_SW: SwitchMaterial


    lateinit var ratingBar: RatingBar

    lateinit var clearBtn: Button
    lateinit var searchBtn: Button
    lateinit var filterSwitches: List<SwitchMaterial>
    val serviceTypes = mutableListOf<String?>()
    fun initViews(createdView: View) {
//        serviceTV = createdView.findViewById(R.id.serviceTV)
        serviceTitleLL = createdView.findViewById(R.id.serviceTitleLL)
        serviceTitleIV = createdView.findViewById(R.id.serviceTitleIV)
        servicesLL = createdView.findViewById(R.id.servicesLL)

//        lingTranslationS = createdView.findViewById(R.id.lingTranslationS)
//        specialTranslationS = createdView.findViewById(R.id.specialTranslationS)
//        voiceoverS = createdView.findViewById(R.id.voiceoverS)
//        audioTranscriptionS = createdView.findViewById(R.id.audioTranscriptionS)
//        qafiahS = createdView.findViewById(R.id.qafiahS)
//        contentWritingS = createdView.findViewById(R.id.contentWritingS)
////        tashkeelS = createdView.findViewById(R.id.tashkeelS)
//        tadqeeqS = createdView.findViewById(R.id.tadqeeqS)
//        naqrahaS = createdView.findViewById(R.id.naqrahaS)

//        levelTV = createdView.findViewById(R.id.levelTV)


        specialityRV  = createdView.findViewById(R.id.recyclerView)

        levelTitleLL = createdView.findViewById(R.id.levelTitleLL)
        levelTitleIV = createdView.findViewById(R.id.levelTitleIV)
        freelancerLevel_LL = createdView.findViewById(R.id.freelancerLevel_LL)

        activeSW = createdView.findViewById(R.id.activeSW)
        distinguishedtSW = createdView.findViewById(R.id.distinguishedtSW)
        experiencedSW = createdView.findViewById(R.id.experiencedSW)
        professionalSW = createdView.findViewById(R.id.professionalSW)

//        durationTV = createdView.findViewById(R.id.durationTV)
        durationTitleLL = createdView.findViewById(R.id.durationTitleLL)
        durationTitleIV = createdView.findViewById(R.id.durationTitleIV)
        milestoneLL = createdView.findViewById(R.id.milestoneLL)

        dur_1_3_days_SW = createdView.findViewById(R.id.dur_1_3_days_SW)
        dur_1_5_days_SW = createdView.findViewById(R.id.dur_1_5_days_SW)
        dur_1_week_SW = createdView.findViewById(R.id.dur_1_week_SW)
        dur_1_3_week_SW = createdView.findViewById(R.id.dur_1_3_week_SW)
        dur_1_month_SW = createdView.findViewById(R.id.dur_1_month_SW)
        dur_2_3_monthes_SW = createdView.findViewById(R.id.dur_2_3_monthes_SW)
        dur_6_monthes_SW = createdView.findViewById(R.id.dur_6_monthes_SW)
        dur_les_year_SW = createdView.findViewById(R.id.dur_les_year_SW)

//        ratingBar = createdView.findViewById(R.id.ratingBar)


        filterSwitches = listOf(
//            lingTranslationS, specialTranslationS, voiceoverS, audioTranscriptionS, qafiahS, tadqeeqS, contentWritingS, naqrahaS,
            activeSW, distinguishedtSW, experiencedSW, professionalSW,
            dur_1_3_days_SW, dur_1_5_days_SW, dur_1_week_SW, dur_1_3_week_SW, dur_1_month_SW, dur_2_3_monthes_SW, dur_6_monthes_SW,
            dur_les_year_SW
        )

        clearBtn = createdView.findViewById(R.id.clearBtn)
        searchBtn = createdView.findViewById(R.id.searchBtn)
    }

    private fun updateUI() {
        if (filter == null)
            filter = ProjectsFilter(mutableListOf(), mutableListOf(),
                mutableListOf(), null, mutableListOf())
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

            dur_1_3_days_SW.isChecked = filter!!.projectDuration!!.contains(SearchFilter.DURATION_1_TO_3_DAYS)
            dur_1_5_days_SW.isChecked = filter!!.projectDuration!!.contains(SearchFilter.DURATION_1_TO_5_DAYS)
            dur_1_week_SW.isChecked = filter!!.projectDuration!!.contains(SearchFilter.DURATION_1_WEEK)
            dur_1_3_week_SW.isChecked = filter!!.projectDuration!!.contains(SearchFilter.DURATION_1_TO_3_WEEKS)
            dur_1_month_SW.isChecked = filter!!.projectDuration!!.contains(SearchFilter.DURATION_1_MONTH)
            dur_2_3_monthes_SW.isChecked = filter!!.projectDuration!!.contains(SearchFilter.DURATION_2_TO_3_MONTHS)
            dur_6_monthes_SW.isChecked = filter!!.projectDuration!!.contains(SearchFilter.DURATION_6_MONTHS)
            dur_les_year_SW.isChecked = filter!!.projectDuration!!.contains(SearchFilter.DURATION_LESS_THAN_YEAR)
//            dur_year_SW.isChecked = filter!!.projectDuration!!.contains(ProjectsFilter.DURATION_YEAR)

//            ratingBar.rating = filter!!.freelancerRating?:0.0f
        }

        serviceTitleLL.setOnClickListener {
            serviceTitleIV.isSelected = !serviceTitleIV.isSelected
            togleVisisbilityAndGg(servicesLL, it)
        }

        levelTitleLL.setOnClickListener {
            levelTitleIV.isSelected = !levelTitleIV.isSelected
            togleVisisbilityAndGg(freelancerLevel_LL, it)
        }
        durationTitleLL.setOnClickListener {
            durationTitleIV.isSelected = !durationTitleIV.isSelected
            togleVisisbilityAndGg(milestoneLL, it)
        }
        clearBtn.setOnClickListener {
            clearAll()
        }
        searchBtn.setOnClickListener {
            filter?.let {
//                if (lingTranslationS.isChecked ) it.serviceTypes!!.add(SearchFilter.SERVICE_LINGUISTIC_TRANSLATION  )
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
//                if (contentWritingS.isChecked )
//                    it.serviceTypes!!.add(SearchFilter.SERVICE_CONTENT_CRAFTING)
//
//
//
//                if (naqrahaS.isChecked) it.serviceTypes!!.add(SearchFilter.SERVICE_NAQRAHA)

                if (serviceTypes.firstOrNull() != null)  it.services?.add(serviceTypes.firstOrNull() ?: "" )



                if (activeSW.isChecked) it.freelancerLevels!!.add(SearchFilter.LEVEL_ACTIVE)
                else it.freelancerLevels!!.remove(SearchFilter.LEVEL_ACTIVE)


                if (distinguishedtSW.isChecked) it.freelancerLevels!!.add(SearchFilter.LEVEL_DISTINGUISHED)
                else it.freelancerLevels!!.remove(SearchFilter.LEVEL_DISTINGUISHED)


                if (experiencedSW.isChecked) it.freelancerLevels!!.add(SearchFilter.LEVEL_EXPERT)
                else it.freelancerLevels!!.remove(SearchFilter.LEVEL_EXPERT)

                if (professionalSW.isChecked)
                    it.freelancerLevels!!.add(SearchFilter.LEVEL_PROFESSIONAL)
                else it.freelancerLevels!!.remove(SearchFilter.LEVEL_PROFESSIONAL)



                if (dur_1_3_days_SW.isChecked)
                    it.projectDuration!!.add(SearchFilter.DURATION_1_TO_3_DAYS)


                if (dur_1_5_days_SW.isChecked)
                    it.projectDuration!!.add(SearchFilter.DURATION_1_TO_5_DAYS)


                if (dur_1_week_SW.isChecked) it.projectDuration!!.add(SearchFilter.DURATION_1_WEEK)


                if (dur_1_3_week_SW.isChecked) it.projectDuration!!.add(SearchFilter.DURATION_1_TO_3_WEEKS)


                if (dur_1_month_SW.isChecked) it.projectDuration!!.add(SearchFilter.DURATION_1_MONTH)


                if (dur_2_3_monthes_SW.isChecked) it.projectDuration!!.add(SearchFilter.DURATION_2_TO_3_MONTHS)


                if (dur_6_monthes_SW.isChecked) it.projectDuration!!.add(SearchFilter.DURATION_6_MONTHS)


                if (dur_les_year_SW.isChecked) it.projectDuration!!.add(SearchFilter.DURATION_LESS_THAN_YEAR)

//
//                if (dur_year_SW.isChecked) it.projectDuration!!.add(ProjectsFilter.DURATION_YEAR)
//                else it.projectDuration!!.remove(ProjectsFilter.DURATION_YEAR)

//                it.freelancerRating = if(ratingBar.rating <= 0) null else ratingBar.rating
            }

            onFilterSetListener.onFilterSubmitted(filter)
            dismiss()
        }
    }

    private fun clearAll() {
        filterSwitches.forEach{
            it.isChecked = false
        }
//        ratingBar.rating = 0f
        filter = null
    }

    companion object {
        const val TAG = "ProjectsModalBottomSheet"
    }
    override fun onResume() {
        super.onResume()
        val serviceLD =

            serviceVM.getAllServices()



        serviceLD.observe(viewLifecycleOwner, requestObserver)





    }

    interface OnFilterSetListener{
        fun onFilterSubmitted(searchfilter: ProjectsFilter?)
    }

    override fun onSwitchClicked(name: String?) {
       serviceTypes.add(name)
    }
}