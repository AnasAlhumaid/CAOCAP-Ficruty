package sa.gov.ksaa.dal.data.webservices.newDal.responses

import android.net.Uri
import sa.gov.ksaa.dal.data.models.Certification
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import java.util.Date

data class NewFreelancer(
    var about: String? = null,
    var clientCount: Int? = null,
    var country: String? = null,
    var createdDate: Date? = null,
    var experience: String? = null,
    var favourite: Boolean? = null,
    var freelancerLastName: String? = null,
    var freelancerName: String? = null,
    var id: Int? = null,
    var listOfFiles: List<FreelancerFile>? = null,
    var listOfRating: List<RatingAndReview>? = null,
    var listNewProject: List<NewProject>? = null,
    var noOfProjectDone: Any? = null,
    var projectDone: Int? = null,
    var projectsUnderway: Int? = null,
    var rating: Float? = null,
    var reviewCount: Int? = null,
    var totalProject: Int? = null,
    var typeOfCertificate: String? = null,
    var typeOfServices: String? = null,


    var listOfServices: List<Skill>? = null,

//    var username: String? = null,

    var freelancerLevel: String? = null,
    var listProject: Any? = null,


    var gender: String? = null,
    var image: String? = null,
    var listOfClosedProject: List<ClosedProject>? = null,


    var dateOfBirth: String? = null,
    var email: String? = null,
//    var enrollmentYear: String? = null,

    var firstName: String? = null,
    var freelancerId: Int? = null,
    var graduationYear: String? = null,
    var lastName: String? = null,
    var message: Any? = null,
    var universityName: String? = null,
    var nationalId: String? = null,
    var password: String? = null,
    var phone: String? = null,
    var typeOfService: String? = null,
    var userType: String? = null,


    val favouriteBy: Any? = null,
    val listOfCompletedProject: List<OfCompletedProjectX>? = null,
) {
    fun getFullName(): String {
        return "${freelancerName ?: firstName} ${freelancerLastName ?: lastName}"
    }

    fun getExperiences(): String? {
        return experience ?: ""
    }

    var photoUri: Uri? = null
    var graduationDate: String? = null
    var enrollmentYear: String? = null
    var previousWorkDesc1: String? = null
    var previousWorkDesc0: String? = null
    var userId: Int? = null

    //    var type_of_service: String?= null
    var isSelected: Boolean = false
    var previousWorkfile0: MyFile? = null
    var previousWorkfile1: MyFile? = null
    var yearOfExperience: Int? = null
    var certification: Certification? = null

    fun isFemale(): Boolean {
        val femaleNames = listOf(
            "حصة",
            "زهراء",
            "البندري",
            "وسام",
            "جميلة",
            "عائشة",
            "أمل",
            "جواهر",
            "رغد",
            "ضحى",
            "باسمة",
            "فاطمة",
            "حرم",
            "ريم",
            "مروة",
            "ميادة",
            "إيمان",
            "لولوة",
            "نورة",
            "هيا",
            "نجاة",
            "لطيفة",
            "ماجدة",
            "موزة",
            "عزيزة",
            "أمينة",
            "سارة",
            "ندى",
            "ريما",
            "داليا",
            "دانا",
            "دانية",
            "دلال",
            "دنيا",
            "خزامى",
            "خولة",
            "حسنية",
            "حفصة",
            "حليمة",
            "حوار",
            "حور",
            "حوراء",
            "حورية",
            "حياة",
            "جمانة",
            "جميلة",
            "جوري",
            "جيداء",
            "جيهان",
            "ثراء",
            "تانيا",
            "تمارة",
            "تماضر",
            "آلاء",
            "آمنة",
            "آية",
            "آيلا",
            "أبرار",
            "أجوان",
            "أروى",
            "أريج",
            "أزهار",
            "أسارير",
            "أسماء",
            "أسيل",
            "أشجان",
            "أطياف",
            "أغاريد",
            "أفراح",
            "أفنان",
            "ألطاف",
            "أم كلثوم",
            "أمانة",
            "أماني",
            "أمنية",
            "أمونة",
            "أميرة",
            "أميمة",
            "إخلاص",
            "إسراء",
            "إيثار",
            "إيناس",
            "ابتسام",
            "ابتهاج",
            "ابتهال",
            "اعتماد",
            "السعدية",
            "انتصار",
            "انشراح",
            "بارعة",
            "بتول",
            "بثينة",
            "بسمة",
            "بسيمة",
            "بشرى",
            "بلقيس",
            "بليغة",
            "بهيجة",
            "ملاك",
            "سوسن",
            "طيف",
            "سمية",
            "ذكرى",
            "لمى",
            "خلود",
            "مها"
        )

        return gender == NewUser.FEMALE_USER_GENDER || gender == NewUser.FEMALE__USER_GENDER || femaleNames.any { name ->
            freelancerName
                ?.contains(name) == true
        }
    }


    fun getAverageRating(): Float {
        var sum =0.0f
        if (listOfRating == null)
            return 0.0f

        listOfRating!!.forEach {
            sum += it.userRating!!
        }
        return sum / listOfRating!!.size
    }
}