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
import vcmsa.projects.thedoghouse_prototype.databinding.ActivityDonationDogFoodBinding

class DonationDogFoodActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "DogFoodHistory"

    private lateinit var binding: ActivityDonationDogFoodBinding
    private lateinit var dogFoodAdapter: DogFoodDonationHistoryAdapter

    private lateinit var allDogFood: MutableList<HistoryDogFoodRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDonationDogFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allDogFood = mutableListOf()

        // === 1. Toolbar and Drawer Setup ===
        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        val navView = binding.navigationView

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
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

        // === 2. Setup Adapter and RecyclerView (UPDATED) ===
        dogFoodAdapter = DogFoodDonationHistoryAdapter(mutableListOf()) { documentId ->
            // ⚡️ CALL THE CONFIRMATION DIALOG ⚡️
            showDeleteConfirmationDialog(documentId)
        }

        binding.recyclerdogfood.layoutManager = LinearLayoutManager(this)
        binding.recyclerdogfood.adapter = dogFoodAdapter

        // === 3. History Button Listeners (Navigation) ===
        binding.FundsBtn.setOnClickListener {
            startActivity(Intent(this, DonationHistoryActivity::class.java))
            finish()
        }

        binding.DogFoodBtn.setOnClickListener {
            loadDogFoodHistory()
        }

        binding.MedsBtn.setOnClickListener {
            startActivity(Intent(this, DonationMedsHistoryActivity::class.java))
            finish()
        }

        loadDogFoodHistory()
    }

    // ----------------------------------------------------------------------
    // --- FIREBASE LOADING FUNCTION ------------------------------------------
    // ----------------------------------------------------------------------
    private fun loadDogFoodHistory() {
        binding.recyclerdogfood.visibility = View.VISIBLE
        allDogFood.clear()

        firestore.collectionGroup("DogFoodDonations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val dogFoodRecords = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(HistoryDogFoodRecord::class.java)
                }
                allDogFood.addAll(dogFoodRecords)
                dogFoodAdapter.updateData(dogFoodRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching dog food history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Dog Food: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ----------------------------------------------------------------------
    // --- NEW: CONFIRMATION DIALOG FUNCTION ----------------------------------
    // ----------------------------------------------------------------------
    private fun showDeleteConfirmationDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to permanently delete this dog food donation record?")
            .setPositiveButton("Delete") { dialog, which ->
                // If admin confirms, proceed with the actual deletion
                performDeleteDogFoodDonation(documentId)
            }
            .setNegativeButton("Cancel", null) // Do nothing on cancel
            .show()
    }


    // ----------------------------------------------------------------------
    // --- ACTUAL FIREBASE DELETION FUNCTION (RENAMED) ------------------------
    // ----------------------------------------------------------------------
    private fun performDeleteDogFoodDonation(documentId: String) {

        // Find the record in the master list to get the userId
        val recordToDelete = allDogFood.find { it.documentId == documentId }

        if (recordToDelete == null || recordToDelete.userId.isNullOrEmpty()) {
            Log.e(TAG, "Deletion failed: Cannot find record or userId for document ID: $documentId")
            Toast.makeText(this, "Error: Could not find record details or user ID for deletion.", Toast.LENGTH_LONG).show()
            return
        }

        // ⚡️ Delete using the full path: /Users/{userId}/DogFoodDonations/{documentId} ⚡️
        val docRef = firestore.collection("Users")
            .document(recordToDelete.userId!!)
            .collection("DogFoodDonations")
            .document(documentId)

        docRef.delete()
            .addOnSuccessListener {
                // Remove item from RecyclerView list and master list, then update UI
                dogFoodAdapter.removeItem(documentId)
                allDogFood.removeAll { it.documentId == documentId }
                Toast.makeText(this, "Dog Food record deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting document: $documentId", e)
                Toast.makeText(this, "Failed to delete record.", Toast.LENGTH_SHORT).show()
            }
    }
}