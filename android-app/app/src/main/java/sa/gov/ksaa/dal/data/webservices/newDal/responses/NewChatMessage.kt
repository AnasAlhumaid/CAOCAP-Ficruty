package sa.gov.ksaa.dal.data.webservices.newDal.responses

data class NewChatMessage(
    var createDate: String? = null,
    var id: Int? = null,
    var message: String? = null,
    var nameId: Int? = null,
    var projectId: Int? = null,
    var projectName: String? = null,
    var sendById: Int? = null,
    var sendToId: Int? = null
)