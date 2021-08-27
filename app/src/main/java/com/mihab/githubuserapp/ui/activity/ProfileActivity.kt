package com.mihab.githubuserapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mihab.githubuserapp.R
import com.mihab.githubuserapp.data.model.Profile
import com.mihab.githubuserapp.data.repository.local.db.UserDatabase
import com.mihab.githubuserapp.data.repository.UserRepository
import com.mihab.githubuserapp.databinding.ActivityProfileBinding
import com.mihab.githubuserapp.ui.viewmodel.LocalDbViewModel
import com.mihab.githubuserapp.ui.viewmodel.UserViewModel
import com.mihab.githubuserapp.ui.viewmodel.UserViewModelProviderFactory
import com.mihab.githubuserapp.utils.CommonTask
import com.mihab.githubuserapp.utils.Resource
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityProfileBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var localDbViewModel: LocalDbViewModel
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)
        userName = intent.getStringExtra("userName")

        val userRepository = UserRepository(UserDatabase(this))
        val viewModelProviderFactory = UserViewModelProviderFactory(userRepository)

        val localDbViewModelProviderFactory =
            LocalDbViewModel.LocalDbViewModelProviderFactory(userRepository)
        localDbViewModel = ViewModelProvider(
            this,
            localDbViewModelProviderFactory
        ).get(LocalDbViewModel::class.java)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        actionBar?.setDisplayHomeAsUpEnabled(true)

        if (CommonTask.isOnline(this)) {
            userViewModel = ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)
            userName?.let { userViewModel.getUserByUserName(it) }
            fetchProfileDataFromRemote()
        } else {
            Toast.makeText(this, "No internet connection available!", Toast.LENGTH_SHORT).show()
            getDataFromLocal(userName)
        }

    }

    private fun fetchProfileDataFromRemote() {
        userViewModel.profile.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { userResponse ->
                        updateUI(userResponse)
                        localDbViewModel.saveProfile(userResponse)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    getDataFromLocal(userName)
                    response.message?.let { message ->
                        Toast.makeText(
                            this,
                            "Something went wrong, please try again later!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("ProfileActivity", "An error occur: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun getDataFromLocal(userName: String?) {
        userName?.let {
            localDbViewModel.getProfileByUserName(userName).observe(this, { profile ->
                profile?.let {
                    updateUI(profile)
                }
            })
        }
    }

    private fun updateUI(profile: Profile) {
        supportActionBar?.title = profile.name
        Picasso.get().load(profile.avatar_url).into(profileBinding.ivProfileImage)
        profileBinding.tvFollower.text = "Followers : ${profile.followers}"
        profileBinding.tvFollowing.text = "Following : ${profile.following}"
        profileBinding.tvName.text = "Name : ${profile.name}"
        profileBinding.tvCompany.text = "Company : ${profile.company}"
        profileBinding.tvLocation.text = "Location : ${profile.location}"
    }

    private fun hideProgressBar() {
        profileBinding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        profileBinding.progressBar.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}