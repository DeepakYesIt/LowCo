package com.business.lawco.networkModel.homeScreen.consumer

import com.google.gson.JsonObject
import com.business.lawco.networkModel.BaseResponse
import com.business.lawco.networkModel.SingleLiveEvent

interface ConsumerHomeRepository {

    suspend fun getUserProfile(): SingleLiveEvent<BaseResponse<JsonObject>>

    suspend fun getAllRegisterLocation(): SingleLiveEvent<BaseResponse<JsonObject>>

    suspend fun getAreaOfPractice(): SingleLiveEvent<BaseResponse<JsonObject>>

    suspend fun getAllAttorneyList(
        isConnected: String,
        latitude: String,
        longitude: String,
       /* address: List<String>,*/
        areaOfPractice: List<String>
    ): SingleLiveEvent<BaseResponse<JsonObject>>

    suspend fun sendRequestToAttorney(
        attorneyId: String,
        action: String,
        latitude:String,
        longitude:String
    ): SingleLiveEvent<BaseResponse<JsonObject>>


}
