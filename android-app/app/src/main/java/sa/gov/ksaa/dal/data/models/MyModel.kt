package sa.gov.ksaa.dal.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.annotation.Excludes
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

@Entity(tableName = "my_model")
open class MyModel(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null
){

    @Transient
    val arabicLocale = Locale("ar", "SA")
    @Transient
     val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, arabicLocale)
    @Transient
    val detailedDateFormat = DateFormat.getDateInstance(DateFormat.LONG, arabicLocale)
    @Transient
    val numberFormat = NumberFormat.getInstance(arabicLocale)
    @Transient
    val simpleDateFormat = SimpleDateFormat("yy/M/dd", arabicLocale)
}