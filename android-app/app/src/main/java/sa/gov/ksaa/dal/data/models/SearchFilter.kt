package sa.gov.ksaa.dal.data.models

 open class SearchFilter(
     var serviceTypes: MutableList<String>? = null,
     var freelancerLevels: MutableList<String>? = null,
     var services : MutableList<String>? = null,
     var freelancerRating: Float? = null,
     open var projectDuration: MutableList<String>? = null
) {
     companion object {
         const val SERVICE_LINGUISTIC_TRANSLATION = "الترجمة اللغوية"
         const val SERVICE_SPECIAL_TRANSLATION = "الترجمة التخصصية"

         const val SERVICE_VOICEOVER = "التعليق الصوتي"
         const val SERVICE_AUDIO_TRANSCRIPTION = "التفريغ الصوتي"

         const val SERVICE_QAFIAH = "العروض والقافية"
         const val SERVICE_CONTENT_CRAFTING = "صياغة المحتوى"

         const val SERVICE_TASHKEEL = "التشكيل"
         const val SERVICE_TADQUEEQ = "التدقيق اللغوي والتشكيل"

         const val SERVICE_NAQRAHA = "التدقيق"

         const val LEVEL_ACTIVE = "نشط"
         const val LEVEL_DISTINGUISHED = "متميز"
         const val LEVEL_EXPERT = "متمرس"
         const val LEVEL_PROFESSIONAL = "محترف"

         const val DURATION_1_TO_3_DAYS = "أيام 3-1"
         const val DURATION_1_TO_5_DAYS = "أيام 5-1"
         const val DURATION_1_WEEK = "أسبوع 1"
         const val DURATION_1_TO_3_WEEKS = "أسبوع 3-1"
         const val DURATION_1_MONTH = "شهر 1"
         const val DURATION_2_TO_3_MONTHS = "أشهر 3-2"
         const val DURATION_6_MONTHS = "أشهر 6"
         const val DURATION_LESS_THAN_YEAR = "أقل من سنة"
         const val DURATION_YEAR = "سنة"

     }


     override fun toString(): String {
         return "SearchFilter(\n" +
                 "    serviceTypes = $serviceTypes\n" +
                 "    freelancerLevels = $freelancerLevels,\n" +
                 "    freelancerRating = $freelancerRating,\n" +
                 "    projectDuration = $projectDuration,\n"
                 ")"
     }
 }

class FreelancerFilter(
    serviceTypes: MutableList<String>? = null,
    freelancerLevels: MutableList<String>? = null,
    services : MutableList<String>? = null,
    freelancerRating: Float? = null,
    projectDuration: MutableList<String>? = null
): SearchFilter(serviceTypes, freelancerLevels,services, freelancerRating,projectDuration){
    companion object {
        const val DURATION_1_TO_3_DAYS = "أيام 3-1"
        const val DURATION_1_TO_5_DAYS = "أيام 5-1"
        const val DURATION_1_WEEK = "أسبوع 1"
        const val DURATION_1_TO_3_WEEKS = "أسبوع 3-1"
        const val DURATION_1_MONTH = "شهر 1"
        const val DURATION_2_TO_3_MONTHS = "أشهر 3-2"
        const val DURATION_6_MONTHS = "أشهر 6"
        const val DURATION_LESS_THAN_YEAR = "أقل من سنة"
        const val DURATION_YEAR = "سنة"
    }

    override var projectDuration: MutableList<String>? = null
    init {
        this.projectDuration = projectDuration
    }
}

