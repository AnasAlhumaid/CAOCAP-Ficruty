package sa.gov.ksaa.dal.data.models

data class ProjectSearchFilter(
    var services: List<String>? = null,
    var freelancer_levels: List<String>? = null,
    var duration: List<String>? = null,
    var freelancer_rating: Int? = null,
    )