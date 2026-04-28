package sa.gov.ksaa.dal.data.models

class ProjectsFilter(
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