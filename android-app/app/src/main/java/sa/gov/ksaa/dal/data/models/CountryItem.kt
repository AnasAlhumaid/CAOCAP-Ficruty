package sa.gov.ksaa.dal.data.models

class CountryItem(
    val code: String = "",
    val name: String = "",
    val dialCode: String = ""
){
    override fun toString(): String {
        return "${convertArabicNumbersToString(dialCode)} $name"
    }
    fun convertArabicNumbersToString(text: String?): String {
        val arabicToWesternMap = mapOf(
            '0' to '٠' , '1' to '١'   , '2' to  '٢','3'  to '٣', '4' to  '٤',
            '5' to  '٥', '6'  to '٦', '7' to  '٧', '8' to  '٨','9' to  '٩'
        )
        val builder = text?.let { StringBuilder(it.length) }
        if (text != null) {
            for (char in text) {
                if (builder != null) {
                    builder.append(arabicToWesternMap[char] ?: char)
                }
            }
        }
        return builder.toString()
    }
}
