package com.example.assignment.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment.models.ImageModel
import com.example.assignment.repo.ImageRepository
import com.example.assignment.singletons.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    private val repository = ImageRepository()
    private val _images = MutableLiveData<List<ImageModel>>()
    val images: LiveData<List<ImageModel>> get() = _images

    fun fetchImages(context: Context) {
        if (NetworkUtils.isConnected(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                val fetchedImages = repository.getImages()
                _images.value = fetchedImages
            }
        } else {
            Log.e("Nett","No internet")
        }
    }

}