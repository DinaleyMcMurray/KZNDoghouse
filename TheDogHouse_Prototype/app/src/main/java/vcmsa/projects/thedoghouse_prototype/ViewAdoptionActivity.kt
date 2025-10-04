// ViewAdoptionActivity.kt

package vcmsa.projects.thedoghouse_prototype


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// IMPORTANT: This activity requires a Collection Group Index on the "AddDog" collection!
class ViewAdoptionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DogAdapterPublic
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "ViewAdoptionActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_adoption)

        // Handle system bars/insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Setup RecyclerView
        recyclerView = findViewById(R.id.viewadoptionrecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with an empty list and context
        adapter = DogAdapterPublic(mutableListOf(), this)
        recyclerView.adapter = adapter

        // 2. Fetch Data
        fetchAvailableDogs()
    }

    private fun fetchAvailableDogs() {
        // 1. Get a reference to the 'AddDog' Collection Group.
        // This is necessary because the collection is nested under 'AdminUserDocument'.
        db.collectionGroup("AddDog")
            // 2. Filter for only dogs that are available for adoption
            .whereEqualTo("status", "Available for Adoption")
            // 3. Order by date (optional, but good practice)
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->

                val availableDogs = querySnapshot.documents.mapNotNull { document ->
                    try {
                        // Map each Firestore document to your data class
                        document.toObject(DogDataRecord::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error mapping document ${document.id}: ${e.message}", e)
                        null
                    }
                }

                adapter.updateData(availableDogs)

                if (availableDogs.isEmpty()) {
                    Toast.makeText(this, "No dogs are available for adoption right now.", Toast.LENGTH_LONG).show()
                }

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching dogs from collection group: ${e.message}", e)
                Toast.makeText(this, "Failed to load dogs. Please check network/index.", Toast.LENGTH_LONG).show()
                // A failure here often means the required index is missing.
            }
    }
}