package sa.gov.ksaa.dal.data.webservices.newDal.responses

import java.util.Date

data class RatingAndReview(
    var freelancerUserId: Int? = null,
    var projectId: Int? = null,
    var id: Int? = null,
    var ratingDate: Date? = null,
    var ratingFrom: String? = null,
    var ratingTO: String? = null,
    var clientUserId: Int? = null,
    var userRating: Float? = null,
    var userReview: String? = null,
    var userRatingWebsite: Float? = null,
    var userReviewWebsite: String? = null,

)