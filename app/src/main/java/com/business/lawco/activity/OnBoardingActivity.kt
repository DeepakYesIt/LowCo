package com.business.lawco.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.business.lawco.utility.AppConstant
import com.business.lawco.R
import com.business.lawco.SessionManager
import com.business.lawco.activity.authactivity.AuthActivity
import com.business.lawco.adapter.OnBoarding
import com.business.lawco.databinding.ActivityOnBoardingBinding
import com.business.lawco.model.OnBordingModel

class OnBoardingActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var  binding: ActivityOnBoardingBinding
    lateinit var sessionManager: SessionManager
    var adapter: OnBoarding? = null
    var datalist : ArrayList<OnBordingModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
         binding.imageBackOnBoarding.setOnClickListener(this)


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
               finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
        sessionManager = SessionManager(this)

        datalist.addAll(getOnBoardingData(sessionManager.getUserType()))
        adapter = OnBoarding(datalist)
        binding.viewpager.adapter = adapter
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.tabLayoutForIndicator, binding.viewpager) { _, _ ->


        }.attach()


        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0 || position == 1 || position == 2 ) {
                    binding.textBtnNext.text = "Next"
                }
            }
        })

        binding.textBtnNext.setOnClickListener {
            if (binding.viewpager.currentItem + 1 < adapter!!.itemCount) {
                binding.viewpager.currentItem += 1
            } else {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getOnBoardingData(userType: String?): ArrayList<OnBordingModel> {
        val data: ArrayList<OnBordingModel> = ArrayList()
        if (userType == AppConstant.ATTORNEY) {
            data.add(
                OnBordingModel(
                    R.drawable.discover_attorney,
                    "Expand Your Practice",
                    getString(R.string.attorneyOnboarding1)
                )
            )
            data.add(
                OnBordingModel(
                    R.drawable.skills_vector,
                    "Showcase Your Skills",
                    getString(R.string.attorneyOnboarding2)
                )
            )
            data.add(
                OnBordingModel(
                    R.drawable.management_attorney,
                    "Effortless Management",
                    getString(R.string.attorneyOnboarding3)
                )
            )
        } else if (userType == AppConstant.CONSUMER) {
            data.add(
                OnBordingModel(
                    R.drawable.discover_attorney,
                    "Discover Attorneys",
                    getString(R.string.consumerOnboarding1)
                )
            )
            data.add(
                OnBordingModel(
                    R.drawable.legal_expert,
                    getString(R.string.Choose_Your_Legal_Expert),
                    getString(R.string.consumerOnboarding2)
                )
            )
            data.add(
                OnBordingModel(
                    R.drawable.management,
                    "Confident Connections",
                    getString(R.string.consumerOnboarding3)
                )
            )
        }
        return data

    }

    override fun onClick(view: View?) {
       when(view!!.id){
           R.id.imageBackOnBoarding->{
               if (binding.viewpager.currentItem > 0) {
                   binding.viewpager.currentItem -= 1
               } else {
                   val intent = Intent(this, IdentityActivity::class.java)
                   startActivity(intent)
                   finish()
               }
           }
       }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, IdentityActivity::class.java)
        startActivity(intent)
        finish()
    }

}