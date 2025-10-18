package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminAddVolunteerActivity : AppCompatActivity() {

    // 🔥 Define the central collection path for the Admin to query
    private val ADMIN_VOLUNTEER_PATH = "Admin/AdminUserDocument/Volunteer"
    // 🔥 Use the fixed ID that the VolunteerManagementActivity expects for deletion
    private val ADMIN_FIXED_USER_ID = "AdminUserDocument"

    private lateinit var volName: EditText
    private lateinit var volGender: EditText
    private lateinit var volAge: EditText
    private lateinit var volNumber: EditText
    private lateinit var volEmail: EditText
    private lateinit var submitButton: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_volunteer)

        // 1. Removed: Initialize Notification Channel (createNotificationChannel())

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        // Setup Toolbar Up/Back Navigation
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                // Navigates back to the previous screen (VolunteerManagementActivity)
                finish()
            }
        }

        volName = findViewById(R.id.volNameEditText)
        volGender = findViewById(R.id.editTextGender)
        volAge = findViewById(R.id.editTextAge)
        volNumber = findViewById(R.id.editTextVolunteerNumber)
        volEmail = findViewById(R.id.editTextEmail)
        submitButton = findViewById(R.id.button4)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Not authenticated. Please log in.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        submitButton.setOnClickListener {
            val name = volName.text.toString().trim()
            val gender = volGender.text.toString().trim()
            val age = volAge.text.toString().trim()
            val phone = volNumber.text.toString().trim()
            val email = volEmail.text.toString().trim()

            if (name.isEmpty() || gender.isEmpty() || age.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val volunteerData = hashMapOf(
                    "Name" to name,
                    "Gender" to gender,
                    "Age" to age,
                    "Phone" to phone,
                    "Email" to email,
                    // Store the fixed ID required for management screen deletion logic
                    "userId" to ADMIN_FIXED_USER_ID
                )

                FirebaseFirestore.getInstance()
                    // Store data in the central Admin collection
                    .collection(ADMIN_VOLUNTEER_PATH)
                    .add(volunteerData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Volunteer details saved!", Toast.LENGTH_SHORT).show()

                        // Removed: showApplicationNotification(name)

                        clearFields()

                        // Navigate back to the management screen after success
                        val intent = Intent(this, VolunteerManagementActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error saving details", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_newsletter -> startActivity(Intent(this, NewsletterActivity::class.java))
                R.id.nav_fundsdonation -> startActivity(Intent(this, MedsDonationActivity::class.java))
                R.id.nav_adoption -> startActivity(Intent(this, ViewAdoptionActivity::class.java))
                R.id.nav_volunteer -> drawerLayout.closeDrawers()
                R.id.nav_account -> startActivity(Intent(this, UserProfileActivity::class.java))
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_help -> { startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun clearFields() {
        volName.text.clear()
        volGender.text.clear()
        volAge.text.clear()
        volNumber.text.clear()
        volEmail.text.clear()
    }
}