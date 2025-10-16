package vcmsa.projects.thedoghouse_prototype


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Menu // NEW
import android.view.MenuItem // NEW
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

// NEW IMPORTS for Retrofit and Coroutines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// NEW IMPORT for the Filter Dialog Fragment
import vcmsa.projects.thedoghouse_prototype.FilterDialogFragment // Assuming this is where you created the file

class ViewAdoptionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DogAdapterPublic
    private val TAG = "ViewAdoptionActivity"

    // NAV DRAWER COMPONENTS
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    // NEW: Properties to hold the current filter state
    private var currentFilterAge: Int? = null
    private var currentFilterBreed: String? = null
    private var currentFilterIsVaccinated: Boolean? = null
    private var currentFilterIsSterilized: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_adoption)

        // 1. Initialize Nav Drawer Views
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        // Set the toolbar as the action bar
        setSupportActionBar(toolbar)

        // Set click listener for the navigation icon (hamburger icon) to open the drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 2. Set up the listener for navigation item clicks (Your provided logic)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_newsletter -> {
                    startActivity(Intent(this, NewsletterActivity::class.java))
                }
                R.id.nav_fundsdonation -> {
                    startActivity(Intent(this, FundsDonationsActivity::class.java))
                    finish()
                }
                R.id.nav_volunteer -> {
                    startActivity(Intent(this, VolunteerActivity::class.java))
                    finish()
                }
                R.id.nav_adoption -> {
                    // Current Activity: Close drawer but do not navigate/finish
                    Toast.makeText(this, "You are already viewing adoptions.", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_help -> {
                    startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
                else -> false // Handle unhandled IDs gracefully
            }
            drawerLayout.closeDrawers()
            true
        }

        // 3. Setup RecyclerView
        recyclerView = findViewById(R.id.viewadoptionrecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with an empty list and context
        adapter = DogAdapterPublic(mutableListOf(), this)
        recyclerView.adapter = adapter

        // 4. Fetch Data (Initial load with no filters)
        fetchAvailableDogs(
            age = currentFilterAge,
            breed = currentFilterBreed,
            isVaccinated = currentFilterIsVaccinated,
            isSterilized = currentFilterIsSterilized
        )
    }

    // Override onBackPressed to close the drawer instead of exiting the activity
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // ------------------------------------------------------------------
    // NEW: Filter Menu Methods
    // ------------------------------------------------------------------

    // 1. Inflate the filter menu into the toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter_menu, menu) // Ensure you created res/menu/filter_menu.xml
        return true
    }

    // 2. Handle the filter icon click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 3. Implemented method to show the filter dialog
    private fun showFilterDialog() {
        val dialog = FilterDialogFragment()

        // Pass the current filter values to the dialog so it can pre-fill the fields (optional but good UX)
        val args = Bundle().apply {
            currentFilterAge?.let { putInt("age", it) }
            currentFilterBreed?.let { putString("breed", it) }
            currentFilterIsVaccinated?.let { putBoolean("isVaccinated", it) }
            currentFilterIsSterilized?.let { putBoolean("isSterilized", it) }
        }
        dialog.arguments = args

        // Show the dialog
        dialog.show(supportFragmentManager, "FilterDialog")
    }

    /**
     * Call this method from your filter dialog when the user hits "Apply".
     */
    fun updateFiltersAndFetch(
        age: Int?,
        breed: String?,
        isVaccinated: Boolean?,
        isSterilized: Boolean?
    ) {
        // Update the activity's state with the new filters
        currentFilterAge = age
        currentFilterBreed = breed
        currentFilterIsVaccinated = isVaccinated
        currentFilterIsSterilized = isSterilized

        // Fetch data with the new filters applied
        fetchAvailableDogs(age, breed, isVaccinated, isSterilized)

        // Optional: Notify user that filters were applied
        Toast.makeText(this, "Filters applied.", Toast.LENGTH_SHORT).show()
    }

    // ------------------------------------------------------------------
    // UPDATED: Fetch Data Method (now accepts parameters)
    // ------------------------------------------------------------------

    /**
     * Fetches available dogs by calling the Render API via Retrofit.
     */
    private fun fetchAvailableDogs(
        age: Int?,
        breed: String?,
        isVaccinated: Boolean?,
        isSterilized: Boolean?
    ) {
        // Launch a coroutine on the IO thread for network operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Pass the filter parameters to the API call
                val availableDogs = RetrofitClient.dogApiService.getFilteredDogs(
                    age = age,
                    breed = breed,
                    isVaccinated = isVaccinated,
                    isSterilized = isSterilized
                )

                // Switch to the Main thread to update the UI
                withContext(Dispatchers.Main) {
                    adapter.updateData(availableDogs.toMutableList())

                    if (availableDogs.isEmpty()) {
                        Toast.makeText(this@ViewAdoptionActivity, "No dogs match the current criteria.", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching dogs from Render API: ${e.message}", e)

                // Switch to the Main thread to display the error
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewAdoptionActivity, "Failed to load dogs. Check network/API status.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}