package com.mihab.githubuserapp.data.repository

import com.mihab.githubuserapp.data.model.Profile
import com.mihab.githubuserapp.data.model.User
import com.mihab.githubuserapp.data.repository.local.db.UserDatabase
import com.mihab.githubuserapp.data.repository.remote.RetrofitInstance

class UserRepository(
    val db: UserDatabase
) {
    suspend fun getUser(id: Int) =
        RetrofitInstance.api.getUsers(id)

    suspend fun getUserByUserName(userName: String) =
        RetrofitInstance.api.getUserByUserName(userName)

    suspend fun insertAllUser(list: List<User>) = db.getUserDao().insertAll(list)

    fun getAllUser() = db.getUserDao().getAllUsers()

    suspend fun insertProfile(profile: Profile) = db.getProfileDao().upsert(profile)

    fun getProfileByUserName(userName: String) = db.getProfileDao().getProfileByUserName(userName)

    fun getSearchResult(userName: String) = db.getUserDao().search(userName)

}