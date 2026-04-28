package sa.gov.ksaa.dal.data.webservices.newDal.responses

import android.net.Uri
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import java.util.Date

data class FreelancerFile(
    val createdDate: Date?,
    val fileCategory: String?,
    val fileName: String?,
    val freelancerId: Int?,
    val id: Int?,
    val fileUrl: String?,
){
    companion object {
        const val WORK_CERTIFICATE_CATEGORY = "WORK CERTIFICATE"
        const val EDUCATION_CERTIFICATE_CATEGORY = "EDUCATION CERTIFICATE"
    }
    fun getCategory(): String{
        return when(fileCategory){
            WORK_CERTIFICATE_CATEGORY -> "شهادة خبرة"
            EDUCATION_CERTIFICATE_CATEGORY -> "شهادة أكاديمية"
            else -> ""
        }
    }

    fun toMyFile(): MyFile {
        val myfile = MyFile(name =fileName?: "", size = 0, null, null, Uri.parse(fileUrl), fileUrl)
        myfile.description = getCategory()


        myfile.freelancerFile = this
        return myfile
    }


}