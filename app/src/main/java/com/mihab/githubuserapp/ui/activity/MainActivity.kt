package com.mihab.githubuserapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mihab.githubuserapp.data.repository.local.db.UserDatabase
import com.mihab.githubuserapp.data.repository.UserRepository
import com.mihab.githubuserapp.databinding.ActivityMainBinding
import com.mihab.githubuserapp.ui.adapter.UserAdapter
import com.mihab.githubuserapp.ui.viewmodel.LocalDbViewModel
import com.mihab.githubuserapp.ui.viewmodel.UserViewModel
import com.mihab.githubuserapp.ui.viewmodel.UserViewModelProviderFactory
import com.mihab.githubuserapp.utils.CommonTask
import com.mihab.githubuserapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.mihab.githubuserapp.utils.NetworkConnectionLiveData
import com.mihab.githubuserapp.utils.Resource
import java.net.SocketTimeoutException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var localDbViewModel: LocalDbViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository(UserDatabase(this))
        val viewModelProviderFactory = UserViewModelProviderFactory(userRepository)

        val localDbViewModelProviderFactory =
            LocalDbViewModel.LocalDbViewModelProviderFactory(userRepository)
        localDbViewModel = ViewModelProvider(
            this,
            localDbViewModelProviderFactory
        ).get(LocalDbViewModel::class.java)

        setupRecyclerView()

        if (CommonTask.isOnline(this)) {
            fetchDataFromRemote(viewModelProviderFactory)
        } else {
            CommonTask.showNoInternetAlertDialog(this)
            getDataFromLocal()
        }

        userAdapter.setOnItemClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userName", it.login)
            startActivity(intent)
        }

        NetworkConnectionLiveData(this)
            .observe(this, Observer { isConnected ->
                if (!isConnected) {
                    Log.d("MainActivity", "Offline")
                    return@Observer
                }
                fetchDataFromRemote(viewModelProviderFactory)
            })


        binding.editTextSearch.addTextChangedListener { editable ->
            editable?.let {
                if (editable.toString().isNotEmpty()) {
                    getSearchDataFromLocal(editable.toString())
                } else {
                    getDataFromLocal()
                }
            }
        }
    }

    private fun fetchDataFromRemote(viewModelProviderFactory: UserViewModelProviderFactory) {
        userViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)
        userViewModel.user.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { userResponse ->
                        userAdapter.users = userResponse

                        localDbViewModel.saveAllUser(userResponse)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(
                            this,
                            "Something went wrong, please try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("MainActivity", "An error occur: $message")

                    }
                    getDataFromLocal()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun getDataFromLocal() {
        localDbViewModel.getSavedUser().observe(this, { users ->
            userAdapter.users = users
        })
    }

    private fun getSearchDataFromLocal(userName: String) {
        localDbViewModel.getSearchData(userName).observe(this, { users ->
            userAdapter.users = users
        })
    }

    private fun setupRecyclerView() = binding.rvGithubUser.apply {
        userAdapter = UserAdapter()
        adapter = userAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
        addOnScrollListener(this@MainActivity.scrollListener)
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                if (CommonTask.isOnline(this@MainActivity)) {
                    userViewModel.getUser()
                    isScrolling = false
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

}