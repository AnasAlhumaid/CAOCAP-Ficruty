package sa.gov.ksaa.dal.data.webservices.newDal.responses

import sa.gov.ksaa.dal.data.models.Freelancer
import sa.gov.ksaa.dal.data.models.MyModel
import sa.gov.ksaa.dal.data.models.UploadedFile
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile


data class AddProjectFile(
    var type: String? = null,
    var graduation_date: String? = null,
    var institute_name: String? = null,
    var freelance_id: Int? = null,
    var freelancer: Freelancer? = null,
    var file_id: Int? = null,
    var file: UploadedFile? = null
) : MyModel() {
    var enrollmrent_date: String? = null
    var addFile: MyFile? = null



}