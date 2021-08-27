package com.mihab.githubuserapp.data.repository.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mihab.githubuserapp.data.model.Profile

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: Profile)

    @Query("SELECT * FROM profile WHERE login == :userName")
    fun getProfileByUserName(userName: String): LiveData<Profile>
}