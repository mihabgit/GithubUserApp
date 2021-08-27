package com.mihab.githubuserapp.data.repository.remote

import com.mihab.githubuserapp.utils.Constants.Companion.BASE_URL
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.Retrofit




class RetrofitInstance {

    companion object {

        val api: GithubApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubApi::class.java)
        }
    }
}