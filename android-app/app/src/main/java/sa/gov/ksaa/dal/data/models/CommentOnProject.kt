package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date


data class CommentOnProject (
    var comment: String? = null,
    var project_id: Int? = null,
    var user_id: Int? = null,
    var user: NewUser? = null,
    var created_date: Date? = null,
    var updated_date: Date? = null,
): MyModel(){
    fun getFormatedCreatedAt(): String{
        return detailedDateFormat.format(created_date)?:""
    }
}