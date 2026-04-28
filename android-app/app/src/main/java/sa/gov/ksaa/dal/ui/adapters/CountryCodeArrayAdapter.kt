package sa.gov.ksaa.dal.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.CountryItem


class CountryCodeArrayAdapter(val countryList: List<CountryItem>, val ctxt: Context):
    ArrayAdapter<CountryItem>(ctxt, R.layout.list_item_spinner_country_code, R.id.codeTV, countryList) {

    override fun getCount(): Int {
        return countryList.size
    }

    override fun getItem(position: Int): CountryItem {
        return countryList[position]
    }

    lateinit var codeTV: TextView
    lateinit var countryTV: TextView
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viu = super.getView(position, convertView, parent)
        val countryItem = getItem(position)
        codeTV = viu.findViewById(R.id.codeTV)
        codeTV.text = convertArabicNumbersToString(countryItem.dialCode)
        countryTV = viu.findViewById(R.id.countryTV)
        countryTV.text = countryItem.name
        return viu
    }

    override fun getContext(): Context {
        return ctxt
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