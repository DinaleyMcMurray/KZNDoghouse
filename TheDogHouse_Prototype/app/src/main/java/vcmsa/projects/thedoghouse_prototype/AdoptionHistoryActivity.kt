package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button // Import the Button class
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AdoptionHistoryActivity : AppCompatActivity() {

    private val TAG = "AdoptionHistoryActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var btnResetSearch: Button // 1. Reset Button Declaration
    private val firestore = FirebaseFirestore.getInstance()

    private var fullAdoptionHistoryList = mutableListOf<AdoptionHistory>()
    private lateinit var adapter: AdoptionHistoryAdapter

    // Confirmed path for Dog Management
    private val DOG_COLLECTION_PATH = "Admin/AdminUserDocument/AddDog"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption_history)

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Drawer + Toolbar setup
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val navView: NavigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> startActivity(Intent(this, DogManagementActivity::class.java))
                R.id.nav_volunteer_management -> startActivity(Intent(this, VolunteerManagementActivity::class.java))
                R.id.nav_events_management -> startActivity(Intent(this, EventsManagementActivity::class.java))
                R.id.nav_adoption_history -> {} // Current activity
                R.id.nav_dogfood -> {
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                }
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        // RecyclerView and Search setup
        recyclerView = findViewById(R.id.recycleradoptionhistory)
        searchEditText = findViewById(R.id.etSearch)
        btnResetSearch = findViewById(R.id.btnResetSearch) // 2. Initialization

        adapter = AdoptionHistoryAdapter(this, fullAdoptionHistoryList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Setup Listeners
        setupSearchListener()
        setupResetButton() // 3. Setup the reset button

        loadAdoptions()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    // -------------------------------------------------------------
    // Filtering and Reset Functions
    // -------------------------------------------------------------

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAdoptions(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupResetButton() {
        btnResetSearch.setOnClickListener {
            // Clear the search field
            searchEditText.setText("")

            // Restore the full list data using the adapter's setData function
            adapter.setData(fullAdoptionHistoryList)

            Toast.makeText(this, "Search reset. Showing all records.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun filterAdoptions(query: String) {
        val filtered = fullAdoptionHistoryList.filter {
            it.dogName.contains(query, ignoreCase = true) ||
                    it.ownerName.contains(query, ignoreCase = true)
        }
        adapter.setData(filtered)
    }

    // -------------------------------------------------------------
    // Data Loading Function
    // -------------------------------------------------------------

    private fun loadAdoptions() {
        fullAdoptionHistoryList.clear()
        adapter.setData(emptyList())
        Toast.makeText(this, "Loading adoption records...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Get all 'Adoption' documents (Requires Collection Group Index)
                val adoptionSnapshots = firestore.collectionGroup("Adoption").get().await()

                val combinedDataList = mutableListOf<AdoptionHistory>()

                // 2. Iterate and Join Data
                for (adoptionDoc in adoptionSnapshots.documents) {
                    val dogId = adoptionDoc.getString("dogId")
                    // Correctly extract the parent User ID from the document reference path
                    val userId = adoptionDoc.reference.parent.parent?.id

                    if (dogId.isNullOrEmpty() || userId.isNullOrEmpty()) {
                        Log.w(TAG, "Skipping document ${adoptionDoc.id}: Missing dogId or failed UserID extraction.")
                        continue
                    }

                    // 3. Fetch Dog Details (Using confirmed path)
                    val dogDoc = firestore.collection(DOG_COLLECTION_PATH).document(dogId).get().await()

                    // 4. Fetch User/Client Details (Using confirmed path 'Users')
                    val userDoc = firestore.collection("Users").document(userId).get().await()

                    if (dogDoc.exists() && userDoc.exists()) {

                        val combinedData = AdoptionHistory(
                            // Dog Details
                            dogName = dogDoc.getString("name") ?: "N/A",
                            sex = dogDoc.getString("sex") ?: "N/A",
                            age = dogDoc.getLong("age")?.toString() ?: "N/A",

                            // Client/Adoption Details
                            ownerName = userDoc.getString("name") ?: "Client N/A",
                            documentUrl = adoptionDoc.getString("fileUrl") ?: "",
                            uploadDate = adoptionDoc.getDate("uploadDate"),
                            documentId = adoptionDoc.id
                        )
                        combinedDataList.add(combinedData)
                    } else {
                        Log.w(TAG, "Missing linked document: Dog ID $dogId not found OR User ID $userId not found.")
                    }
                }

                // 5. Update the UI
                withContext(Dispatchers.Main) {
                    fullAdoptionHistoryList.addAll(combinedDataList.sortedByDescending { it.uploadDate })
                    adapter.setData(fullAdoptionHistoryList)
                    Toast.makeText(this@AdoptionHistoryActivity, "Loaded ${fullAdoptionHistoryList.size} adoption records.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Fatal Error loading adoptions history: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdoptionHistoryActivity, "Failed to load data. Check logs for index/path errors.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}