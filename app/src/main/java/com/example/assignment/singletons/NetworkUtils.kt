package com.example.assignment.singletons

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.MutableLiveData

object NetworkUtils {
    val networkStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

//
//    fun isConnected(context: Context): Boolean {
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        val activeNetwork = connectivityManager.activeNetwork ?: return false
//        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
//
//        return when {
//            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
//            capabilities.hasTransport(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> true
//            else -> true
//        }
//    }
}
