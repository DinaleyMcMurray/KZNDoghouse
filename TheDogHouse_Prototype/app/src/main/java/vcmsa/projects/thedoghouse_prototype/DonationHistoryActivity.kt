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
import vcmsa.projects.thedoghouse_prototype.databinding.ActivityDonationHistoryBinding

class DonationHistoryActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "FundsHistory"

    private lateinit var binding: ActivityDonationHistoryBinding

    private lateinit var fundsAdapter: DonationHistoryAdapter

    private lateinit var allDonations: MutableList<HistoryFundsRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDonationHistoryBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        supportActionBar?.hide()
        setContentView(binding.root)

        allDonations = mutableListOf()

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
                R.id.nav_sponsor -> startActivity(Intent(this, SponsorManagementActivity::class.java))
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.nav_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        fundsAdapter = DonationHistoryAdapter(mutableListOf()) { documentId ->
            showDeleteConfirmationDialog(documentId)
        }

        binding.recyclerfunds.layoutManager = LinearLayoutManager(this)
        binding.recyclerfunds.adapter = fundsAdapter

        // === 3. History Button Listeners (Navigation Tabs) ===
        binding.FundsBtn.setOnClickListener {
            loadFundsHistory()
        }

        binding.DogFoodBtn.setOnClickListener {
            val intent = Intent(this, DonationDogFoodActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.MedsBtn.setOnClickListener {
            val intent = Intent(this, DonationMedsHistoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Load data automatically when the page loads
        loadFundsHistory()
    }

    private fun loadFundsHistory() {
        binding.recyclerfunds.visibility = View.VISIBLE
        allDonations.clear()

        firestore.collectionGroup("FundsDonations")
            .orderBy("dateSubmitted", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fundsRecords = querySnapshot.documents.mapNotNull { document ->
                    // ⚡️ Capture all fields, including the required userId ⚡️
                    document.toObject(HistoryFundsRecord::class.java)
                }

                allDonations.addAll(fundsRecords) // Keep a copy for finding user ID later
                fundsAdapter.updateData(fundsRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching funds history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Funds: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showDeleteConfirmationDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to permanently delete this funds donation record?")
            .setPositiveButton("Delete") { dialog, which ->
                // If admin confirms, proceed with the actual deletion
                performDeleteFundsDonation(documentId)
            }
            .setNegativeButton("Cancel", null) // Do nothing on cancel
            .show()
    }

    private fun performDeleteFundsDonation(documentId: String) {

        // 🔥 CRITICAL: Find the userId from the master list (allDonations) 🔥
        val recordToDelete = allDonations.find { it.documentId == documentId }

        // Ensure the record and required userId are available
        if (recordToDelete == null || recordToDelete.userId.isNullOrEmpty()) {
            Log.e(TAG, "Deletion failed: Cannot find record or userId for document ID: $documentId")
            Toast.makeText(this, "Error: Could not find record details or user ID for deletion.", Toast.LENGTH_LONG).show()
            return
        }

        val docRef = firestore.collection("Users")
            .document(recordToDelete.userId!!) // Non-null assertion is safe after the check above
            .collection("FundsDonations")
            .document(documentId)

        docRef.delete()
            .addOnSuccessListener {
                // Remove item from RecyclerView list and master list, then update UI
                fundsAdapter.removeItem(documentId)
                allDonations.removeAll { it.documentId == documentId }
                Toast.makeText(this, "Donation record deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting document: $documentId", e)
                Toast.makeText(this, "Failed to delete record.", Toast.LENGTH_SHORT).show()
            }
    }
}