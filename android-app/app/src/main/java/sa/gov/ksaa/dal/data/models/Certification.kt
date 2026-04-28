package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import java.util.Date

data class Certification(
    var type: String? = null,
    var graduation_date: String? = null,
    var institute_name: String? = null,
    var freelance_id: Int? = null,
    var freelancer: Freelancer? = null,
    var file_id: Int? = null,
    var file: UploadedFile? = null
) : MyModel() {
    var enrollmrent_date: String? = null
    var educationCertificate0: MyFile? = null
    var educationCertificate1: MyFile? = null

    fun getFormattedGraduationYear(): String? {
        return graduation_date?.let { dateFormat.format(it) }
    }
}