package sa.gov.ksaa.dal.ui.fragments.starter

import android.os.Bundle
import android.view.View
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.ui.fragments.BaseFragment

class Slider4FeaturesFragment: BaseFragment(R.layout.fragment_slider_4_features){

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)
    }

    override fun onActivityCreated() {
        super.onActivityCreated()
        appBarLayout.visibility = View.VISIBLE
        setTitle("مميزات منصة دال")
    }

    override fun onDestroy() {
        super.onDestroy()
        appBarLayout.visibility = View.GONE
    }
}