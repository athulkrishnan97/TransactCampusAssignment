package com.example.assignment.viewmodels

import PreferenceManager.Companion.DEFAULT_AUTHOR_FILTER
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment.models.ImageModel
import com.example.assignment.repo.ImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    private val repository = ImageRepository()
    private val _images = MutableLiveData<List<ImageModel>>()
    val images: LiveData<List<ImageModel>> get() = _images

    fun fetchImages(reverseOrder: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            val fetchedImages = repository.getImages()
            _images.value = if (reverseOrder) {
                fetchedImages.sortedByDescending { it.author }
            } else {
                fetchedImages
            }
        }
    }

    fun getAuthors(images: List<ImageModel>): List<String> {
        val authors = mutableListOf(DEFAULT_AUTHOR_FILTER)
        authors.addAll(images.map { it.author }.distinct())
        return authors
    }

}