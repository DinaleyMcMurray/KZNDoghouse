package vcmsa.projects.thedoghouse_prototype

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
    // Ensure this class name matches your actual adapter file (e.g., MedsAdapter)
    private lateinit var medsAdapter: MedsDonationHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚡️ Initialize View Binding ⚡️
        binding = ActivityDonationMedsHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // === 1. Toolbar and Drawer Setup ===
        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        val navView = binding.navigationView

        setSupportActionBar(toolbar)
        binding.toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ✅ FIX 1: Corrected Navigation Drawer Listener (Removed nesting)
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

        // === 2. Setup Adapter and RecyclerView ===
        // ✅ FIX 2: Assuming adapter class is MedsAdapter (was MedsDonationHistoryAdapter)
        medsAdapter = MedsDonationHistoryAdapter(mutableListOf())

        // The ID of the RecyclerView in your layout is 'recyclermeds'
        binding.recyclermeds.layoutManager = LinearLayoutManager(this)
        binding.recyclermeds.adapter = medsAdapter

        // === 3. History Button Listeners (Navigation) ===
        binding.FundsBtn.setOnClickListener {
            val intent = Intent(this, DonationHistoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.DogFoodBtn.setOnClickListener {
            val intent = Intent(this, DonationDogFoodActivity::class.java)
            startActivity(intent)
            finish()
        }

        // MedsBtn: Reloads data since we are already on this page
        binding.MedsBtn.setOnClickListener {
            loadMedsHistory()
        }

        // Load data automatically when the page loads
        loadMedsHistory()
    } // ✅ FIX 3: Ensures onCreate method is closed properly

    // ----------------------------------------------------------------------
    // --- FIREBASE LOADING FUNCTION (Dedicated) ------------------------------
    // ----------------------------------------------------------------------

    private fun loadMedsHistory() {
        binding.recyclermeds.visibility = View.VISIBLE

        firestore.collectionGroup("MedsDonations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val medsRecords = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(HistoryMedsRecord::class.java)
                }
                medsAdapter.updateData(medsRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching medication history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Medication: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }
}