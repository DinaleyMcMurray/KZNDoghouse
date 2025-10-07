package vcmsa.projects.thedoghouse_prototype

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import vcmsa.projects.thedoghouse_prototype.databinding.ActivityDonationHistoryBinding

class DonationHistoryActivity : AppCompatActivity() {

    // --- Firebase Instance ---
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "DonationHistory"

    // ⚡️ View Binding Instance ⚡️
    private lateinit var binding: ActivityDonationHistoryBinding

    // --- UI/Navigation Variables ---
    // (DrawerLayout and Toolbar are defined in binding)

    // --- Adapter Variables ---
    private lateinit var fundsAdapter: DonationHistoryAdapter
    private lateinit var dogFoodAdapter: DonationHistoryAdapter
    private lateinit var medsAdapter: DonationHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚡️ Initialize View Binding and set content view ⚡️
        binding = ActivityDonationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // === 1. Initialize Views ===
        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar

        // === 2. Setup Adapters and Layout Managers ===
        fundsAdapter = DonationHistoryAdapter(mutableListOf())
        dogFoodAdapter = DonationHistoryAdapter(mutableListOf())
        medsAdapter = DonationHistoryAdapter(mutableListOf())

        // Using the simplified RecyclerView IDs from your XML
        setupRecyclerView(binding.recyclerfunds, fundsAdapter)
        setupRecyclerView(binding.recyclerdogfood, dogFoodAdapter)
        setupRecyclerView(binding.recyclermeds, medsAdapter)

        // === 3. Toolbar and Navigation Setup ===
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // === 4. Button Listeners (Calling the three dedicated load methods) ===
        binding.FundsBtn.setOnClickListener {
            showRecyclerView(binding.recyclerfunds)
            loadFundsHistory() // ✅ Dedicated call
        }

        binding.DogFoodBtn.setOnClickListener {
            showRecyclerView(binding.recyclerdogfood)
            loadDogFoodHistory() // ✅ Dedicated call
        }

        binding.MedsBtn.setOnClickListener {
            showRecyclerView(binding.recyclermeds)
            loadMedsHistory() // ✅ Dedicated call
        }

        // Load the default view on startup (Funds)
        binding.FundsBtn.callOnClick()
    }

    // ----------------------------------------------------------------------
    // --- FIREBASE LOADING FUNCTIONS (3 Dedicated Methods) -------------------
    // ----------------------------------------------------------------------

    /**
     * Loads donation history for Funds from the FundsDonations collection group.
     */
    private fun loadFundsHistory() {
        // fundsAdapter.clearData() // Optional: if you had a clear method
        firestore.collectionGroup("FundsDonations")
            .orderBy("dateSubmitted", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fundsRecords = querySnapshot.documents.mapNotNull { document ->
                    // Use toObject for clean mapping based on HistoryFundsRecord model
                    document.toObject(HistoryFundsRecord::class.java)
                }
                fundsAdapter.updateData(fundsRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching funds history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Funds: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Loads donation history for Dog Food from the DogFoodDonations collection group.
     */
    private fun loadDogFoodHistory() {
        // dogFoodAdapter.clearData() // Optional: if you had a clear method
        firestore.collectionGroup("DogFoodDonations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val dogFoodRecords = querySnapshot.documents.mapNotNull { document ->
                    // Use toObject for clean mapping based on HistoryDogFoodRecord model
                    document.toObject(HistoryDogFoodRecord::class.java)
                }
                dogFoodAdapter.updateData(dogFoodRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching dog food history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Dog Food: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Loads donation history for Medication from the MedicationDonations collection group.
     */
    private fun loadMedsHistory() {
        // medsAdapter.clearData() // Optional: if you had a clear method
        firestore.collectionGroup("MedicationDonations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val medsRecords = querySnapshot.documents.mapNotNull { document ->
                    // Use toObject for clean mapping based on HistoryMedsRecord model
                    document.toObject(HistoryMedsRecord::class.java)
                }
                medsAdapter.updateData(medsRecords)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching meds history: ${e.message}", e)
                Toast.makeText(this, "Failed to load Medication: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    // ----------------------------------------------------------------------
    // --- HELPER FUNCTIONS -------------------------------------------------
    // ----------------------------------------------------------------------

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: DonationHistoryAdapter) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun showRecyclerView(recyclerViewToShow: RecyclerView) {
        binding.recyclerfunds.visibility = View.GONE
        binding.recyclerdogfood.visibility = View.GONE
        binding.recyclermeds.visibility = View.GONE

        recyclerViewToShow.visibility = View.VISIBLE
    }
}