package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView // Import TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    // UI components
    private lateinit var editName: EditText
    private lateinit var viewEmail: TextView // CHANGED: Now a TextView
    private lateinit var editPhone: EditText
    private lateinit var editAge: EditText
    // REMOVED: editCurrentPassword
    private lateinit var btnSave: Button
    private lateinit var BackBtn: Button

    // Firebase instances
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val USERS_COLLECTION = "Users"

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    // 🔥 FIX 1: Sandbox Business Email (Facilitator) for receiving test payments 🔥
    // This is the email of the NPO's test account, which receives the funds.
    private val PAYPAL_SANDBOX_RECEIVER_EMAIL = "sb-8d13646818350@business.example.com"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editprofile)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Correct drawer toggle
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize Views
        editName = findViewById(R.id.editTextText)
        viewEmail = findViewById(R.id.profile_email_readonly) // CHANGED ID
        editPhone = findViewById(R.id.editTextPhone)
        editAge = findViewById(R.id.editTextNumber)
        btnSave = findViewById(R.id.btn_save_profile)
        toolbar = findViewById(R.id.toolbar)
        BackBtn = findViewById(R.id.btn_back)

        setupToolbar()
        loadUserData()

        // Set up save button listener (no password needed now)
        btnSave.setOnClickListener {
            saveUserData()
        }

        BackBtn.setOnClickListener {
            startActivity (Intent(this, UserProfileActivity::class.java))
        }

        // Handle nav item clicks
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
                    // Optional: Handle logout
                    startActivity(Intent(this, FundsDonationsActivity::class.java))
                    finish()
                }
                R.id.nav_volunteer -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, VolunteerActivity::class.java))
                    finish()
                }
                R.id.nav_adoption -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, ViewAdoptionActivity::class.java))
                    finish()
                }
                R.id.nav_help -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Edit Profile"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadUserData() {
        val user = mAuth.currentUser
        val uid = user?.uid

        if (uid == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show()
            return
        }

        db.collection(USERS_COLLECTION).document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Load editable fields
                    editName.setText(documentSnapshot.getString("name"))
                    editPhone.setText(documentSnapshot.getString("contactNumber"))
                    editAge.setText(documentSnapshot.getString("age"))

                    // Load read-only email
                    viewEmail.text = documentSnapshot.getString("email")
                } else {
                    Toast.makeText(this, "Profile data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("EditProfile", "Load failed: ${e.message}")
            }
    }

    private fun saveUserData() {
        val user = mAuth.currentUser
        val uid = user?.uid

        if (uid == null) {
            Toast.makeText(this, "Error: Cannot save data, user ID missing.", Toast.LENGTH_LONG).show()
            return
        }

        val newName = editName.text.toString().trim()
        val newPhone = editPhone.text.toString().trim()
        val newAge = editAge.text.toString().trim()

        // Since email is read-only, we pull the original value from the TextView
        val currentEmail = viewEmail.text.toString().trim()

        val userMap = hashMapOf<String, Any>(
            "name" to newName,
            "email" to currentEmail, // Saves the original email value back
            "contactNumber" to newPhone,
            "age" to newAge
        )

        // Directly call Firestore update (safe now, no Auth changes)
        db.collection(USERS_COLLECTION).document(uid).update(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving profile details: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditProfile", "Firestore update failed: ${e.message}")
            }
    }
}