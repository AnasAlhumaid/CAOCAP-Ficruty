package sa.gov.ksaa.dal.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment

class ExploreVPadapter(val fragments: ArrayList<BaseFragment>,
                       fragmentManager: FragmentManager,
                       lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): BaseFragment {
        return fragments.get(position)
    }
}