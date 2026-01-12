package com.business.lawco.fragment.settingFragment.aboutus

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.business.lawco.R
import com.business.lawco.SessionManager
import com.business.lawco.base.BaseFragment
import com.business.lawco.databinding.FragmentAboutUsBinding
import com.business.lawco.networkModel.common.CommonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutUsFragment : BaseFragment() ,View.OnClickListener{
    lateinit var  binding: FragmentAboutUsBinding
    private lateinit var sessionManager: SessionManager

    private lateinit var commonViewModel: CommonViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            FragmentAboutUsBinding.inflate(LayoutInflater.from(requireActivity()), container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        commonViewModel = ViewModelProvider(this)[CommonViewModel::class.java]
        binding.commonViewModel = commonViewModel

//        val callback: OnBackPressedCallback =
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    findNavController().navigate(R.id.action_aboutUsFragment_to_settingsFragment)
//                }
//            }
//
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.arrowWhite.setOnClickListener(this)

        startGetAboutUs()

        binding.aboutRefresh.setOnRefreshListener{
            startGetAboutUs()
        }


    }

    override fun onClick(item: View?) {

        when(item!!.id){
            R.id.arrowWhite->{
              //  findNavController().navigate(R.id.action_aboutUsFragment_to_settingsFragment)
                findNavController().navigateUp()
            }
        }

    }

    // This function is used for start get About Us for database
    private fun startGetAboutUs(){
        if (!sessionManager.isNetworkAvailable()){
            binding.aboutRefresh.isRefreshing = false
            sessionManager.alertErrorDialog(getString(R.string.no_internet))
        }else{
            getAboutUs()
        }
    }

    // This function is used for get About Us for database
    private fun getAboutUs() {
        showMe()
        lifecycleScope.launch {
            commonViewModel.getContent("0")
                .observe(viewLifecycleOwner) { jsonObject ->
                    dismissMe()
                    binding.aboutRefresh.isRefreshing = false
                    val jsonObjectData = sessionManager.checkResponse(jsonObject)

                    if (jsonObjectData != null){
                        try {
                            val spanned = Html.fromHtml(jsonObjectData["content"].asString, Html.FROM_HTML_MODE_LEGACY)
                            binding.aboutUsText.text = spanned
                        }catch (e:Exception){
                            Log.d("@Error","***"+e.message)
                        }
                    }

                }
        }


    }


}