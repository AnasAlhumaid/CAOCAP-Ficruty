package sa.gov.ksaa.dal.data.webservices.newDal

import android.net.Uri
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FreelancerFile
import java.io.InputStream

class MyFile(var name: String, var size: Long, var inputStream: InputStream?, var mimeType: String?, var uri: Uri?, var imageRealPath: String?){
    var description: String = ""
    var freelancerFile: FreelancerFile? = null

    companion object {
        const val FILE_NAME_DISPLAY = "اسم الملف"
    }

    override fun toString(): String {
        return "MyFile(name='$name', size=$size, inputStream=$inputStream, mimeType=$mimeType, uri=$uri, imageRealPath=$imageRealPath)"
    }

}