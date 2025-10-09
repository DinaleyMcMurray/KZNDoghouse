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
import kotlin.concurrent.thread // Import for simple background threading

class DogManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DogAdapter
    private lateinit var dogList: MutableList<DogDataRecord>

    private val db = FirebaseFirestore.getInstance()
    private val ADMIN_DOC_ID = "AdminUserDocument"
    private val DOGS_COLLECTION_PATH = "Admin/$ADMIN_DOC_ID/AddDog"
    private val CLOUDINARY_FOLDER = "doghouse_app/dogs/" // Matches folder in AddDogActivity.kt
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
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dogListener?.remove()
    }

    private fun setupDogDataListener() {
        dogListener = db.collection(DOGS_COLLECTION_PATH)
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    Toast.makeText(this, "Failed to load dogs: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val newDogList = mutableListOf<DogDataRecord>()
                    for (doc in snapshots.documents) {
                        try {
                            val dog =
                                doc.toObject(DogDataRecord::class.java)?.copy(documentId = doc.id)
                            if (dog != null) {
                                newDogList.add(dog)
                            }
                        } catch (ex: Exception) {
                            Log.e(
                                TAG,
                                "Error converting document to DogDataRecord: ${ex.message}",
                                ex
                            )
                        }
                    }
                    dogList.clear()
                    dogList.addAll(newDogList)
                    adapter.notifyDataSetChanged()
                }
            }
    }

    /**
     * Handles the button click: prompts for confirmation and initiates deletion if confirmed.
     */
    private fun handleAdoptionClick(dog: DogDataRecord) {
        if (dog.documentId.isEmpty()) {
            Toast.makeText(this, "Error: Cannot delete dog without an ID.", Toast.LENGTH_LONG)
                .show()
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
                // Call the function to handle both Firebase and Cloudinary deletion
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
        // 1. Delete the Firestore record first
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
            // 1. Find the starting position of the Public ID (i.e., the start of the folder path)
            val publicIdStartIndex = imageUrl.indexOf(folderAnchor)

            // 2. Find the file extension (.jpg, .png, etc.) to know where the ID ends
            val publicIdEndIndex = imageUrl.lastIndexOf(".")

            if (publicIdStartIndex != -1 && publicIdEndIndex > publicIdStartIndex) {
                // Extract the entire path from the start of "doghouse_app/dogs" up to the file extension
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
                                // Logs the status and the exact ID used for final troubleshooting if it still fails
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