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
import vcmsa.projects.thedoghouse_prototype.databinding.ActivityDonationDogFoodBinding

class DonationDogFoodActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "DogFoodHistory"

    private lateinit var binding: ActivityDonationDogFoodBinding

    // NOTE: This should be 'DogFoodAdapter' if you followed the simple adapter creation pattern
    private lateinit var dogFoodAdapter: DogFoodDonationHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚡️ Initialize View Binding ⚡️
        binding = ActivityDonationDogFoodBinding.inflate(layoutInflater)
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

        // ⚡️ ADDED: Navigation Drawer Listener (Inline format) ⚡️
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
        dogFoodAdapter = DogFoodDonationHistoryAdapter(mutableListOf())

        binding.recyclerdogfood.layoutManager = LinearLayoutManager(this)
        binding.recyclerdogfood.adapter = dogFoodAdapter

        // === 3. History Button Listeners (Navigation) ===
        binding.FundsBtn.setOnClickListener {
            val intent = Intent(this, DonationHistoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.DogFoodBtn.setOnClickListener {
            loadDogFoodHistory()
        }

        binding.MedsBtn.setOnClickListener {
            val intent = Intent(this, DonationMedsHistoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Load data automatically when the page loads
        loadDogFoodHistory()
    }

    // ----------------------------------------------------------------------
    // --- FIREBASE LOADING FUNCTION (Dedicated) ------------------------------
    // ----------------------------------------------------------------------

    private fun loadDogFoodHistory() {
        binding.recyclerdogfood.visibility = View.VISIBLE

        firestore.collectionGroup("DogFoodDonations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val dogFoodRecords = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(HistoryDogFoodRecord::class.java)
                }
                dogFoodAdapter.updateData(dogFoodRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching dog food history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Dog Food: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}