package vcmsa.projects.thedoghouse_prototype

import DogDataRecord // 1. Import your data model
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager // 2. Required for RecyclerView
import androidx.recyclerview.widget.RecyclerView // 3. Required for RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore // 4. Required for Firestore

class DogManagementActivity : AppCompatActivity() {

    // --- Dog Data and RecyclerView Setup ---
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DogAdapter
    private lateinit var dogList: MutableList<DogDataRecord>

    private val db = FirebaseFirestore.getInstance()
    private val ADMIN_DOC_ID = "AdminUserDocument"
    // CRITICAL: Path must match where you save the data in AddDogActivity
    private val DOGS_COLLECTION_PATH = "Admin/$ADMIN_DOC_ID/AddDog"
    private val TAG = "DogManagementActivity"
    // ----------------------------------------

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_management)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        val addDogButton: Button = findViewById(R.id.AddDogBtn)

        setSupportActionBar(toolbar)

        // 1. Initialize RecyclerView components
        recyclerView = findViewById(R.id.recyclerdogmanagement)
        dogList = mutableListOf()
        adapter = DogAdapter(dogList) // The adapter you created
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 2. Load data immediately when the activity starts
        fetchDogData()

        // Hook up toolbar icon to open drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Hook up Add Dog button
        addDogButton.setOnClickListener {
            startActivity(Intent(this, AddDogActivity::class.java))
        }

        // Handle nav item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {
                    // Already on this page
                    drawerLayout.closeDrawers()
                }
                R.id.nav_volunteer_management -> {
                    startActivity(Intent(this, VolunteerManagementActivity::class.java))
                }
                R.id.nav_events_management -> {
                    startActivity(Intent(this, EventsManagementActivity::class.java))
                }
                R.id.nav_adoption_history -> {
                    startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    /**
     * Fetches all dog documents from Firestore and updates the RecyclerView.
     */
    private fun fetchDogData() {
        dogList.clear() // Clear existing data before fetching fresh data

        db.collection(DOGS_COLLECTION_PATH)
            // Sort by date added (latest first)
            .orderBy("dateAdded", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val newDogs = result.documents.mapNotNull { document ->
                    try {
                        // CRITICAL: Converts the Firestore document into a DogData object
                        document.toObject(DogDataRecord::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error mapping dog document to DogData: ${document.id}", e)
                        Toast.makeText(this, "A dog record has corrupted data.", Toast.LENGTH_SHORT).show()
                        null
                    }
                }

                // Tell the adapter to update its list and refresh the display
                adapter.updateData(newDogs)

                if (newDogs.isEmpty()) {
                    Toast.makeText(this, "No dogs are currently available for management.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting dog documents: ", exception)
                Toast.makeText(this, "Failed to load dog data: Check your database connection.", Toast.LENGTH_LONG).show()
            }
    }

    override fun onResume() {
        super.onResume()
        fetchDogData()
    }
}