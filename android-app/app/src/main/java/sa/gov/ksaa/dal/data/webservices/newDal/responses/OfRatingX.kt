package sa.gov.ksaa.dal.data.webservices.newDal.responses

import java.util.Date

data class OfRatingX(
    val ratingDate: Date?,
    val ratingFrom: String?,
    val ratingTO: String?,
    val userId: Int?,
    val userRating: Int?,
    val userReview: String?
)