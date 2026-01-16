package com.business.lawco.fragment.attronyfragment.claimMyProfile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.business.lawco.R
import com.business.lawco.SessionManager
import com.business.lawco.adapter.attroney.AttorneyProfileAdapter
import com.business.lawco.base.BaseFragment
import com.business.lawco.databinding.FragmentClaimProfileBinding
import com.business.lawco.model.AttorneyProfile
import com.business.lawco.networkModel.claimProfile.ClaimProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ClaimProfileFragment : BaseFragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var binding: FragmentClaimProfileBinding
    private lateinit var claimProfileViewModel: ClaimProfileViewModel
    private lateinit var attorneyProfileAdapter: AttorneyProfileAdapter

    private val attorneyProfileList = ArrayList<AttorneyProfile>()
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClaimProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        claimProfileViewModel = ViewModelProvider(this)[ClaimProfileViewModel::class.java]

        setupRecyclerView()
        setupSearchListener()
        observeSearchResults()
        observeClaimResult()

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.ivSearch.setOnClickListener {
            performSearch()
        }

        claimProfileViewModel.searchAttorneyProfiles("")

    }



    private fun setupRecyclerView() {
        attorneyProfileAdapter = AttorneyProfileAdapter(attorneyProfileList, requireActivity())
        binding.rvSearchResults.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSearchResults.adapter = attorneyProfileAdapter

        attorneyProfileAdapter.setOnClaimProfile(object : AttorneyProfileAdapter.OnClaimProfile {
            override fun onClaimProfile(position: Int, profileId: String) {
                showClaimConfirmationDialog(position, profileId)
            }
        })
    }


    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim()
            }

            override fun afterTextChanged(s: Editable?) {
                if (searchQuery.length >= 2) {
                    performSearch()
                } else if (searchQuery.isEmpty()) {
                    claimProfileViewModel.searchAttorneyProfiles("")
                }
            }
        })
    }


    private fun performSearch() {
        claimProfileViewModel.searchAttorneyProfiles(searchQuery)
    }

    private fun observeSearchResults() {
        claimProfileViewModel.getSearchResults().observe(viewLifecycleOwner) { list ->

            attorneyProfileList.clear()
            attorneyProfileList.addAll(list)

            if (attorneyProfileList.isNotEmpty()) {
                binding.rvSearchResults.visibility = View.VISIBLE
                binding.emptyStateContainer.visibility = View.GONE
                attorneyProfileAdapter.updateProfileList(attorneyProfileList)
            } else {
                showEmptyState()
            }
        }
    }

    private fun observeClaimResult() {
        claimProfileViewModel.getClaimResult().observe(viewLifecycleOwner) { success ->
            if (success) {
                val pos = attorneyProfileList.indexOfFirst { it.profileId == getLastClaimedId() }
                if (pos != -1) {
                    attorneyProfileList[pos] =
                        attorneyProfileList[pos].copy(isClaimed = true)
                    attorneyProfileAdapter.notifyItemChanged(pos)
                }
            }
        }
    }

    // simple helper
    private fun getLastClaimedId(): String {
        return attorneyProfileList.first { !it.isClaimed }.profileId
    }

    // ---------------- Empty State ----------------

    private fun showEmptyState() {
        binding.rvSearchResults.visibility = View.GONE
        binding.emptyStateContainer.visibility = View.VISIBLE
    }

    private fun showClaimConfirmationDialog(position: Int, profileId: String) {
        val confirmDialog = Dialog(requireContext())
        confirmDialog.setContentView(R.layout.alert_claim_confirmation_dialog)
        confirmDialog.setCancelable(false)
        confirmDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val textCancel = confirmDialog.findViewById<TextView>(R.id.textCancel)
        val textProceed = confirmDialog.findViewById<TextView>(R.id.textProceed)

        textCancel.setOnClickListener {
            confirmDialog.dismiss()
            showVerificationSuccessDialog()
        }

        textProceed.setOnClickListener {
               confirmDialog.dismiss()
              claimProfileViewModel.claimProfile(profileId)
        }

        confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        confirmDialog.show()
    }

    private fun showVerificationSuccessDialog() {
        val successDialog = Dialog(requireContext())
        successDialog.setContentView(R.layout.alert_verification_success_dialog)
        successDialog.setCancelable(false)
        successDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnGoToProfile = successDialog.findViewById<TextView>(R.id.btnGoToProfile)

        btnGoToProfile.setOnClickListener {
            successDialog.dismiss()
            showVerificationNotCompletedDialog()
        }

        successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        successDialog.show()

    }

    private fun showVerificationNotCompletedDialog() {
        val successDialog = Dialog(requireContext())
        successDialog.setContentView(R.layout.alert_verification_not_completed_dialog)
        successDialog.setCancelable(false)
        successDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnUpdateInformation = successDialog.findViewById<TextView>(R.id.btnUpdateInformation)

        btnUpdateInformation.setOnClickListener {
            successDialog.dismiss()

        }

        successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        successDialog.show()
    }

}

