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
                // Call the new delete handler
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

        // ... (Navigation setup remains the same) ...
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()
            true
        }
    }

    // Uses a listener for automatic UI updates after create/edit/status change
    // Inside DogManagementActivity.kt

    private fun setupDogDataListener() {
        db.collection(DOGS_COLLECTION_PATH)
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                // ... (error handling for e) ...

                if (snapshot != null) {
                    val newDogs = snapshot.documents.mapNotNull { document ->

                        // CRITICAL DEBUG STEP: Log the raw data map
                        Log.d(TAG, "Raw Data for ${document.id}: ${document.data}")
                        Log.d(TAG, "Raw isVaccinated: ${document.getBoolean("isVaccinated")}")

                        try {
                            // Attempt to map to the Kotlin object
                            document.toObject(DogDataRecord::class.java)?.copy(documentId = document.id)
                        } catch (e: Exception) {
                            Log.e(TAG, "Mapping FAILED for document ${document.id}. Check DogDataRecord type vs Raw Data.", e)
                            null
                        }
                    }
                    adapter.updateData(newDogs)
                }
            }
    }

    private fun handleAdoptionClick(dog: DogDataRecord) {
        if (dog.documentId.isEmpty()) {
            Toast.makeText(this, "Error: Dog ID missing for deletion.", Toast.LENGTH_SHORT).show()
            return
        }

        // Perform the deletion operation
        db.collection(DOGS_COLLECTION_PATH)
            .document(dog.documentId)
            .delete() // <-- This command removes the document entirely
            .addOnSuccessListener {
                // The RecyclerView will update automatically via the snapshot listener
                Toast.makeText(this, "Dog '${dog.name}' marked Adopted and REMOVED from the list.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting dog document: ${e.message}", e)
                Toast.makeText(this, "Failed to remove dog: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}