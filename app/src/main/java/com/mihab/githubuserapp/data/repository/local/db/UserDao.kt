package com.mihab.githubuserapp.data.repository.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mihab.githubuserapp.data.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userList: List<User>)

    @Query("SELECT * FROM users WHERE login LIKE '%' || :userName || '%'")
    fun search(userName: String): LiveData<List<User>>

}