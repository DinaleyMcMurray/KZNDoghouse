package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SponsorManagementActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth // Added FirebaseAuth for logout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SponsorshipAdapter
    private val sponsorRecords = mutableListOf<SponsorshipRecord>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Ensure this matches your XML file name
        setContentView(R.layout.activity_sponsor_management)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // === NAVIGATION SETUP ===
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        setupNavigationDrawer()
        // ========================

        // === RECYCLERVIEW SETUP ===
        recyclerView = findViewById(R.id.recyclersponsorhistory)
        adapter = SponsorshipAdapter(this, sponsorRecords)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        // ==========================

        fetchSponsorRecords()
    }

    private fun fetchSponsorRecords() {
        // CRITICAL: We use collectionGroup("Sponsors") to query ALL "Sponsors" subcollections
        // across ALL user documents. This requires a Firestore Collection Group Index.

        firestore.collectionGroup("Sponsors")
            .orderBy("dateSubmitted", Query.Direction.DESCENDING) // Use the field saved in Firestore
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fetchedRecords = mutableListOf<SponsorshipRecord>()
                for (document in querySnapshot.documents) {
                    try {
                        val record = document.toObject(SponsorshipRecord::class.java)?.copy(
                            recordId = document.id, // Store the document ID of the Sponsorship record
                            // Extract the parent User ID from the document reference
                            userId = document.reference.parent.parent?.id ?: ""
                        )
                        if (record != null) {
                            fetchedRecords.add(record)
                        }
                    } catch (e: Exception) {
                        Log.e("SponsorMngmt", "Error converting document: ${document.id}", e)
                    }
                }
                adapter.updateData(fetchedRecords)
                if (fetchedRecords.isEmpty()) {
                    Toast.makeText(this, "No sponsorship records found.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("SponsorMngmt", "Error fetching sponsorship records", e)
                Toast.makeText(this, "Failed to load records. Check Firestore logs.", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Sets up the navigation drawer functionality for an Admin user.
     * Assumes this screen is the main admin dashboard/hub.
     */
    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> startActivity(Intent(this, DogManagementActivity::class.java))
                R.id.nav_volunteer_management -> startActivity(Intent(this, VolunteerManagementActivity::class.java))
                R.id.nav_events_management -> startActivity(Intent(this, EventsManagementActivity::class.java))
                R.id.nav_adoption_history -> startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                R.id.nav_dogfood -> startActivity(Intent(this, DonationHistoryActivity::class.java))
                R.id.nav_sponsor -> startActivity(Intent(this, SponsorManagementActivity::class.java))
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}