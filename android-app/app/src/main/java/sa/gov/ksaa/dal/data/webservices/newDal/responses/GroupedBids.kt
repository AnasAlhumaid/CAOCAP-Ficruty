package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class GroupedBids(
    val listOfTenderRequestDto: List<NewBid>,
    val projectId: Int?,
    val projectTitle: String?,
)