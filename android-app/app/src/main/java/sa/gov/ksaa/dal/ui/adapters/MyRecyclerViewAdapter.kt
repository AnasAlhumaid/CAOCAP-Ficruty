package sa.gov.ksaa.dal.ui.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

interface MyRecyclerViewAdapter {

    var context: Context
    val dateFormat: DateFormat
        get() = SimpleDateFormat("d MMM yyyy", Locale("ar", "SA"))
    val numberFormat: NumberFormat
        get() = NumberFormat.getInstance(Locale("ar", "SA"))

    fun setUserImage(uri: Any?, imageView: ImageView, userType: String, gender: String?) {
        var urii = uri
        val imageId = if (isUserClient(userType)) R.drawable.client_cercular
        else if (isUserFemale(gender)) R.drawable.freelancer_female_cercular_100
        else R.drawable.freelancer_male_cercular_100

        if (urii is String && urii.endsWith(".tmp"))
            urii = null
        if (urii == null)
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.accent))
        else imageView.imageTintList = null

        Glide.with(context)
            .load(urii)
            .placeholder(imageId)
            .centerCrop()
            .transform(
                CenterCrop(),
                CircleCrop()
            )//  RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius))
            .into(imageView)
    }

    fun isUserClient(userType: String): Boolean {
        if (userType.equals(NewUser.INDIVIDUAL_CLIENT_USER_TYPE, true))
            return true
        if (userType.equals(NewUser.ORG_CLIENT_USER_TYPE, true))
            return true
        if (userType.equals(NewUser.CLIENT_USER_TYPE, true))
            return true
        return false
    }

    fun isUserFemale(gender: String?): Boolean {
        if (gender == null)
            return false
        return gender == NewUser.FEMALE_USER_GENDER || gender == NewUser.FEMALE__USER_GENDER
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
    fun convertEnglishNumbersToString(text: String?): String {
        val arabicToEnglishMap = mapOf(
            '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
            '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
        )
        val builder = text?.let { StringBuilder(it.length) }
        if (text != null) {
            for (char in text) {
                if (builder != null) {
                    builder.append(arabicToEnglishMap[char] ?: char)
                }
            }
        }
        return builder.toString()
    }
}