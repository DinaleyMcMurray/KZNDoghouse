package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration

class DogManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DogAdapter
    private lateinit var dogList: MutableList<DogDataRecord>

    private val db = FirebaseFirestore.getInstance()
    private val ADMIN_DOC_ID = "AdminUserDocument"
    private val DOGS_COLLECTION_PATH = "Admin/$ADMIN_DOC_ID/AddDog"
    private val TAG = "DogManagementActivity"

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private var dogListener: ListenerRegistration? = null // Listener to detach on destroy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_management)

        // Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        val addDogButton: Button = findViewById(R.id.AddDogBtn)
        recyclerView = findViewById(R.id.recyclerdogmanagement)

        setSupportActionBar(toolbar)

        // 1. Initialize RecyclerView components with click handlers
        dogList = mutableListOf()
        adapter = DogAdapter(
            dogList,
            onEditClick = { dog ->
                val intent = Intent(this, AddDogActivity::class.java).apply {
                    putExtra("DOG_TO_EDIT", dog)
                }
                startActivity(intent)
            },
            onAdoptedClick = { dog ->
                // Assuming this handler exists elsewhere in your code
                handleAdoptionClick(dog)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 2. Load data using the Realtime Listener
        setupDogDataListener()

        // Hook up Add Dog button
        addDogButton.setOnClickListener {
            startActivity(Intent(this, AddDogActivity::class.java))
        }

        // 3. Toolbar navigation button setup (Opens the drawer)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 4. Navigation View Item Selection Listener (Handles navigation)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {
                    // Current activity, no action needed besides closing the drawer
                }
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
                R.id.nav_volunteer_management -> {
                    startActivity(Intent(this, VolunteerManagementActivity::class.java))
                }
                R.id.nav_events_management -> {
                    startActivity(Intent(this, EventsManagementActivity::class.java))
                }
                R.id.nav_adoption_history -> {
                    startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                }
                R.id.nav_dogfood -> {
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                }
            }
            // CRITICAL: Close the drawer after an item is selected
            drawerLayout.closeDrawers()
            // CRITICAL: Return true to indicate the item selection was handled
            true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // Detach the Firestore listener to prevent memory leaks
        dogListener?.remove()
    }

    private fun setupDogDataListener() {
        // Listener to fetch all dogs and update the RecyclerView in real-time
        dogListener = db.collection(DOGS_COLLECTION_PATH)
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    Toast.makeText(this, "Failed to load dogs: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val newDogList = mutableListOf<DogDataRecord>()
                    for (doc in snapshots.documents) {
                        try {
                            // Assuming DogDataRecord has a map conversion function or structure
                            val dog = doc.toObject(DogDataRecord::class.java)?.copy(documentId = doc.id)
                            if (dog != null) {
                                newDogList.add(dog)
                            }
                        } catch (ex: Exception) {
                            Log.e(TAG, "Error converting document to DogDataRecord: ${ex.message}", ex)
                        }
                    }
                    dogList.clear()
                    dogList.addAll(newDogList)
                    adapter.notifyDataSetChanged()
                }
            }
    }

    // Placeholder for your adoption click handler (ensure this function exists)
    private fun handleAdoptionClick(dog: DogDataRecord) {
        Toast.makeText(this, "Marking ${dog.name} as adopted...", Toast.LENGTH_SHORT).show()
        // Implement logic to update Firestore status or remove the dog
    }
}