package com.business.lawco.fragment.consumerfragment.attronydetails

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.business.lawco.R
import com.business.lawco.SessionManager
import com.business.lawco.adapter.ImageShowAdapter
import com.business.lawco.adapter.ImageUploadAdapter
import com.business.lawco.base.BaseFragment
import com.business.lawco.databinding.FragmentAttorneyDetailsBinding
import com.business.lawco.model.consumer.AttorneyProfile
import com.business.lawco.networkModel.homeScreen.consumer.ConsumerHomeScreenViewModel
import com.business.lawco.utility.AppConstant
import com.business.lawco.utility.PermissionUtils
import com.business.lawco.utility.ValidationData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AttorneyDetailsFragment : BaseFragment() , OnMapReadyCallback , View.OnClickListener{

    lateinit var binding: FragmentAttorneyDetailsBinding

    private var gMap: GoogleMap? = null

    private var connected : Int = 0
    private var requestSent : Int = 0

    lateinit var sessionManager: SessionManager
    private lateinit var consumerHomeScreenViewModel: ConsumerHomeScreenViewModel
    private var attorneyId : String = ""
    var phone :String = ""
    var attorneyDetail: AttorneyProfile?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttorneyDetailsBinding.inflate(LayoutInflater.from(requireActivity()) ,container , false)
        binding.mapFragment.onCreate(savedInstanceState)
        binding.mapFragment.getMapAsync(this)


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        initView()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        sessionManager = SessionManager(requireContext())
        consumerHomeScreenViewModel = ViewModelProvider(this)[ConsumerHomeScreenViewModel::class.java]
        binding.consumerHomeScreenViewModel = consumerHomeScreenViewModel
        binding.aboutContentBox.visibility = View.VISIBLE
        binding.contactBox.visibility = View.GONE

        val arrayListJson = requireArguments().getString(AppConstant.ATTORNEY_PROFILE)
        val type = object : TypeToken<AttorneyProfile>() {}.type
        attorneyDetail =  Gson().fromJson(arrayListJson, type)

        Log.e("Attorney Detail",attorneyDetail.toString())

        attorneyId = attorneyDetail?.id.toString()
        binding.tvAttorneyName.text = attorneyDetail?.full_name
        binding.tvAreaOfWork.text = attorneyDetail?.area_of_practice
        binding.tvLocation.text =attorneyDetail?.address
        if (attorneyDetail?.distance != null){ binding.tvDistance.text = ValidationData.formatDistance(attorneyDetail?.distance!!.toDouble()) }
        binding.tvAbout.text = attorneyDetail?.about
        binding.tvPhone.text = attorneyDetail?.phone.toString()
        binding.tvEmail.text = attorneyDetail?.email
        binding.tvAddress.text = attorneyDetail?.address
        connected = attorneyDetail?.connected!!
        requestSent = attorneyDetail?.request!!
        phone = attorneyDetail?.phone.toString()

        if (attorneyDetail!!.profile_picture_url!=null){
            Glide.with(requireActivity())
                .load(/*AppConstant.BASE_URL +*/ attorneyDetail?.profile_picture_url)
                .placeholder(R.drawable.demo_user) // jab tak image load ho rahi hai
                .error(R.drawable.demo_user)
                .into(binding.tvProfile)
        }else{
            binding.tvProfile.setImageResource(R.drawable.demo_user)
        }


        binding.btConnect.setOnClickListener(this)
        binding.btAbout.setOnClickListener(this)
        binding.btContact.setOnClickListener(this)
        binding.imageBack.setOnClickListener(this)
        binding.btnCall.setOnClickListener(this)
        binding.btnMessage.setOnClickListener(this)

        binding.zoomIn.setOnClickListener {
            gMap?.animateCamera(CameraUpdateFactory.zoomIn())
            Log.e("******","ZoomIn")
        }
        binding.zoomOut.setOnClickListener {
            gMap?.animateCamera(CameraUpdateFactory.zoomOut())
            Log.e("******","ZoomOut")
        }

        Log.e("Request Sent",requestSent.toString())
        Log.e("Connected ",connected.toString())


        if (connected == 1){
            binding.btConnect.visibility = View.GONE
            binding.callEnableBox.visibility = View.GONE
        }else{
            binding.btConnect.visibility = View.VISIBLE
            binding.callEnableBox.visibility = View.VISIBLE
        }

        if (attorneyDetail?.online_status == 1) {
            binding.showActive.visibility = View.VISIBLE
        }else{
            binding.showActive.visibility = View.GONE
        }

        if (requestSent == 1){
            binding.btConnect.text = "Sent"
            binding.btConnect.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            binding.btConnect.setBackgroundResource(R.drawable.sent_bg)
        } else{
            binding.btConnect.text = "Connect"
            binding.btConnect.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.btConnect.setBackgroundResource(R.drawable.orange_button_identity)
        }


        binding.btnRequest.setOnClickListener {
            showView()
        }

    }
    private fun showView(){
        val requestDialog = Dialog(requireContext())
        requestDialog.setContentView(R.layout.request_dialog)
        requestDialog.setCancelable(false)
        requestDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val etCardNumber: EditText = requestDialog.findViewById(R.id.etCardNumber)
        val etSubject: EditText = requestDialog.findViewById(R.id.etSubject)
        val btnYes: TextView = requestDialog.findViewById(R.id.yes)
        val tvUpload: TextView = requestDialog.findViewById(R.id.tvUpload)
        val btnCancel: TextView = requestDialog.findViewById(R.id.Cancel)
        val tvInfo: TextView = requestDialog.findViewById(R.id.tvInfo)
        val rcyData: RecyclerView = requestDialog.findViewById(R.id.rcyData)
        val imgUpload: ImageView = requestDialog.findViewById(R.id.imgUpload)
        val btnShow: LinearLayout = requestDialog.findViewById(R.id.btnShow)

        imgUpload.visibility = View.GONE
        tvInfo.visibility = View.GONE
        btnShow.visibility = View.GONE
        tvUpload.visibility = View.GONE

        etCardNumber.isEnabled = false
        etSubject.isEnabled = false

        rcyData.adapter= ImageShowAdapter(requireContext())

        btnYes.setOnClickListener {
            requestDialog.dismiss()
        }

        btnCancel.setOnClickListener {
            requestDialog.dismiss()
        }

        requestDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapFragment.onDestroy()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        if (attorneyDetail?.latitude!=null && attorneyDetail?.longitude!=null) {
            val currentLatLng = LatLng(
                attorneyDetail?.latitude!!.toDouble(), attorneyDetail?.longitude!!.toDouble()
            )
            gMap?.addMarker(MarkerOptions().position(currentLatLng).title("My Location"))
            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        }

    }


    @SuppressLint("SetTextI18n")
    override fun onClick(item: View?) {

        when(item!!.id){
            R.id.imageBack->{
               findNavController().navigateUp()
            }

            R.id.btConnect->{
                if (requestSent == 1){
                    Toast.makeText(requireContext(),R.string.already_req, Toast.LENGTH_SHORT).show()
                }else{
                    sendRequest(attorneyId ,"1")
                }
            }

            R.id.btnCall -> {
                if (connected == 1){
                    callToAttorney()
                }
            }

            R.id.btnMessage -> {
                if (connected == 1){
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("sms:$phone")
                    startActivity(intent)
                }
            }

            R.id.btContact ->{
                if (connected == 1){
                    showContact()
                }
            }

            R.id.btAbout ->{

                showAbout()
            }
       }

    }

    private fun callToAttorney() {
        val dialIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phone")
        }

        if (PermissionUtils.isCallPermissionGranted(requireContext())) {
            startActivity(dialIntent)
        }else{
           sessionManager.allowCallPermissionAlertDialog()
        }

    }

    private fun showAbout(){
        binding.btAbout.setBackgroundResource(R.drawable.orange_button_identity)
        binding.btAbout.setTextColor(requireContext().getColor(R.color.white))
        binding.btContact.background = null
        binding.btContact.setTextColor(requireContext().getColor(R.color.inactive_text_color))
        binding.aboutContentBox.visibility = View.VISIBLE
        binding.contactBox.visibility = View.GONE
    }

    private fun showContact(){
        binding.btContact.setBackgroundResource(R.drawable.orange_button_identity)
        binding.btContact.setTextColor(requireContext().getColor(R.color.white))
        binding.btAbout.background = null
        binding.btAbout.setTextColor(requireContext().getColor(R.color.inactive_text_color))
        binding.aboutContentBox.visibility = View.GONE
        binding.contactBox.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun sendRequest(
        attorneyId: String,
        action: String,
    ) {
       showMe()
        lifecycleScope.launch {
            consumerHomeScreenViewModel.sendRequestToAttorney(attorneyId, action,
                sessionManager.getUserLat(),
                sessionManager.getUserLng())
                .observe(viewLifecycleOwner) { jsonObject ->
                    dismissMe()
                    val jsonObjectData = sessionManager.checkResponse(jsonObject)
                    if (jsonObjectData != null) {
                        try {

                            if (action.toInt() == 1){
                                binding.btConnect.text = "Sent"
                                binding.btConnect.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.black
                                    )
                                )
                                binding.btConnect.setBackgroundResource(R.drawable.sent_bg)
                                requestSent = 1
                            } else{
                                binding.btConnect.text = "Connect"
                                binding.btConnect.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.white
                                    )
                                )
                                binding.btConnect.setBackgroundResource(R.drawable.orange_button_identity)
                                requestSent = 0
                            }

                        } catch (e: Exception) {
                            Log.d("@Error","***"+e.message)
                        }
                    }
                }
        }
    }


}