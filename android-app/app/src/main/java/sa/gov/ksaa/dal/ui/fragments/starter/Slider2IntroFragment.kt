package sa.gov.ksaa.dal.ui.fragments.starter

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import sa.gov.ksaa.dal.R

class Slider2IntroFragment: Fragment(R.layout.fragment_slider_2_intro_1){

    lateinit var aboutText: TextView
    lateinit var readMoreTV: TextView
    var isShortText = true
    lateinit var shortText: String
    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
//        aboutText = createdView.findViewById(R.id.aboutText)
//        readMoreTV = createdView.findViewById(R.id.readMoreTV)
//        shortText = aboutText.text.toString()
//        readMoreTV.setOnClickListener {
//            if (isShortText) {
//                aboutText.text = "منصة وطنية رقمية تقدم حزمة واسعة من خدمات اللغة العربية الداعمة والمساندة في مجال العمل الحر، تهدف إلى تلبية احتياجات المستفيدين من الأفراد والمؤسسات، وتعمل على ربط المستفيدين بالمستقلين أصحاب العمل الحر من خلال العمل في مشاريع المنصة المستهدفة."
//                isShortText = false
//                } else {
//                aboutText.text = shortText
//                isShortText = true
//            }
//
//        }
    }
}