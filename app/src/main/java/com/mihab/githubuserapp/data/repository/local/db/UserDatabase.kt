package com.mihab.githubuserapp.data.repository.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mihab.githubuserapp.data.model.Profile
import com.mihab.githubuserapp.data.model.User
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [User::class, Profile::class], version = 1
)
abstract class UserDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getProfileDao(): ProfileDao

    companion object {
        // Singleton prevents multiple instance of database opening at the same time
        @Volatile
        private var INSTANCE: UserDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: createDatabase(context).also { INSTANCE = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java,
                "user_db"
            ).build()
    }

}