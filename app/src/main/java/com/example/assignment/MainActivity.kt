package com.example.assignment

import PreferenceManager
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.adapters.ImageAdapter
import com.example.assignment.models.ImageModel
import com.example.assignment.singletons.NetworkUtils
import com.example.assignment.viewmodels.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainActivityViewModel
    lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var authorSpinner: Spinner
    private var images1 = listOf<ImageModel>()
    private  lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var previousAuthorSelection: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferenceManager = PreferenceManager(this)
        authorSpinner = findViewById(R.id.author_spinner)


        CoroutineScope(Dispatchers.IO).launch {
            preferenceManager.filterPreferenceFlow.collect { value ->
                previousAuthorSelection = value
            }
        }




        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        imageAdapter = ImageAdapter(listOf())
        recyclerView.adapter = imageAdapter

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.fetchImages()

        // Observe the LiveData in the ViewModel
        viewModel.images.observe(this) { images ->
            images1 = images
            // Update the adapter's data
            imageAdapter.setData(images)
            val authors = viewModel.getAuthors(images)
            arrayAdapter = ArrayAdapter(this, R.layout.spinner_item, authors)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            authorSpinner.adapter = arrayAdapter
            authorSpinner.setSelection(arrayAdapter.getPosition(previousAuthorSelection))
        }

        val nameObserver = Observer<Boolean> { newStatus ->
            if (!newStatus) {
                val dialog: AlertDialog = setupErrorDialog()
                dialog.show()
            }
        }
        NetworkUtils.networkStatus.observe(this, nameObserver)
        authorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedAuthor = parent.getItemAtPosition(position).toString()
                CoroutineScope(Dispatchers.IO).launch {
                    preferenceManager.setFilterPreference(selectedAuthor)
                }
                val filteredImages = if (selectedAuthor == "None"){
                    images1
                }
                else{
                    images1.filter { it.author == selectedAuthor }

                }

                // Update your adapter with the filtered list of images
                imageAdapter.setData(filteredImages)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // When nothing is selected, show all images
                imageAdapter.setData(images1)
            }
        }

    }

    private fun setupErrorDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this).apply {
            setMessage("Do you want to retry")
            setTitle("Network Failed")
            setPositiveButton("Retry"
            ) { dialog, _ ->
                viewModel.fetchImages()
                dialog.cancel()
            }
            setNegativeButton("Close"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
        return builder.create()
    }

}