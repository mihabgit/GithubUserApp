package com.mihab.githubuserapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AlertDialog

class CommonTask {
    companion object {
        fun isOnline(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.activeNetworkInfo?.run {
                    return when(type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }

            return false
        }

        fun showNoInternetAlertDialog(context: Context) {
            val alertDialog: AlertDialog = context.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton("Ok") { dialog, id ->
                        dialog.dismiss()
                    }
                }
                builder.create()
            }
            alertDialog.setTitle("Sorry!")
            alertDialog.setMessage("No Internet connection!")
            alertDialog.show()
        }
    }
}