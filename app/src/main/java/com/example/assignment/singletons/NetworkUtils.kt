package com.example.assignment.singletons

/**
 * This class is responsible for keeping track of the network connectivity status of the application.
 */
import androidx.lifecycle.MutableLiveData

object NetworkUtils {
    val networkStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
}
