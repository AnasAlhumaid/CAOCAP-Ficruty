package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date

data class ChatMessage(
    var line_text: String? = null,
    var date_time: Date? = null,
    var from: NewUser? = null,
    var to: NewUser? = null,
)
