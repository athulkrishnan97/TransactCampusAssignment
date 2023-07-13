package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.adapters.ImageAdapter
import com.example.assignment.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    lateinit var viewModel : MainActivityViewModel
    lateinit var recyclerView:RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        imageAdapter = ImageAdapter(listOf())
        recyclerView.adapter = imageAdapter

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.fetchImages(this)
        // Observe the LiveData in the ViewModel
        viewModel.images.observe(this) { images ->
            // Update the adapter's data
            imageAdapter.setData(images)
        }
    }
}