package com.business.lawco.adapter.consumer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.business.lawco.R
import com.business.lawco.databinding.SelectedAttorneyListBinding
import com.business.lawco.model.consumer.AttorneyProfile
import com.business.lawco.utility.AppConstant
import com.business.lawco.utility.ValidationData

class SelectedAttorneyAdapter(
    private var selectedAttorneyList: List<AttorneyProfile>,
    var requireContext: Context
) : RecyclerView.Adapter<SelectedAttorneyAdapter.Holder>() {

    interface OnSendRequest {
        fun onSendRequest(position: Int, attorneyId: String, action: String)
    }

    private var listener: OnSendRequest? = null

    fun setOnSendRequest(listener: OnSendRequest) {
        this.listener = listener
    }

    class Holder(val binding: SelectedAttorneyListBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataItem: AttorneyProfile, requireContext: Context) {

            binding.btViewProfile.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(AppConstant.ATTORNEY_PROFILE, Gson().toJson(dataItem))
                Navigation.findNavController(it).navigate(
                    R.id.action_selectedAttorneyFragment_to_attorneyDetailsFragment,
                    bundle
                )
            }



            binding.tvAttorneyName.text = dataItem.full_name
            binding.tvTypeofAttorney.text = dataItem.area_of_practice+" Attorneys"
            binding.tvAddress.text = dataItem.address
            if (dataItem.distance != null) {
                binding.tvDistance.text =
                    ValidationData.formatDistance(dataItem.distance.toDouble())
            }

            Glide.with(requireContext)
                .load(AppConstant.BASE_URL + dataItem.profile_picture_url)
                .placeholder(R.drawable.demo_user)
                .into(binding.tvProfile)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding =
            SelectedAttorneyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return selectedAttorneyList.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val dataItem = selectedAttorneyList[position]

        if (dataItem.connected == 1) {
            holder.binding.btnConnect.visibility = View.GONE
        } else {
            holder.binding.btnConnect.visibility = View.VISIBLE
        }

        if (dataItem.request == 0) {
            holder.binding.btnConnect.text = "Connect"
            holder.binding.btnConnect.setBackgroundResource(R.drawable.orange_button_identity)
            holder.binding.btnConnect.setTextColor(getColor(requireContext, R.color.white))
        } else {
            holder.binding.btnConnect.text = "Sent"
            holder.binding.btnConnect.setBackgroundResource(R.drawable.sent_bg)
            holder.binding.btnConnect.setTextColor(getColor(requireContext, R.color.black))
        }


        holder.binding.btnConnect.setOnClickListener {
            if (dataItem.request == 0) {
                listener?.onSendRequest(position, dataItem.id.toString(), "1")
            } else {
               // listener?.onSendRequest(position, dataItem.id.toString(), "0")
                Toast.makeText(requireContext,R.string.already_req, Toast.LENGTH_SHORT).show()
            }
        }

        holder.bind(dataItem, requireContext)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(attorneyList: List<AttorneyProfile>) {
        selectedAttorneyList = attorneyList
        notifyDataSetChanged()
    }


}