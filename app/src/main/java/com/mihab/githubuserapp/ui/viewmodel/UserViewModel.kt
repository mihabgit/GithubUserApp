package com.mihab.githubuserapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihab.githubuserapp.data.model.Profile
import com.mihab.githubuserapp.data.model.User
import com.mihab.githubuserapp.data.repository.UserRepository
import com.mihab.githubuserapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val user: MutableLiveData<Resource<List<User>>> = MutableLiveData()
    var userId = 0
    private var userResponse: List<User>? = null

    val profile: MutableLiveData<Resource<Profile>> = MutableLiveData()

    init {
        getUser()
    }

    fun getUser() = viewModelScope.launch {
        user.postValue(Resource.Loading())
        val response = userRepository.getUser(userId)
        user.postValue(handleUserResponse(response))
    }

    fun getUserByUserName(userName: String) = viewModelScope.launch {
        profile.postValue(Resource.Loading())
        val response = userRepository.getUserByUserName(userName)
        profile.postValue(handleProfileResponse(response))
    }

    private fun handleUserResponse(response: Response<List<User>>) : Resource<List<User>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                Log.d("Url : ", " "+response.raw().request.url)
                userId += 20
                if (userResponse == null) {
                    userResponse = resultResponse
                } else {
                    val oldUser = userResponse?.toMutableList()
                    oldUser?.addAll(resultResponse)
                }
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleProfileResponse(response: Response<Profile>) : Resource<Profile> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}