package sa.gov.ksaa.dal.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import sa.gov.ksaa.dal.ui.fragments.starter.ViewPagerFragment

class ViewPagerAdapter2(list: ArrayList<Fragment>, fragmentManager: FragmentManager, lifecycle: Lifecycle,viewPagerAdapter2: ViewPagerAdapter2.FragmentLifecycle ) : FragmentStateAdapter (fragmentManager, lifecycle, ){
    val fragmentList = list
    override fun getItemCount(): Int {

        return  fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return  fragmentList[position]
    }


    interface FragmentLifecycle {

        fun onResumeFragment()
    }

}