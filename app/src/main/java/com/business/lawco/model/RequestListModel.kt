package com.business.lawco.model

data class RequestListModel(
    var message  : String,
    var success  : Boolean,
    var data : ArrayList<RequestData>?,
)

data class RequestData(
var consumer_id : Int,
var request_id : String,
var profile_picture_url : String?,
var name : String?,
var phone : String?,
var email : String?,
var address : String?,
var latitude : String?,
var longitude : String?,
var distance : String?,
var updated_at : String,
var formatted_date : String,
var attorney_area_of_practice : String?="",
var formatted_time : String
)