package vcmsa.projects.thedoghouse_prototype

import android.app.AlertDialog // ⚡️ IMPORT for AlertDialog ⚡️
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import vcmsa.projects.thedoghouse_prototype.databinding.ActivityDonationMedsHistoryBinding

class DonationMedsHistoryActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "MedsHistory"

    private lateinit var binding: ActivityDonationMedsHistoryBinding
    private lateinit var medsAdapter: MedsDonationHistoryAdapter

    private lateinit var allMeds: MutableList<HistoryMedsRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDonationMedsHistoryBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        supportActionBar?.hide()
        setContentView(binding.root)

        // Initialize the master list
        allMeds = mutableListOf()

        // === 1. Toolbar and Drawer Setup (Remains the same) ===
        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        val navView = binding.navigationView

        setSupportActionBar(toolbar)
        binding.toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> startActivity(Intent(this, DogManagementActivity::class.java))
                R.id.nav_volunteer_management -> startActivity(Intent(this, VolunteerManagementActivity::class.java))
                R.id.nav_events_management -> startActivity(Intent(this, EventsManagementActivity::class.java))
                R.id.nav_adoption_history -> startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                R.id.nav_dogfood -> startActivity(Intent(this, DonationHistoryActivity::class.java))
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.nav_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        medsAdapter = MedsDonationHistoryAdapter(mutableListOf()) { documentId ->
            // ⚡️ CALL THE CONFIRMATION DIALOG ⚡️
            showDeleteConfirmationDialog(documentId)
        }

        binding.recyclermeds.layoutManager = LinearLayoutManager(this)
        binding.recyclermeds.adapter = medsAdapter

        // === 3. History Button Listeners (Remains the same) ===
        binding.FundsBtn.setOnClickListener {
            startActivity(Intent(this, DonationHistoryActivity::class.java))
            finish()
        }

        binding.DogFoodBtn.setOnClickListener {
            startActivity(Intent(this, DonationDogFoodActivity::class.java))
            finish()
        }

        binding.MedsBtn.setOnClickListener {
            loadMedsHistory()
        }

        loadMedsHistory()
    }

    private fun loadMedsHistory() {
        binding.recyclermeds.visibility = View.VISIBLE
        allMeds.clear()

        firestore.collectionGroup("MedsDonations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val medsRecords = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(HistoryMedsRecord::class.java)
                }
                allMeds.addAll(medsRecords)
                medsAdapter.updateData(medsRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching medication history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Medication: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    // ----------------------------------------------------------------------
    // --- NEW: CONFIRMATION DIALOG FUNCTION ----------------------------------
    // ----------------------------------------------------------------------
    private fun showDeleteConfirmationDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to permanently delete this medication donation record?")
            .setPositiveButton("Delete") { dialog, which ->
                // If admin confirms, proceed with the actual deletion
                performDeleteMedsDonation(documentId)
            }
            .setNegativeButton("Cancel", null) // Do nothing on cancel
            .show()
    }

    // ----------------------------------------------------------------------
    // --- ACTUAL FIREBASE DELETION FUNCTION (RENAMED) ------------------------
    // ----------------------------------------------------------------------
    private fun performDeleteMedsDonation(documentId: String) {

        // Find the record in the master list to get the userId
        val recordToDelete = allMeds.find { it.documentId == documentId }

        if (recordToDelete == null || recordToDelete.userId.isNullOrEmpty()) {
            Log.e(TAG, "Deletion failed: Cannot find record or userId for document ID: $documentId")
            Toast.makeText(this, "Error: Could not find record details or user ID for deletion.", Toast.LENGTH_LONG).show()
            return
        }

        // ⚡️ Delete using the full path: /Users/{userId}/MedsDonations/{documentId} ⚡️
        val docRef = firestore.collection("Users")
            .document(recordToDelete.userId!!)
            .collection("MedsDonations")
            .document(documentId)

        docRef.delete()
            .addOnSuccessListener {
                // Remove item from RecyclerView list and master list, then update UI
                medsAdapter.removeItem(documentId)
                allMeds.removeAll { it.documentId == documentId }
                Toast.makeText(this, "Medication record deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting document: $documentId", e)
                Toast.makeText(this, "Failed to delete record.", Toast.LENGTH_SHORT).show()
            }
    }
}