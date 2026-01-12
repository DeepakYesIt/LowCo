package com.business.lawco.fragment.consumerfragment.filterAttrony

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.business.lawco.R
import com.business.lawco.SessionManager
import com.business.lawco.adapter.consumer.AttorneySearchAdapter
import com.business.lawco.adapter.consumer.SelectedAttorneyAdapter
import com.business.lawco.base.BaseFragment
import com.business.lawco.databinding.FragmentSelectedAttorneyBinding
import com.business.lawco.fragment.filterDialog.BottomSheetAttorneyFilterDialog
import com.business.lawco.fragment.filterDialog.FilterApply
import com.business.lawco.model.consumer.AttorneyListDataModel
import com.business.lawco.model.consumer.AttorneyProfile
import com.business.lawco.model.consumer.Data
import com.business.lawco.networkModel.homeScreen.consumer.ConsumerHomeScreenViewModel
import com.business.lawco.utility.AppConstant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterAttorneyFragment : BaseFragment(), View.OnClickListener, FilterApply {
    lateinit var binding: FragmentSelectedAttorneyBinding
    private lateinit var adapterSelectedAttorney: SelectedAttorneyAdapter
    private var attorneyList: List<AttorneyProfile> = arrayListOf()
    private lateinit var attorneySearchAdapter: AttorneySearchAdapter
    lateinit var sessionManager: SessionManager
    private lateinit var consumerHomeScreenViewModel: ConsumerHomeScreenViewModel

    private var addressList = ArrayList<String>()
    private var areaOfPracticeList = ArrayList<String>()
    private var LOCATION_PERMISSION_REQUEST_CODE = 100
    var latitude: String? = null
    var longitude: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectedAttorneyBinding.inflate(
            LayoutInflater.from(requireActivity()),
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        consumerHomeScreenViewModel =
            ViewModelProvider(this)[ConsumerHomeScreenViewModel::class.java]
        binding.consumerHomeScreenViewModel = consumerHomeScreenViewModel


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //findNavController().navigate(R.id.action_selectedAttorneyFragment_to_consumerHomeFragment)
                    findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        addressList = requireArguments().getStringArrayList(AppConstant.ADDRESS_LIST)!!
        areaOfPracticeList =
            requireArguments().getStringArrayList(AppConstant.AREA_OF_PRACTICE_LIST)!!
        binding.tvSelectedAttorney.text = requireArguments().getString(AppConstant.FILTER_PAGE_NAME)

        binding.EtSearch.setHint("Search an " + binding.tvSelectedAttorney.text)

        Log.e("Select Address", addressList.toString())
        Log.e("Select Area of Practice", areaOfPracticeList.toString())

        binding.FilterRefreshList.setOnRefreshListener {
            // getAllAttorneyList("0.411111","50.66666",addressList,areaOfPracticeList)
            getAllAttorneyList(
                latitude.toString(), longitude.toString(), addressList, areaOfPracticeList
            )
        }

        attorneySearchAdapter =
            AttorneySearchAdapter(attorneyList, requireContext(), AppConstant.FILTER)
        binding.recyclerAttorneySearch.adapter = attorneySearchAdapter
        binding.recyclerAttorneySearch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.EtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(editTextAttorneySearch: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (editTextAttorneySearch != null) {
                    if (editTextAttorneySearch.isNotEmpty()) {
//                        binding.searchView.visibility = View.VISIBLE
                        val filterList: ArrayList<AttorneyProfile> = arrayListOf()
                        attorneyList.forEach {
                            if (it.full_name.contains(editTextAttorneySearch.toString(), ignoreCase = true)) {
                                filterList.add(it)
                            }
                        }

//                        if (filterList.size>0){
//                            attorneySearchAdapter.updateData(filterList, binding.searchView, binding.EtSearch)
//                            binding.tvNoData.visibility=View.GONE
//                            binding.recyclerAttorneySearch.visibility=View.VISIBLE
//                        }else{
//                            binding.tvNoData.visibility=View.VISIBLE
//                            binding.recyclerAttorneySearch.visibility=View.GONE
//                        }

                        if (filterList.isNotEmpty()){
                            adapterSelectedAttorney.updateData(filterList)
                            binding.rcvSelectedAttorney.visibility = View.VISIBLE
                            binding.textNoDataFound.visibility = View.GONE
                        }else{
                            binding.rcvSelectedAttorney.visibility = View.GONE
                            binding.textNoDataFound.visibility = View.VISIBLE
                        }
                    } else {
                        showList()
                    }
                } else {
                    showList()
                }
            }

            override fun afterTextChanged(editTextAttorneySearch: Editable?) {

            }

        })

        adapterSelectedAttorney = SelectedAttorneyAdapter(attorneyList, requireContext())
        binding.rcvSelectedAttorney.adapter = adapterSelectedAttorney
        binding.rcvSelectedAttorney.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapterSelectedAttorney.setOnSendRequest(object : SelectedAttorneyAdapter.OnSendRequest {
            override fun onSendRequest(position: Int, attorneyId: String, action: String) {
                sendRequest(position, attorneyId, action)
            }
        })
        binding.btBack.setOnClickListener(this)
        binding.btFilter.setOnClickListener(this)

        showCurrentLocation()

    }

    private fun showList(){
        if (attorneyList.isNotEmpty()){
            adapterSelectedAttorney.updateData(attorneyList)
            binding.rcvSelectedAttorney.visibility = View.VISIBLE
            binding.textNoDataFound.visibility = View.GONE
        }else{
            binding.rcvSelectedAttorney.visibility = View.GONE
            binding.textNoDataFound.visibility = View.VISIBLE
        }
    }

    private fun showCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        if (!sessionManager.isNetworkAvailable()) {
                            sessionManager.alertErrorDialog(getString(R.string.no_internet))
                        } else {
                            latitude = location.latitude.toString()
                            longitude = location.longitude.toString()
                            getAllAttorneyList(
                                latitude.toString(),
                                longitude.toString(),
                                addressList,
                                areaOfPracticeList
                            )
                        }

                    } else {
                        Log.e("Location Is ", "Null")
                    }
                }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.EtSearch.text.clear()
    }


    override fun onClick(item: View?) {
        when (item!!.id) {
            R.id.btBack -> {
                // findNavController().navigate(R.id.action_selectedAttorneyFragment_to_consumerHomeFragment)
                findNavController().navigateUp()
            }
            R.id.btFilter -> {
                val bottomSheetFragment = BottomSheetAttorneyFilterDialog(this)
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getAllAttorneyList(
        latitude: String, longitude: String,
        address: List<String>,
        areaOfPractice: List<String>
    ) {
        Log.e("LatLong", "$latitude ....... $longitude")
        showMe()
        lifecycleScope.launch {
            consumerHomeScreenViewModel.getAllAttorneyList(
                "0", latitude,
                longitude, areaOfPractice
            )
                .observe(viewLifecycleOwner) { jsonObject ->
                    dismissMe()
                    binding.FilterRefreshList.isRefreshing = false
                    val jsonObjectData = sessionManager.checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            val attorneyListResp = Gson().fromJson(jsonObjectData, AttorneyListDataModel::class.java)
                            attorneyList = attorneyListResp.data

                            if (attorneyList.isNotEmpty()){
                                adapterSelectedAttorney.updateData(attorneyList)
                                attorneySearchAdapter.updateData(attorneyList, binding.searchView, binding.EtSearch)
                                binding.rcvSelectedAttorney.visibility = View.VISIBLE
                                binding.textNoDataFound.visibility = View.GONE
                            }else{
                                binding.rcvSelectedAttorney.visibility = View.GONE
                                binding.textNoDataFound.visibility = View.VISIBLE
                            }


                        } catch (e: Exception) {
                            binding.rcvSelectedAttorney.visibility = View.GONE
                            binding.textNoDataFound.visibility = View.VISIBLE
                            Log.d("@Error","***"+e.message)
                        }
                    }else{
                        binding.rcvSelectedAttorney.visibility = View.GONE
                        binding.textNoDataFound.visibility = View.VISIBLE
                    }

                }
        }
    }

    fun sendRequest(
        position: Int,
        attorneyId: String,
        action: String,
    ) {
        Log.e("Request Power", action)
        showMe()

        lifecycleScope.launch {
            consumerHomeScreenViewModel.sendRequestToAttorney(
                attorneyId, action,
                sessionManager.getUserLat(),
                sessionManager.getUserLng()
            )
                .observe(viewLifecycleOwner) { jsonObject ->
                    dismissMe()
                    val jsonObjectData = sessionManager.checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {
                            if (action.toInt() == 0) {
                                attorneyList[position].request = 0
                            } else {
                                attorneyList[position].request = 1
                            }
                            adapterSelectedAttorney.notifyItemChanged(position)
                        } catch (e: Exception) {
                            Log.d("@Error","***"+e.message)
                        }
                    }
                }
        }
    }

    override fun apply(address: List<Data>, practice: List<String>, practiceId: MutableList<String>) {
        if (!address.isEmpty()) {
            getAllAttorneyList(
                address[0].latitude,
                address[0].longitude, listOf(),
                practice
            )
        } else {
            getAllAttorneyList(
                latitude.toString(),
                longitude.toString(), listOf(),
                practice
            )
        }
    }


}