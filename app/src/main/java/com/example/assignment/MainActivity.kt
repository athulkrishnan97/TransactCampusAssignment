package com.example.assignment

import PreferenceManager
import PreferenceManager.Companion.DEFAULT_AUTHOR_FILTER
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.adapters.ImageAdapter
import com.example.assignment.models.ImageModel
import com.example.assignment.singletons.NetworkUtils
import com.example.assignment.singletons.SortingEnum
import com.example.assignment.viewmodels.MainActivityViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var authorSpinner: Spinner
    private lateinit var selectedAuthor: String

    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var previouslyFilteredAuthor: String
    private lateinit var progressCircle: CircularProgressIndicator
    private lateinit var fab: FloatingActionButton

    private var currentSorting: SortingEnum = SortingEnum.ALPHABETICAL
    private var updatedImages = listOf<ImageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferenceManager = PreferenceManager(this)
        initialiseViews()
        getPreviouslyFilteredAuthor()
        recyclerView.layoutManager = LinearLayoutManager(this)
        imageAdapter = ImageAdapter(listOf())

        recyclerView.adapter = imageAdapter
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.fetchImages()

        fetchDataAndUpdateSpinner()
        observeForErrors()

        authorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedAuthor = parent.getItemAtPosition(position).toString()
                populateImages(selectedAuthor)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                imageAdapter.setData(updatedImages)
            }
        }
        fab.setOnClickListener {
            setupSortingDialog().show()
        }
    }

    /**
     * This Method observes the images live data from the viewmodel. This live data contains the
     * result of the api call to fetch the images. It also sets up the adapter for the spinner. The
     * spinner contains the names of the author. It also sets the spinner selection to the same
     * author as last time the app was opened.
     */
    private fun fetchDataAndUpdateSpinner() {
        viewModel.images.observe(this) { images ->
            updatedImages = images
            val authors = viewModel.getAuthors(images)
            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, authors)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            authorSpinner.adapter = arrayAdapter
            authorSpinner.setSelection(arrayAdapter.getPosition(previouslyFilteredAuthor))
            progressCircle.visibility = View.GONE
        }
    }

    /**
     * This method observes a NetworkUtils class. The class contains a live data which represents the
     * network status. If the connection to the internet is broken, a dialogbox is shown to the user.
     * The user has the option to either retry the connection or close the app.
     */
    private fun observeForErrors() {
        val errorObserver = Observer<Boolean> { newStatus ->
            if (!newStatus) {
                val dialog: AlertDialog = setupErrorDialog()
                if (!dialog.isShowing) {
                    dialog.show()
                    progressCircle.visibility = View.VISIBLE
                }
            }
        }
        NetworkUtils.networkStatus.observe(this, errorObserver)
    }

    /**
     * This application uses DataStore instead of sharedpreferences. Datastore is more versatile and
     * recommended by the official google documentation. This method fetches the author which was
     * filtered by the user the last time the app was opened.
     */
    private fun getPreviouslyFilteredAuthor() {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceManager.filterPreferenceFlow.collect { value ->
                previouslyFilteredAuthor = value
            }
        }
    }

    /**
     * This function initialises the variables and links it to the layout IDs
     */
    private fun initialiseViews() {
        authorSpinner = findViewById(R.id.author_spinner)
        progressCircle = findViewById(R.id.progress_circle)
        fab = findViewById(R.id.floating_action_button)
        recyclerView = findViewById(R.id.recycler_view)
    }

    /**
     * This method is responsible for populating the recycler view in the app. The elements can be
     * sorted depending upon the choice of the user. It can either be in the descending order of the
     * name of the author or can also be descending.
     *
     * Note: The elements are not sorted if they already being filtered with a particular author.
     * The DEFAULT_AUTHOR_FILTER is "None". In this case, the filtering is ignored and all the images
     * from the api call will be displayed.
     */
    private fun populateImages(selectedAuthor: String) {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceManager.setFilterPreference(selectedAuthor)
        }
        val filteredImages = if (selectedAuthor == DEFAULT_AUTHOR_FILTER) {
            if (currentSorting == SortingEnum.REVERSE_ALPHABETICAL) {
                val sorted = updatedImages.sortedByDescending { it.author }
                sorted
            } else {
                updatedImages
            }
        } else {
            updatedImages.filter { it.author == selectedAuthor }
        }
        imageAdapter.setData(filteredImages)
    }

    /**
     * This method sets up the UI dialog asking the user whether they want to sort the elements
     * alphabetically or reverse alphabetically. The enum "SortingEnum" is used to keep track of the
     * current state of the sort.
     */
    private fun setupSortingDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(
                if (currentSorting == SortingEnum.ALPHABETICAL) {
                    getString(R.string.ask_to_reverse_order)
                } else {
                    getString(R.string.ask_to_set_normal_order)
                }
            )
            setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                currentSorting = if (currentSorting == SortingEnum.ALPHABETICAL) {
                    SortingEnum.REVERSE_ALPHABETICAL
                } else {
                    SortingEnum.ALPHABETICAL
                }
                populateImages(selectedAuthor)
            }
            setNegativeButton(
                getString(R.string.no)
            ) { _, _ ->
            }
        }
        return builder.create()
    }

    /**
     * This method sets up the error dialog for when there are network issues preventing the app from
     * fetching the images/api calls properly.
     */
    private fun setupErrorDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.ask_for_retry))
            setTitle(getString(R.string.network_failed))
            setPositiveButton(
                getString(R.string.retry)
            ) { dialog, _ ->
                viewModel.fetchImages()
                dialog.cancel()
            }
            setNegativeButton(
                getString(R.string.close)
            ) { dialog, _ ->
                finish()
            }
        }
        return builder.create()
    }

}