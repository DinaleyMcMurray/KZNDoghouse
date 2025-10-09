package vcmsa.projects.thedoghouse_prototype

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.cloudinary.android.MediaManager
import kotlin.concurrent.thread
import java.util.Date

class AdoptionHistoryActivity : AppCompatActivity() {

    private val TAG = "AdoptionHistoryActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var btnResetSearch: Button
    private val firestore = FirebaseFirestore.getInstance()

    private var fullAdoptionHistoryList = mutableListOf<AdoptionHistory>()
    private lateinit var adapter: AdoptionHistoryAdapter

    private val DOG_COLLECTION_PATH = "Admin/AdminUserDocument/AddDog"
    // NOTE: This folder path is now only used for string manipulation in the deletion function
    private val CLOUDINARY_DOCUMENTS_FOLDER = "doghouse_app/adoption_documents"

    // ⚠️ CLOUDINARY CONFIGURATION (MUST BE HERE TO INITIALIZE!) ⚠️
    private val CLOUDINARY_CLOUD_NAME = "dyuieeirb"
    private val CLOUDINARY_API_KEY = "959111626652188"
    private val CLOUDINARY_API_SECRET = "MPC45jC70zK656BiiADN-0ULohs"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption_history)

        // --- Cloudinary Initialization ---
        try {
            val config = mapOf(
                "cloud_name" to CLOUDINARY_CLOUD_NAME,
                "api_key" to CLOUDINARY_API_KEY,
                "api_secret" to CLOUDINARY_API_SECRET
            )
            // Use applicationContext as previously established
            MediaManager.init(applicationContext, config)
        } catch (e: Exception) {
            // Log if initialization fails (e.g., if called twice, though the SDK usually prevents this)
            Log.e("Cloudinary", "Initialization failed: ${e.message}")
        }

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
                R.id.nav_adoption_history -> {}
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
        btnResetSearch = findViewById(R.id.btnResetSearch)

        // Initialize adapter with the Delete Listener
        adapter = AdoptionHistoryAdapter(this, fullAdoptionHistoryList) { adoptionRecord ->
            showDeleteConfirmationDialog(adoptionRecord)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setupSearchListener()
        setupResetButton()

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
            searchEditText.setText("")
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
    // Data Loading Function (Forgiving Load Logic)
    // -------------------------------------------------------------

    private fun loadAdoptions() {
        fullAdoptionHistoryList.clear()
        adapter.setData(emptyList())
        Toast.makeText(this, "Loading adoption records...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Get all 'Adoption' documents
                val adoptionSnapshots = firestore.collectionGroup("Adoption").get().await()

                val combinedDataList = mutableListOf<AdoptionHistory>()

                // 2. Iterate and Join Data
                for (adoptionDoc in adoptionSnapshots.documents) {
                    val dogId = adoptionDoc.getString("dogId")

                    // Extract UserID from the path: Users/{userId}/Adoption/{docId}
                    val userId = adoptionDoc.reference.parent.parent?.id

                    if (dogId.isNullOrEmpty() || userId.isNullOrEmpty()) {
                        Log.w(TAG, "Skipping document ${adoptionDoc.id}: Missing dogId or failed UserID extraction.")
                        continue
                    }

                    // 3. Fetch Dog Details
                    val dogDoc = firestore.collection(DOG_COLLECTION_PATH).document(dogId).get().await()

                    // 4. Fetch User/Client Details
                    val userDoc = firestore.collection("Users").document(userId).get().await()

                    // --- FORGIVING LOAD LOGIC: DETERMINE VALUES ---

                    // Dog Details: If dogDoc is missing, use N/A placeholders
                    val dogName = dogDoc.getString("name") ?:
                    if (dogDoc.exists()) "N/A" else "Dog ID: $dogId N/A (DELETED)"
                    val sex = dogDoc.getString("sex") ?: "N/A"
                    val age = dogDoc.getLong("age")?.toString() ?: "N/A"

                    // Client/Adoption Details: If userDoc is missing, use N/A placeholders
                    val ownerName = userDoc.getString("name") ?:
                    if (userDoc.exists()) "Client N/A" else "User ID: $userId N/A (DELETED)"

                    // Log a warning if data is missing, but DO NOT skip the record
                    if (!dogDoc.exists() || !userDoc.exists()) {
                        Log.w(TAG, "WARN: Linked document(s) missing for Adoption Doc ${adoptionDoc.id}. Loading with N/A fields.")
                    }

                    // --- CREATE RECORD (ALWAYS ADDS THE RECORD HERE) ---
                    val combinedData = AdoptionHistory(
                        dogName = dogName,
                        sex = sex,
                        age = age,
                        ownerName = ownerName,
                        documentUrl = adoptionDoc.getString("fileUrl") ?: "",
                        uploadDate = adoptionDoc.getDate("uploadDate"),
                        documentId = adoptionDoc.id,
                        userId = userId
                    )
                    combinedDataList.add(combinedData)
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
                    Toast.makeText(this@AdoptionHistoryActivity, "Failed to load data. Check logs.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Deletion Logic (Updated for guaranteed refresh)
    // -------------------------------------------------------------

    private fun showDeleteConfirmationDialog(record: AdoptionHistory) {
        if (record.userId.isEmpty()) {
            Toast.makeText(this, "Cannot delete: Client ID is missing from the record path.", Toast.LENGTH_LONG).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to permanently delete the adoption record for ${record.dogName} by ${record.ownerName}? This action is irreversible.")
            .setPositiveButton("Delete") { dialog, which ->
                performDeleteAdoptionRecordAndDocument(record)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performDeleteAdoptionRecordAndDocument(record: AdoptionHistory) {
        if (record.userId.isEmpty() || record.documentId.isEmpty()) {
            Toast.makeText(this, "Error: Missing ID for deletion.", Toast.LENGTH_LONG).show()
            return
        }

        // 1. Define the Firestore document reference
        val docRef = firestore.collection("Users")
            .document(record.userId)
            .collection("Adoption")
            .document(record.documentId)

        // 2. Delete the Firestore record first
        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Adoption record deleted from Firestore.", Toast.LENGTH_SHORT).show()

                // 3. Trigger Cloudinary deletion for the uploaded document
                if (record.documentUrl.isNotEmpty()) {
                    deleteFileFromCloudinary(record.documentUrl, record.documentId)
                }

                // 4. Remove item from the local list and force UI update.
                val index = fullAdoptionHistoryList.indexOfFirst { it.documentId == record.documentId }
                if (index != -1) {
                    fullAdoptionHistoryList.removeAt(index)

                    // 🚨 GUARANTEED REFRESH: Re-set the filtered list to the adapter
                    adapter.setData(fullAdoptionHistoryList)
                }

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting adoption record ${record.documentId} from Firestore: ${e.message}", e)
                Toast.makeText(this, "Failed to delete record: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Deletes the file associated with the given URL from Cloudinary.
     * NOTE: The 'imageUrl' parameter is actually the file URL (documentUrl).
     */
    private fun deleteFileFromCloudinary(fileUrl: String, documentId: String) {
        // Based on your AdoptionActivity, the upload folder is "doghouse_app/adoption_forms"
        val folderAnchor = "doghouse_app/adoption_forms"

        try {
            // 1. Find the starting position of the Public ID
            val publicIdStartIndex = fileUrl.indexOf(folderAnchor)

            // 2. Find the file extension (.pdf, .doc, etc.) to know where the ID ends
            val publicIdEndIndex = fileUrl.lastIndexOf(".")

            if (publicIdStartIndex != -1 && publicIdEndIndex > publicIdStartIndex) {
                // Extract the entire path from the start of the folder up to the file extension
                val publicIdToDelete = fileUrl.substring(publicIdStartIndex, publicIdEndIndex)

                thread {
                    try {
                        // FIX: MediaManager.get() now works because init() was called in onCreate
                        val result = MediaManager.get().getCloudinary()
                            .uploader().destroy(publicIdToDelete, mapOf<String, Any>())

                        runOnUiThread {
                            val status = result["result"] as? String
                            if (status == "ok") {
                                Log.i(TAG, "Cloudinary document delete success for adoption ID $documentId. ID: $publicIdToDelete")
                                Toast.makeText(this, "Document deleted from Cloudinary.", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e(TAG, "Cloudinary document delete FAILED. Status: $status. ID used: $publicIdToDelete")
                                Toast.makeText(this, "Warning: Cloudinary deletion failed.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        // This catch block will no longer hit the IllegalStateException
                        Log.e(TAG, "Cloudinary deletion failed in background thread: ${e.message}", e)
                        runOnUiThread {
                            Toast.makeText(this, "Warning: Cloudinary deletion failed with exception.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Log.w(TAG, "Could not reliably extract full Public ID from document URL: $fileUrl")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Cloudinary deletion process failed: ${e.message}")
        }
    }
}