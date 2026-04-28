package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date

data class Project(
    var budget: String? = null,
    var created_date:  Date? = null,
    var duration: String? = null,
    var is_deleted: Boolean? = null,
    var is_public: Boolean? = true,
    var project_category: String? = null,
    var project_description: String? = null,
    var project_id: Int? = null,
    var project_name: String? = null,
    var updated_date: Date? = null,
    var user_id: Int? = null,
    var biddingCount: Int? = null,
    var quotations_deadline: Date? = null,
    var about : String? = null,
    var quotations_max_no : Int? = null,
    var isfavorite : Boolean? = null,
    var status : Int? = null,
    var client : NewUser? = null,
    var working_project : WorkingProject? = null,
    var freelancer_level : String? = null,
    var user : NewUser? = null,
    var comments : MutableList<CommentOnProject>? = null,
    var biddings : MutableList<Bid>? = null,
    var favorers : MutableList<Freelancer>? = null
) : MyModel() {
    fun getStatus(): String{
        return when(status){
            null -> "مشروع جديد"
            newProjectStatus -> "مشروع جديد"
            projectHasBidsStatus -> "عرض مرسل"
            ongoingProjectStatus -> "مشروع قيد التنفيذ"
            completedProjectStatus -> "مشروع مكتمل"
            cancelledProjectStatus -> "مشروع ملغي"
            else -> ""
        }
    }
    fun getShortCreatedDate(): String{
        return simpleDateFormat.format(created_date)
    }
    companion object {
        const val newProjectStatus = 1
        const val projectHasBidsStatus = 2
        const val ongoingProjectStatus = 3
        const val completedProjectStatus = 4
        const val cancelledProjectStatus = 5
    }
}