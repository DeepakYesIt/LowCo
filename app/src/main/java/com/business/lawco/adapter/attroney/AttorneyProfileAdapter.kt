package com.business.lawco.adapter.attroney


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.business.lawco.R
import com.business.lawco.model.AttorneyProfile

import de.hdodenhof.circleimageview.CircleImageView

class AttorneyProfileAdapter(
    private var profileList: ArrayList<AttorneyProfile>,
    private val context: Context
) : RecyclerView.Adapter<AttorneyProfileAdapter.ViewHolder>() {

    private var onClaimProfile: OnClaimProfile? = null

    interface OnClaimProfile {
        fun onClaimProfile(position: Int, profileId: String)
    }

    fun setOnClaimProfile(onClaimProfile: OnClaimProfile) {
        this.onClaimProfile = onClaimProfile
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProfileList(newList: ArrayList<AttorneyProfile>) {
        profileList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attorney_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profile = profileList[position]

        holder.tvAttorneyName.text = profile.fullName
        holder.tvAttorneyType.text = profile.attorneyType
        holder.tvLocation.text = "${profile.city}, ${profile.state}"

        // Load profile image
        if (profile.profileImage != null) {
            Glide.with(context)
                .load(profile.profileImage)
                .placeholder(R.drawable.empty_profile_icon)
                .error(R.drawable.empty_profile_icon)
                .into(holder.ivProfileImage)
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.empty_profile_icon)
        }

        // Handle claim status
        if (profile.isClaimed) {
            holder.btnClaimProfile.visibility = View.GONE
            holder.tvClaimedBadge.visibility = View.VISIBLE
        } else {
            holder.btnClaimProfile.visibility = View.VISIBLE
            holder.tvClaimedBadge.visibility = View.GONE
        }

        holder.btnClaimProfile.setOnClickListener {
            onClaimProfile?.onClaimProfile(position, profile.profileId)
        }
    }

    override fun getItemCount(): Int = profileList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val tvAttorneyName: TextView = itemView.findViewById(R.id.tvAttorneyName)
        val tvAttorneyType: TextView = itemView.findViewById(R.id.tvAttorneyType)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val btnClaimProfile: TextView = itemView.findViewById(R.id.btnClaimProfile)
        val tvClaimedBadge: TextView = itemView.findViewById(R.id.tvClaimedBadge)
    }
}