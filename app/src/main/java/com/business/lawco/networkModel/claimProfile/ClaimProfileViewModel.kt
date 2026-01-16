package com.business.lawco.networkModel.claimProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.business.lawco.R
import com.business.lawco.model.AttorneyProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClaimProfileViewModel @Inject constructor() : ViewModel() {

    private val allProfiles = mutableListOf(
        AttorneyProfile("1","Devon M. Thurtle Anderson","Adoption Attorney","Seattle","WA",R.drawable.pic1,false),
        AttorneyProfile("2","Devon M. Thurtle Anderson","Adoption Attorney","Seattle","WA",R.drawable.pic2,true),
        AttorneyProfile("3","Devon M. Thurtle Anderson","Adoption Attorney","Seattle","WA",R.drawable.pic3,false),
        AttorneyProfile("4","Devon M. Thurtle Anderson","Adoption Attorney","Seattle","WA",null,false),
        AttorneyProfile("5","Devon M. Thurtle Anderson","Adoption Attorney","Seattle","WA",null,true),

        AttorneyProfile("6","Michael Johnson","Criminal Defense Attorney","Portland","OR",null,false),
        AttorneyProfile("7","Sarah Williams","Corporate Attorney","San Francisco","CA",R.drawable.pic1,false),
        AttorneyProfile("8","Robert Davis","Personal Injury Attorney","Los Angeles","CA",R.drawable.pic1,false),
        AttorneyProfile("9","Jennifer Martinez","Immigration Attorney","New York","NY",R.drawable.pic1,false),
        AttorneyProfile("10","James Thompson","Real Estate Attorney","Chicago","IL",R.drawable.pic1,false)
    )

    private val searchResults = MutableLiveData<List<AttorneyProfile>>()
    fun getSearchResults(): LiveData<List<AttorneyProfile>> = searchResults

    private val claimResult = MutableLiveData<Boolean>()
    fun getClaimResult(): LiveData<Boolean> = claimResult

    // 🔍 Search
    fun searchAttorneyProfiles(query: String) {
        viewModelScope.launch {
            delay(500)

            val result = allProfiles.filter {
                it.fullName.contains(query, true) ||
                        it.attorneyType.contains(query, true) ||
                        it.city.contains(query, true) ||
                        it.state.contains(query, true)
            }

            searchResults.postValue(result)
        }
    }

    // ✅ Claim profile
    fun claimProfile(profileId: String) {
        viewModelScope.launch {
            delay(500)

            val index = allProfiles.indexOfFirst { it.profileId == profileId }
            if (index != -1) {
                allProfiles[index] = allProfiles[index].copy(isClaimed = true)
                claimResult.postValue(true)
            }
        }
    }

    // 📌 My Claimed Profiles
    fun getMyClaimedProfiles(): LiveData<List<AttorneyProfile>> {
        val liveData = MutableLiveData<List<AttorneyProfile>>()
        viewModelScope.launch {
            delay(300)
            liveData.postValue(allProfiles.filter { it.isClaimed })
        }
        return liveData
    }
}

