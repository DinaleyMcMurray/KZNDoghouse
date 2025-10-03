package vcmsa.projects.thedoghouse_prototype

import VolunteerRecord
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query // Import for querying

class VolunteerManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VolunteerAdapter
    private lateinit var volunteerList: MutableList<VolunteerRecord>
    private lateinit var progressBar: ProgressBar // We'll assume you add a ProgressBar to your XML

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "VolunteerMgt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_management) // Your XML with DrawerLayout

        // Initialize ProgressBar (Add <ProgressBar> to your activity_volunteer_management.xml)
        progressBar = findViewById(R.id.progressBar)

        // 1. Drawer + Toolbar setup
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val navView: NavigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 2. Setup RecyclerView
        recyclerView = findViewById(R.id.recyclervolunteers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the list and adapter
        volunteerList = mutableListOf()
        adapter = VolunteerAdapter(volunteerList)
        recyclerView.adapter = adapter

        // 3. Fetch Data from Firestore
        fetchVolunteerData()

        // 4. Handle navigation clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            val intent: Intent? = when (menuItem.itemId) {
                R.id.nav_dog_management -> Intent(this, DogManagementActivity::class.java)
                R.id.nav_events_management -> Intent(this, EventsManagementActivity::class.java)
                R.id.nav_adoption_history -> Intent(this, AdoptionHistoryActivity::class.java)
                R.id.nav_volunteer_management -> null // Already here, close drawer
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    null
                }
                else -> null
            }
            intent?.let { startActivity(it) }
            drawerLayout.closeDrawers()
            true
        }
    }

    // Function to query Firestore for volunteers
    private fun fetchVolunteerData() {
        progressBar.visibility = View.VISIBLE
        volunteerList.clear()

        db.collectionGroup("Volunteer")
            // FIX: The field name in the database for sorting is "Name" (Capital N)
            .orderBy("Name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                progressBar.visibility = View.GONE
                for (document in result) {
                    try {
                        // ------------------------------------------------------------------
                        // FIX: Map using the correct capitalization/names from Firestore document
                        // ------------------------------------------------------------------
                        val volunteer = VolunteerRecord(
                            // Field names MUST match capitalization: "Name", "Gender", "Phone", "Email"
                            name = document.getString("Name") ?: "N/A",
                            gender = document.getString("Gender") ?: "N/A",
                            age = document.getString("Age") ?: "N/A",
                            contactNumber = document.getString("Phone") ?: "N/A",
                            email = document.getString("Email") ?: "N/A"
                        )
                        volunteerList.add(volunteer)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error mapping volunteer data: ${e.message}")
                    }
                }
                if (volunteerList.isEmpty()) {
                    Toast.makeText(this, "No volunteers found.", Toast.LENGTH_LONG).show()
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(
                    this,
                    "Failed to load volunteer data. Check Firebase Index.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}