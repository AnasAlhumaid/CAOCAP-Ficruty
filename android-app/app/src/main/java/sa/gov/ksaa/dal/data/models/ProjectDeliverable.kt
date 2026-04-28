package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import java.util.Date

data class ProjectDeliverable (
    var description: String? = null,
    var submission_date: Date? = null,
    var file_id: Int? = null,
    var file: UploadedFile? = null,
    var working_project_id: Int? = null,
    var working_project: OngoingProject? = null,
        ): MyModel() {
    var userId: Int? = null
    var projectId: Int? = null
    var uploadfile: MyFile? = null
        }