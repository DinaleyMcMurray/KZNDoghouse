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
import vcmsa.projects.thedoghouse_prototype.databinding.ActivityDonationHistoryBinding

class DonationHistoryActivity : AppCompatActivity() {

    // --- Firebase Instance ---
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "FundsHistory"

    // ⚡️ View Binding Instance ⚡️
    private lateinit var binding: ActivityDonationHistoryBinding

    // --- Adapter Variables ---
    // NOTE: This MUST be FundsAdapter if you created the simple adapter
    private lateinit var fundsAdapter: DonationHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚡️ Initialize View Binding ⚡️
        binding = ActivityDonationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // === 1. Toolbar and Drawer Setup ===
        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        val navView = binding.navigationView // Uses View Binding

        setSupportActionBar(toolbar)
        // Set up the menu icon to open the drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ⚡️ ADAPTED: Navigation Drawer Listener (Inline format) ⚡️
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
        // NOTE: Ensure FundsAdapter class exists and is used
        fundsAdapter = DonationHistoryAdapter(mutableListOf())

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

    // ----------------------------------------------------------------------
    // --- FIREBASE LOADING FUNCTION ------------------------------------------
    // ----------------------------------------------------------------------

    private fun loadFundsHistory() {
        binding.recyclerfunds.visibility = View.VISIBLE

        firestore.collectionGroup("FundsDonations")
            .orderBy("dateSubmitted", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fundsRecords = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(HistoryFundsRecord::class.java)
                }
                fundsAdapter.updateData(fundsRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching funds history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Funds: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}