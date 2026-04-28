package sa.gov.ksaa.dal.data.webservices.newDal.responses

import java.util.Date

data class Favourite_aProjectRes(
    var createdDate: Date? = null,
    var favourite: Boolean? = null,
    var freelancerId: Int? = null,
    var isdeleted: Boolean? = null,
    var userId: Int? = null,
)