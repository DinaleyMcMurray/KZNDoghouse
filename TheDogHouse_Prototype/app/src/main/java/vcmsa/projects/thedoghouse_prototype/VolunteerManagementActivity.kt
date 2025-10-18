package vcmsa.projects.thedoghouse_prototype

import VolunteerRecord
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class VolunteerManagementActivity : AppCompatActivity(), VolunteerAdapter.OnItemDeleteListener {

    private lateinit var etSearch: EditText
    private lateinit var btnResetSearch: Button
    private lateinit var btnAddVolunteer: Button
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VolunteerAdapter
    private lateinit var volunteerList: MutableList<VolunteerRecord>
    private lateinit var allVolunteers: MutableList<VolunteerRecord>
    private lateinit var progressBar: ProgressBar

    private lateinit var drawerLayout: DrawerLayout

    // Initialize Auth and Firestore
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "VolunteerMgt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Note: Using deprecated flags might be incompatible with enableEdgeToEdge()
        // window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        // window.statusBarColor = android.graphics.Color.TRANSPARENT

        // This line hides the standard action bar if you are using a MaterialToolbar
        supportActionBar?.hide()
        setContentView(R.layout.activity_volunteer_management)

        progressBar = findViewById(R.id.progressBar)

        // 1. Drawer + Toolbar setup
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        // 🔥 FIX: Assign the NavigationView to the class property
        navigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)

        // Setting DisplayHomeAsUpEnabled makes the navigation icon (hamburger) visible
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                // This is the essential fix: open the drawer instead of navigating home
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        // 2. Setup RecyclerView
        recyclerView = findViewById(R.id.recyclervolunteers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the lists
        volunteerList = mutableListOf()
        allVolunteers = mutableListOf()

        adapter = VolunteerAdapter(volunteerList, this)
        recyclerView.adapter = adapter

        // 3. Initialize Search EditText and Reset Button
        etSearch = findViewById(R.id.etSearch)
        btnResetSearch = findViewById(R.id.btnResetSearch)
        btnAddVolunteer = findViewById(R.id.btnAddVolunteer)

        // 4. Attach Listeners
        setupSearchListener()
        setupResetButton()
        setupAddVolunteerButton()


        // 5. Handle navigation clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {
                    startActivity(Intent(this, DogManagementActivity::class.java))
                    finish()
                }
                R.id.nav_volunteer_management -> {
                    // This is the current activity. Do nothing except close the drawer.
                    // The drawer closing is handled below.
                }
                R.id.nav_events_management -> {
                    startActivity(Intent(this, EventsManagementActivity::class.java))
                    finish()
                }
                R.id.nav_adoption_history -> {
                    startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                    finish()
                }
                R.id.nav_dogfood -> {
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                    finish()
                }
                R.id.nav_sponsor -> {
                    startActivity(Intent(this, SponsorManagementActivity::class.java))
                    finish()
                }
                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, AdminHomeActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

    }

    // ----------------------------------------------------
    // FIX: Add onResume() to force data reload when returning
    // ----------------------------------------------------
    override fun onResume() {
        super.onResume()
        fetchVolunteerData()
    }
    // ----------------------------------------------------

    private fun setupAddVolunteerButton() {
        btnAddVolunteer.setOnClickListener {
            // Opens the Admin form activity
            val intent = Intent(this, AdminAddVolunteerActivity::class.java)
            Toast.makeText(this, "Opening Volunteer Application Form...", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }


    override fun onDeleteClick(documentId: String, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to remove this volunteer record?")
            .setPositiveButton("Delete") { _, _ ->
                deleteVolunteerRecord(documentId, position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteVolunteerRecord(documentId: String, position: Int) {
        // CRITICAL FIX: Find the User ID associated with the document ID
        val recordToDelete = allVolunteers.firstOrNull { it.documentId == documentId }

        if (recordToDelete == null || recordToDelete.userId.isEmpty() || recordToDelete.userId == "N/A") {
            Log.e(TAG, "Deletion failed: Cannot find record or userId for document ID: $documentId")
            Toast.makeText(this, "Error: Volunteer data missing User ID.", Toast.LENGTH_LONG).show()
            return
        }

        // NEW, RELIABLE DELETION METHOD: Use the direct document path
        val docRef = db.collection("Users")
            .document(recordToDelete.userId)
            .collection("Volunteer")
            .document(documentId)

        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Volunteer deleted successfully!", Toast.LENGTH_SHORT).show()
                removeRecordFromLists(documentId, position)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting document at path: $e")
                Toast.makeText(this, "Error deleting volunteer: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun removeRecordFromLists(documentId: String, position: Int) {
        if (position >= 0 && position < volunteerList.size) {
            volunteerList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
        allVolunteers.removeAll { it.documentId == documentId }
    }

    private fun setupResetButton() {
        btnResetSearch.setOnClickListener {
            etSearch.setText("")
            volunteerList.clear()
            volunteerList.addAll(allVolunteers)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Search reset. Showing all volunteers.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        val lowerCaseQuery = query.lowercase().trim()

        if (lowerCaseQuery.isEmpty()) {
            volunteerList.clear()
            volunteerList.addAll(allVolunteers)
        } else {
            val filteredList = allVolunteers.filter { record ->
                record.name.lowercase().contains(lowerCaseQuery)
            }
            volunteerList.clear()
            volunteerList.addAll(filteredList)
        }
        adapter.notifyDataSetChanged()
    }

    private fun fetchVolunteerData() {
        progressBar.visibility = View.VISIBLE
        volunteerList.clear()
        allVolunteers.clear()

        db.collectionGroup("Volunteer")
            .orderBy("Name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                progressBar.visibility = View.GONE
                for (document in result) {
                    try {
                        val volunteer = VolunteerRecord(
                            documentId = document.id,
                            // NEW: Capture the userId for use in direct deletion
                            userId = document.getString("userId") ?: "N/A",
                            name = document.getString("Name") ?: "N/A",
                            gender = document.getString("Gender") ?: "N/A",
                            age = document.getString("Age") ?: "N/A",
                            contactNumber = document.getString("Phone") ?: "N/A",
                            email = document.getString("Email") ?: "N/A"
                        )
                        allVolunteers.add(volunteer)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error mapping volunteer data: ${e.message}")
                    }
                }

                volunteerList.addAll(allVolunteers)

                if (volunteerList.isEmpty()) {
                    if (result.isEmpty) {
                        Toast.makeText(this, "No volunteer records found in the database.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Failed to process volunteer data.", Toast.LENGTH_LONG).show()
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(
                    this,
                    "Failed to load volunteer data. Check Logcat for details.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // Ensure onBackPressed handles the drawer correctly
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Since this is likely an Admin secondary screen, pressing back should go home
            startActivity(Intent(this, AdminHomeActivity::class.java))
            finish()
        }
    }
}