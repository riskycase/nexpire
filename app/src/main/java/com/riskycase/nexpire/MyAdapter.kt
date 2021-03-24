package com.riskycase.nexpire

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.riskycase.nexpire.ui.Expired
import com.riskycase.nexpire.ui.Upcoming

@Suppress("DEPRECATION")
internal class MyAdapter (
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
        ) :
        FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Upcoming()
            1 -> Expired()
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}