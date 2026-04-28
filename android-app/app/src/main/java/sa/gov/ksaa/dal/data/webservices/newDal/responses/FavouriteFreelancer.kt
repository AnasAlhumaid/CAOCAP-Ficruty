package sa.gov.ksaa.dal.data.webservices.newDal.responses

import java.util.Date

data class FavouriteFreelancer(
    val createdDate: Date?,
    val favourite: Boolean?,
    val freelancerId: Int?,
    val isdeleted: Boolean?,
    val userId: Int?,

    val about: String?,
    val enrollmentYear: String?,
    val experince: String?,
    val freelancerNmae: String?,
    val freelancerLevel: String?,
    val graduationYear: String?,
    val nameOfUnivesity: String?,
    val typeOfCertificate: String?,
    val typeOfService: String?,
    val yearOfExperience: Int?,



    val clientCount: Int?,
    val country: String?,
    val experience: String?,
    val freelancerLastName: String?,
    val freelancerName: String?,
    val gender: String?,
    val id: Int?,
    val image: String?,
    val listOfCompletedProject: Any?,
    val listOfFiles: List<FreelancerFile>?,
    val listOfRating: List<Any>?,
    val listProject: Any?,
    val noOfProjectDone: Any?,
    val projectDone: Int?,
    val projectsUnderway: Int?,
    val rating: Float?,
    val reviewCount: Int?,
    val totalProject: Int?,
    val typeOfServices: String?,
    val username: String?,

    val favouriteBy: String?,
){
    fun isFemale(): Boolean {
        val femaleNames = listOf("حصة", "زهراء", "البندري", "وسام", "جميلة", "عائشة", "أمل", "جواهر",
            "رغد", "ضحى", "باسمة", "فاطمة", "حرم", "ريم", "مروة", "ميادة", "إيمان", "لولوة", "نورة",
            "هيا", "نجاة", "لطيفة", "ماجدة", "موزة", "عزيزة", "أمينة", "سارة", "ندى", "ريما", "داليا",
            "دانا", "دانية", "دلال", "دنيا", "خزامى", "خولة", "حسنية", "حفصة", "حليمة", "حوار", "حور",
            "حوراء", "حورية", "حياة", "جمانة", "جميلة", "جوري", "جيداء", "جيهان", "ثراء", "تانيا",
            "تمارة", "تماضر", "آلاء", "آمنة", "آية", "آيلا", "أبرار", "أجوان", "أروى", "أريج", "أزهار",
            "أسارير", "أسماء", "أسيل", "أشجان", "أطياف", "أغاريد", "أفراح", "أفنان", "ألطاف", "أم كلثوم",
            "أمانة", "أماني", "أمنية", "أمونة", "أميرة", "أميمة", "إخلاص", "إسراء", "إيثار", "إيناس",
            "ابتسام", "ابتهاج", "ابتهال", "اعتماد", "السعدية", "انتصار", "انشراح", "بارعة", "بتول",
            "بثينة", "بسمة", "بسيمة", "بشرى", "بلقيس", "بليغة", "بهيجة", "ملاك", "سوسن", "طيف", "سمية",
            "ذكرى", "لمى","خلود","مها")

        return gender == NewUser.FEMALE_USER_GENDER || gender == NewUser.FEMALE__USER_GENDER || femaleNames.any { name ->
            freelancerName
                ?.contains(name) == true
        }
    }

    fun getFullName(): String {
        return freelancerName?: "$freelancerNmae $typeOfServices"
    }
}