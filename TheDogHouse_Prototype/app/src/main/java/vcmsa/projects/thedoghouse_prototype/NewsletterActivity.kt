package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class NewsletterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsletterAdapter
    private lateinit var newsletterList: MutableList<NewsletterItem>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    // ⚡️ Firebase setup ⚡️
    private val firestore = FirebaseFirestore.getInstance()
    private val EVENTS_COLLECTION_PATH = "Admin/AdminUserDocument/CreateEvents"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_newsletter)

        // Apply system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        // Setup toolbar with drawer
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerNewsletters)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ⚡️ FIX 1: Pass the Activity context (this) to the adapter ⚡️
        newsletterList = mutableListOf()
        adapter = NewsletterAdapter(newsletterList, this)
        recyclerView.adapter = adapter

        // ⚡️ Start fetching data ⚡️
        loadEvents()

        // Handle nav item clicks (Your original logic)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> {
                    startActivity(Intent(this, EditProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_newsletter -> {
                    // Current activity
                }
                R.id.nav_fundsdonation -> {
                    startActivity(Intent(this, FundsDonationsActivity::class.java))
                    finish()
                }
                R.id.nav_volunteer -> {
                    startActivity(Intent(this, VolunteerActivity::class.java))
                    finish()
                }
                R.id.nav_adoption -> {
                    startActivity(Intent(this, ViewAdoptionActivity::class.java))
                    finish()
                }
                R.id.nav_donation_history -> {
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                    finish()
                }
                R.id.nav_help -> {
                    startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // ⚡️ Function to fetch events from Firestore with corrected field names ⚡️
    private fun loadEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Order by 'dateCreated' which is the actual timestamp field in your document
                val snapshot = firestore.collection(EVENTS_COLLECTION_PATH)
                    .orderBy("dateCreated", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val fetchedEvents = snapshot.documents.map { document ->
                    NewsletterItem(
                        // ⚡️ FIX: Look for 'name' (document field) ⚡️
                        title = document.getString("name") ?: "N/A",

                        location = document.getString("location") ?: "Online",

                        // ⚡️ FIX: Look for 'dateAndTime' (document field) ⚡️
                        date = document.getString("dateAndTime") ?: "N/A",

                        cost = document.getString("cost") ?: "Free",

                        // ⚡️ FIX: Look for 'about' (document field) ⚡️
                        description = document.getString("about") ?: "No description provided.",

                        imageUrl = document.getString("imageUrl") ?: "",

                        // ⚡️ FIX: Use 'dateCreated' (document field) ⚡️
                        timestamp = document.getDate("dateCreated")
                    )
                }

                withContext(Dispatchers.Main) {
                    newsletterList.clear()
                    newsletterList.addAll(fetchedEvents)
                    adapter.notifyDataSetChanged()
                    // This toast should now reflect the correct count
                    Toast.makeText(this@NewsletterActivity, "Loaded ${newsletterList.size} events.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // This will catch Indexing errors or other network failures
                    Toast.makeText(this@NewsletterActivity, "Failed to load events: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}