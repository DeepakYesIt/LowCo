package com.business.lawco.adapter.attroney

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.business.lawco.R
import com.business.lawco.databinding.CreditsItemBinding
import com.business.lawco.model.RequestData
import com.business.lawco.utility.ValidationData

class RequestListAdapter(var datalist :  List<RequestData>, val requireActivity: FragmentActivity): RecyclerView.Adapter<RequestListAdapter.Holder>() {

    interface OnRequestAction {
        fun onRequestAction(position : Int ,requestId: String, action: String)
    }

    private var listener: OnRequestAction? = null

    fun setOnRequestAction(listener: OnRequestAction) {
        this.listener = listener
    }

    class Holder(val binding: CreditsItemBinding):RecyclerView.ViewHolder(binding.root){

       fun bind(dataItem: RequestData, requireActivity: FragmentActivity) {

           binding.tvConsumerName.text = dataItem.name
//           binding.tvDistance.text = ValidationData.formatDistance(dataItem.distance.toDouble())
           val distanceValue = dataItem.distance?.toDoubleOrNull() ?: 0.0
           binding.tvDistance.text = ValidationData.formatDistance(distanceValue)


           if (dataItem.attorney_area_of_practice!=null) {
               binding.tvNeed.text = "Looking For "+dataItem.attorney_area_of_practice+ " Attorney"
           }

           if (dataItem.profile_picture_url!=null){
               Glide.with(requireActivity)
                   .load(/*AppConstant.BASE_URL + */dataItem.profile_picture_url)
                   .placeholder(R.drawable.demo_user)
                   .into(binding.tvProfile)
           }else{
               binding.tvProfile.setImageResource(R.drawable.demo_user)
           }


       }

   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CreditsItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)

      return Holder(binding)
    }

    override fun getItemCount(): Int {
       return  datalist.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val dataItem = datalist[position]

        holder.binding.btAccept.setOnClickListener(){
            listener?.onRequestAction(position,dataItem.request_id,"accepted")
        }

        holder.binding.btDecline.setOnClickListener(){
            listener?.onRequestAction(position,dataItem.request_id,"rejected")
        }

        holder.bind(dataItem,requireActivity)
    }


}