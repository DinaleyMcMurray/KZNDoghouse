package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.HashMap // Used for creating the data map

class MedsDonationActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var donorNameEditText: EditText
    private lateinit var medicationNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var dropOffDateEditText: EditText
    private lateinit var dropOffTimeEditText: EditText
    private lateinit var submitButton: Button

    // Navigation buttons
    private lateinit var fundsButton: Button
    private lateinit var dogFoodButton: Button
    private lateinit var medicationButton: Button

    private lateinit var auth: FirebaseAuth // Authentication instance
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "MedsDonation" // Tag for logging

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meds_donation)

        // Initialize Auth
        auth = FirebaseAuth.getInstance()

        // Initialize drawer and toolbar
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Correct drawer toggle
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Initialize form fields
        donorNameEditText = findViewById(R.id.DonorName)
        medicationNameEditText = findViewById(R.id.MedicationName)
        quantityEditText = findViewById(R.id.Quantity)
        dropOffDateEditText = findViewById(R.id.editTextDate)
        dropOffTimeEditText = findViewById(R.id.editTextTime2)
        submitButton = findViewById(R.id.button4)

        fundsButton = findViewById(R.id.button1)
        dogFoodButton = findViewById(R.id.button2)
        medicationButton = findViewById(R.id.button3)

        // ⚡️ Submit logic (CALLS THE NEW FUNCTION) ⚡️
        submitButton.setOnClickListener {
            saveMedsDonation()
        }

        // Navigation buttons
        fundsButton.setOnClickListener {
            startActivity(Intent(this, FundsDonationsActivity::class.java))
        }
        dogFoodButton.setOnClickListener {
            startActivity(Intent(this, DogFoodActivity::class.java))
        }
        medicationButton.setOnClickListener {
            Toast.makeText(this, "You are already on the Medication page", Toast.LENGTH_SHORT).show()
        }

        // NAV DRAWER CLICK HANDLER
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_newsletter -> startActivity(Intent(this, NewsletterActivity::class.java))
                R.id.nav_volunteer -> startActivity(Intent(this, VolunteerActivity::class.java))
                R.id.nav_adoption -> startActivity(Intent(this, ViewAdoptionActivity::class.java))
                R.id.nav_fundsdonation -> drawerLayout.closeDrawers() // Close drawer or navigate if needed
                R.id.nav_account -> startActivity(Intent(this, EditProfileActivity::class.java))
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_help -> {startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // ----------------------------------------------------------------------
    // --- FIREBASE SAVING FUNCTION -------------------------------------------
    // ----------------------------------------------------------------------

    private fun saveMedsDonation() {
        val donorName = donorNameEditText.text.toString().trim()
        val medicationName = medicationNameEditText.text.toString().trim()
        val quantity = quantityEditText.text.toString().trim()
        val dropOffDate = dropOffDateEditText.text.toString().trim()
        val dropOffTime = dropOffTimeEditText.text.toString().trim()
        val userId = auth.currentUser?.uid // Get the logged-in user's ID

        if (donorName.isEmpty() || medicationName.isEmpty() || quantity.isEmpty() || dropOffDate.isEmpty() || dropOffTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all donation fields.", Toast.LENGTH_LONG).show()
            return
        }

        // Ensure user is logged in
        if (userId == null) {
            Toast.makeText(this, "User not authenticated. Please log in.", Toast.LENGTH_LONG).show()
            return
        }

        // Create the donation map
        val donationData = hashMapOf(
            "donorName" to donorName,
            "medicationName" to medicationName,
            "quantity" to quantity,
            "dropOffDate" to dropOffDate,
            "dropOffTime" to dropOffTime,
            "timestamp" to Date(), // Firestore automatically converts Date to Timestamp
            "userId" to userId // IMPORTANT for collectionGroup queries used in history
        )

        // Save to Firestore in a subcollection under the User's document
        // Path: /Users/{userId}/MedsDonations/{documentId}
        firestore.collection("Users")
            .document(userId)
            .collection("MedsDonations")
            .add(donationData)
            .addOnSuccessListener {
                Log.d(TAG, "Meds Donation successfully saved with ID: ${it.id}")
                Toast.makeText(this, "Medication Donation submitted successfully!", Toast.LENGTH_LONG).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding Meds Donation: ", e)
                Toast.makeText(this, "Error submitting donation: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun clearFields() {
        donorNameEditText.text.clear()
        medicationNameEditText.text.clear()
        quantityEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
    }
}