package com.example.e_commerce_v2.ui.home.fragments


import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce_v2.R
import com.example.e_commerce_v2.databinding.FragmentHomeBinding
import com.example.e_commerce_v2.ui.common.customviews.CircleView
import com.example.e_commerce_v2.ui.common.fragments.BaseFragment
import com.example.e_commerce_v2.ui.home.adapter.SalesAdAdapter
import com.example.e_commerce_v2.ui.home.model.SalesAdUIModel
import com.example.e_commerce_v2.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {


    override val viewModel: HomeViewModel by viewModels()


    override fun getLayoutId() = R.layout.fragment_home


    override fun init() {
        initViews()
        initViewModel()
    }

    private fun initViewModel() {

    }

    private fun initViews() {
        initSalesAdsView()

    }

    private fun initSalesAdsView() {

        val salesAds = listOf(
            SalesAdUIModel(
                title = "Super Flash Sale",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/e-commerce-8494e.appspot.com/o/temps%2FPromotion%20Image.png?alt=media&token=0e2c72ff-d39a-4bb0-b9da-4991a39c782e",
                endAt = Date(System.currentTimeMillis())
            ),
            SalesAdUIModel(
                title = " Limited offer",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/e-commerce-8494e.appspot.com/o/temps%2Fbig-sale-megaphone-banner-isolated-on-white-background-vector-sale-banner-discount-offer-market-advertising-illustration-2BNBMX2.jpg?alt=media&token=686207af-500e-48e6-bfad-ce752dcbb826",
                endAt = Date(System.currentTimeMillis())
            )
        )
        initializeIndicators(salesAds.size)
        val salesAdapter = SalesAdAdapter(lifecycleScope, salesAds)
        binding.saleAdsViewPager.apply {
            adapter = salesAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicators(position)
                }
            })
        }
        binding.saleAdsShimmerView.root.stopShimmer()
        binding.saleAdsShimmerView.root.visibility = View.GONE

    }


    private var indicators = mutableListOf<CircleView>()

    private fun initializeIndicators(count: Int) {
        for (i in 0 until count) {
            val circleView = CircleView(requireContext())
            val params = LinearLayout.LayoutParams(
                20, 20
            )
            params.setMargins(8, 0, 8, 0) // Margin between circles
            circleView.setLayoutParams(params)
            circleView.setRadius(10f) // Set radius
            circleView.setColor(
                if (i == 0) requireContext().getColor(R.color.primary_color) else requireContext().getColor(
                    R.color.neutral_grey
                )
            ) // First indicator is red
            circleView.setOnClickListener {
                binding.saleAdsViewPager.setCurrentItem(i, true)
            }
            indicators.add(circleView)
            binding.indicatorView.addView(circleView)
        }

    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until indicators.size) {
            indicators[i].setColor(
                if (i == position) requireContext().getColor(R.color.primary_color) else requireContext().getColor(
                    R.color.neutral_grey
                )
            )
        }
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}