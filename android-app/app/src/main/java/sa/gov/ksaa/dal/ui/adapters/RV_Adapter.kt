package sa.gov.ksaa.dal.ui.adapters

import java.text.DateFormat
import java.text.NumberFormat
import java.util.Locale

class RV_Adapter {
    companion object {
        val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("ar", "SA"))
        val numberFormat = NumberFormat.getInstance(Locale("ar", "SA"))
    }

}