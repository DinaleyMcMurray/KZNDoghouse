package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager // <-- New Import
import androidx.recyclerview.widget.RecyclerView // <-- New Import
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore // <-- New Import
import com.google.firebase.firestore.toObject

class EventsManagementActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var AddEventBtn: Button

    // RecyclerView variables
    private lateinit var recyclerEvents: RecyclerView // <-- New
    private lateinit var eventAdapter: EventAdapter    // <-- New
    private val eventsList = mutableListOf<EventData>() // <-- New

    // Firestore setup
    private val firestore = FirebaseFirestore.getInstance()
    private val ADMIN_DOC_ID = "AdminUserDocument"
    private val EVENTS_COLLECTION_PATH = "Admin/$ADMIN_DOC_ID/CreateEvents" // Path to events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_events_management)

        // 1. Initialize UI/Navigation Components
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)
        AddEventBtn = findViewById(R.id.AddEventBtn)
        recyclerEvents = findViewById(R.id.recyclerEvents) // <-- Initialize RecyclerView

        // 2. Set Listeners / Toolbar
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        AddEventBtn.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        // 3. Setup RecyclerView
        eventAdapter = EventAdapter(
            eventsList,
            onEditClick = { event ->
                // 🚨 FIX: Implement logic to open CreateEventActivity in EDIT mode
                val intent = Intent(this, CreateEventActivity::class.java).apply {
                    // Put the entire Parcelable EventData object into the Intent
                    putExtra("EVENT_TO_EDIT", event)
                }
                startActivity(intent)
            },
            onDeleteClick = { event ->
                deleteEvent(event)
            }
        )
        recyclerEvents.apply {
            layoutManager = LinearLayoutManager(this@EventsManagementActivity)
            adapter = eventAdapter
        }

        // 4. Fetch Data
        fetchEventsData()


        // 5. Handle Nav Clicks (unchanged)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {
                    startActivity(Intent(this, DogManagementActivity::class.java))
                }
                R.id.nav_volunteer_management -> {
                    startActivity(Intent(this, VolunteerManagementActivity::class.java))
                }
                R.id.nav_events_management -> {
                    // Already here
                }
                R.id.nav_adoption_history -> {
                    startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                }
                R.id.nav_dogfood -> {
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                }
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        // 6. Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchEventsData() {
        firestore.collection(EVENTS_COLLECTION_PATH)
            .orderBy("dateCreated") // Order by creation date
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("EventsManagement", "Listen failed.", e)
                    Toast.makeText(this, "Failed to load events.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val newEvents = snapshot.documents.mapNotNull { document ->
                        // Use toObject to map Firestore document to EventData class
                        document.toObject<EventData>()
                    }
                    eventAdapter.updateData(newEvents)
                }
            }
    }

    private fun deleteEvent(event: EventData) {
        if (event.documentId.isEmpty()) {
            Toast.makeText(this, "Error: Event ID missing.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection(EVENTS_COLLECTION_PATH)
            .document(event.documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Event '${event.name}' deleted successfully!", Toast.LENGTH_SHORT).show()
                // Fetch data listener handles the automatic UI update
            }
            .addOnFailureListener { e ->
                Log.e("EventsManagement", "Error deleting event: ${e.message}", e)
                Toast.makeText(this, "Failed to delete event: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}