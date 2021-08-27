package com.mihab.githubuserapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mihab.githubuserapp.data.model.Profile
import com.mihab.githubuserapp.data.model.User
import com.mihab.githubuserapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LocalDbViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun saveAllUser(userList: List<User>) = viewModelScope.launch {
        userRepository.insertAllUser(userList)
    }

    fun getSavedUser() = userRepository.getAllUser()

    fun saveProfile(profile: Profile) = viewModelScope.launch {
        userRepository.insertProfile(profile)
    }

    fun getProfileByUserName(userName: String) = userRepository.getProfileByUserName(userName)

    fun getSearchData(userName: String) = userRepository.getSearchResult(userName)

    class LocalDbViewModelProviderFactory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LocalDbViewModel(userRepository) as T
        }
    }
}