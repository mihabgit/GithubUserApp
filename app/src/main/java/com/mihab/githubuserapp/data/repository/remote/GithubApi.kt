package com.mihab.githubuserapp.data.repository.remote

import com.mihab.githubuserapp.data.model.Profile
import com.mihab.githubuserapp.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("users")
    suspend fun getUsers(
        @Query("since") id: Int
    ): Response<List<User>>

    @GET("users/{userName}")
    suspend fun getUserByUserName(
        @Path("userName") userName: String
    ): Response<Profile>

}