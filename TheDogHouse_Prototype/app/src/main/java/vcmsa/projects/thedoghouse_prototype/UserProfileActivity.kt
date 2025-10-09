package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth // Import Firebase Auth
import com.google.firebase.firestore.FirebaseFirestore // Import Firestore

class UserProfileActivity : AppCompatActivity() {

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val USERS_COLLECTION = "Users" // Collection name where user data is saved

    // UI components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navigationView: NavigationView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var editButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 1. Initialize UI components from XML
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)
        nameTextView = findViewById(R.id.profile_name)
        emailTextView = findViewById(R.id.profile_email)
        phoneTextView = findViewById(R.id.profile_phone)
        ageTextView = findViewById(R.id.profile_age)
        editButton = findViewById(R.id.btn_edit_profile)

        // Setup Toolbar and Navigation Drawer
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        setupNavigationDrawer()

        // 2. Load the user's data from Firestore
        loadUserProfile()

        // 3. Setup Edit Button Listener
        editButton.setOnClickListener {
            // Navigate to the EditProfileActivity
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid // Get the ID of the currently logged-in user

        if (userId == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show()
            Log.e("UserProfile", "User UID is null.")
            // Redirect to login if needed
            return
        }

        // Fetch the user document from Firestore
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Pull the fields saved during registration and display them
                    val name = document.getString("name") ?: "N/A"
                    val email = document.getString("email") ?: "N/A"
                    val contactNumber = document.getString("contactNumber") ?: "N/A"
                    val age = document.getString("age") ?: "N/A" // Should now exist

                    nameTextView.text = name.uppercase()
                    emailTextView.text = email
                    phoneTextView.text = contactNumber
                    ageTextView.text = age

                    Log.d("UserProfile", "Profile data loaded successfully for $name")
                } else {
                    Toast.makeText(this, "Profile data document not found.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load profile data: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("UserProfile", "Error loading profile data: ${e.message}")
            }
    }

    // Placeholder for your actual navigation logic
    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_newsletter -> {
                    startActivity(Intent(this, NewsletterActivity::class.java))
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
                R.id.nav_help -> {
                    startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}