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
import androidx.appcompat.app.AlertDialog
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import kotlin.concurrent.thread

// NOTE: You MUST create a FirestoreDogData class with 'dateAdded: Date?'
// We will use this new class for all internal operations here.

class DogManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DogAdapter
    // CHANGE 1: Use the dedicated Firestore data class
    private lateinit var dogList: MutableList<FirestoreDogData>

    private val db = FirebaseFirestore.getInstance()

    // Using the single, fixed Admin path for dog management
    private val ADMIN_DOC_ID = "AdminUserDocument"
    private val DOGS_COLLECTION_PATH = "Admin/$ADMIN_DOC_ID/AddDog"

    private val CLOUDINARY_FOLDER = "doghouse_app/dogs/"
    private val TAG = "DogManagementActivity"

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private var dogListener: ListenerRegistration? = null

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
        // CHANGE 2: Initialize list with the new class type
        dogList = mutableListOf()
        // NOTE: DogAdapter must be updated to accept List<FirestoreDogData>
        adapter = DogAdapter(
            dogList,
            onEditClick = { dog ->
                val intent = Intent(this, AddDogActivity::class.java).apply {
                    // NOTE: AddDogActivity must be updated to expect FirestoreDogData
                    putExtra("DOG_TO_EDIT", dog)
                }
                startActivity(intent)
            },
            // CHANGE 3: Click handler uses FirestoreDogData
            onAdoptedClick = { dog ->
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

        // 3. Toolbar navigation setup
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 4. Navigation View Item Selection Listener (Handles navigation)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {}
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
                R.id.nav_volunteer_management -> startActivity(Intent(this, VolunteerManagementActivity::class.java))
                R.id.nav_events_management -> startActivity(Intent(this,EventsManagementActivity::class.java))
                R.id.nav_adoption_history -> startActivity(Intent(this,AdoptionHistoryActivity::class.java))
                R.id.nav_dogfood -> startActivity(Intent(this, DonationHistoryActivity::class.java))
                R.id.nav_sponsor -> startActivity(Intent(this, SponsorManagementActivity::class.java))
                else -> false // Handle unhandled IDs gracefully
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dogListener?.remove()
    }

    /**
     * Uses a direct collection reference to the single Admin path.
     */
    private fun setupDogDataListener() {
        // Now using the exact path where all dog data is stored
        dogListener = db.collection(DOGS_COLLECTION_PATH)
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for dog management.", e)
                    Toast.makeText(this, "Failed to load dogs: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // CHANGE 4: Use the dedicated Firestore data class
                    val newDogList = mutableListOf<FirestoreDogData>()
                    for (doc in snapshots.documents) {
                        try {
                            // CHANGE 5: Map to the dedicated Firestore data class
                            val dog = doc.toObject(FirestoreDogData::class.java)?.copy(documentId = doc.id)
                            dog?.let {
                                newDogList.add(it)
                            }
                        } catch (ex: Exception) {
                            // CHANGE 6: Update log message
                            Log.e(
                                TAG,
                                "Error converting document to FirestoreDogData: ${ex.message}",
                                ex
                            )
                        }
                    }

                    dogList.clear()
                    dogList.addAll(newDogList)
                    adapter.notifyDataSetChanged()
                    Log.d(TAG, "Dogs loaded: ${dogList.size}.")
                }
            }
    }

    // CHANGE 7: Parameter type updated to FirestoreDogData
    private fun handleAdoptionClick(dog: FirestoreDogData) {
        if (dog.documentId.isEmpty()) {
            Toast.makeText(this, "Error: Dog ID is missing. Cannot proceed.", Toast.LENGTH_LONG).show()
            return
        }

        if (dog.status != "Available for Adoption") {
            Toast.makeText(
                this,
                "${dog.name} is already ${dog.status}. The record cannot be deleted this way.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Show confirmation dialog before permanent deletion
        AlertDialog.Builder(this)
            .setTitle("Confirm Adoption & Deletion")
            .setMessage("Are you sure you want to permanently delete ${dog.name}'s record? This confirms the adoption and removes them from the available list.")
            .setPositiveButton("Yes, Adopt & Delete") { dialog, _ ->
                // Use the DOGS_COLLECTION_PATH which is correct for this data structure
                deleteDogRecordAndImage(dog.documentId, dog.name, dog.imageUrl)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    /**
     * Orchestrates the deletion of the Firestore record and the Cloudinary image.
     */
    private fun deleteDogRecordAndImage(dogId: String, dogName: String, imageUrl: String) {
        // 1. Delete the Firestore record using the fixed collection path and document ID
        db.collection(DOGS_COLLECTION_PATH).document(dogId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "$dogName adopted and removed successfully!",
                    Toast.LENGTH_LONG
                ).show()

                // 2. Trigger Cloudinary deletion
                if (imageUrl.isNotEmpty()) {
                    deleteImageFromCloudinary(imageUrl, dogName)
                }
                // The real-time listener handles the UI update automatically
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting dog $dogName from Firestore: ${e.message}", e)
                Toast.makeText(this, "Failed to delete dog record: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun deleteImageFromCloudinary(imageUrl: String, dogName: String) {
        val folderAnchor = "doghouse_app/dogs"

        try {
            // Logic to extract the Cloudinary Public ID
            val publicIdStartIndex = imageUrl.indexOf(folderAnchor)
            val publicIdEndIndex = imageUrl.lastIndexOf(".")

            if (publicIdStartIndex != -1 && publicIdEndIndex > publicIdStartIndex) {
                val publicIdToDelete = imageUrl.substring(publicIdStartIndex, publicIdEndIndex)

                thread {
                    try {
                        val result = MediaManager.get().getCloudinary()
                            .uploader().destroy(publicIdToDelete, mapOf<String, Any>())

                        runOnUiThread {
                            val status = result["result"] as? String
                            if (status == "ok") {
                                Log.i(TAG, "Cloudinary delete success for $dogName: $publicIdToDelete")
                                Toast.makeText(this, "Image for $dogName deleted from Cloudinary.", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e(TAG, "Cloudinary delete FAILED for $dogName: Status $status. ID used: $publicIdToDelete")
                                Toast.makeText(this, "Warning: Failed to delete image. ID or settings may be wrong.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Cloudinary deletion failed in background thread: ${e.message}", e)
                        runOnUiThread {
                            Toast.makeText(this, "Warning: Cloudinary deletion failed with exception.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Log.w(TAG, "Could not reliably extract full Public ID from URL: $imageUrl")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Cloudinary deletion process failed: ${e.message}")
        }
    }
}