package vcmsa.projects.thedoghouse_prototype


import android.content.Intent // Required for starting new activities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat // Required to open/close the drawer
import androidx.drawerlayout.widget.DrawerLayout // Required for the drawer layout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar // Required for the toolbar
import com.google.android.material.navigation.NavigationView // Required for the nav view
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ViewAdoptionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DogAdapterPublic
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "ViewAdoptionActivity"

    // NAV DRAWER COMPONENTS
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_adoption)




        // 1. Initialize Nav Drawer Views
        // These IDs must match the ones in your activity_view_adoption.xml layout
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

//        // Handle system bars/insets (keep this at the bottom of the view initialization)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // 3. Setup RecyclerView
        recyclerView = findViewById(R.id.viewadoptionrecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with an empty list and context
        adapter = DogAdapterPublic(mutableListOf(), this)
        recyclerView.adapter = adapter

        // 4. Fetch Data
        fetchAvailableDogs()
    }

    // Override onBackPressed to close the drawer instead of exiting the activity
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun fetchAvailableDogs() {
        // 1. Get a reference to the 'AddDog' Collection Group.
        db.collectionGroup("AddDog")
            // 2. Filter for only dogs that are available for adoption
            .whereEqualTo("status", "Available for Adoption")
            // 3. Order by date (optional, but good practice)
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->

                val availableDogs = querySnapshot.documents.mapNotNull { document ->
                    try {
                        // Map each Firestore document to your data class
                        document.toObject(DogDataRecord::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error mapping document ${document.id}: ${e.message}", e)
                        null
                    }
                }

                adapter.updateData(availableDogs)

                if (availableDogs.isEmpty()) {
                    Toast.makeText(this, "No dogs are available for adoption right now.", Toast.LENGTH_LONG).show()
                }

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching dogs from collection group: ${e.message}", e)
                Toast.makeText(this, "Failed to load dogs. Please check network/index.", Toast.LENGTH_LONG).show()
            }
    }
}