package com.example.e_commerce_v2.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPager(fragmentActivity: FragmentActivity,val fragments:List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment =fragments[position]

}