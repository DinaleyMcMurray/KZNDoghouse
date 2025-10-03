package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth // Import for Auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date // Used for timestamp, good practice for donations

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meds_donation)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Edge-to-edge padding logic
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Linking XML views
        donorNameEditText = findViewById(R.id.DonorName)
        medicationNameEditText = findViewById(R.id.MedicationName)
        quantityEditText = findViewById(R.id.Quantity)
        dropOffDateEditText = findViewById(R.id.editTextDate)
        dropOffTimeEditText = findViewById(R.id.editTextTime2)
        submitButton = findViewById(R.id.button4)

        fundsButton = findViewById(R.id.button1)
        dogFoodButton = findViewById(R.id.button2)
        medicationButton = findViewById(R.id.button3)

        // Submit data to Firestore
        submitButton.setOnClickListener {
            // Check if user is logged in
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Please log in to submit a donation.", Toast.LENGTH_LONG).show()
                // Optionally redirect to login
                return@setOnClickListener
            }

            val donorName = donorNameEditText.text.toString().trim()
            val medicationName = medicationNameEditText.text.toString().trim()
            val quantity = quantityEditText.text.toString().trim()
            val date = dropOffDateEditText.text.toString().trim()
            val time = dropOffTimeEditText.text.toString().trim()

            if (donorName.isEmpty() || medicationName.isEmpty() || quantity.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val donationData = hashMapOf(
                    "donorName" to donorName,
                    "medicationName" to medicationName,
                    "quantity" to quantity,
                    "dropOffDate" to date,
                    "dropOffTime" to time,
                    "timestamp" to Date() // Saves the time of submission
                )

                // CRITICAL CHANGE: Saving to the 'MedsDonations' subcollection
                // within the current user's document in the 'Users' collection.
                firestore.collection("Users")
                    .document(currentUser.uid)
                    .collection("MedsDonations") // Subcollection under the user document
                    .add(donationData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Medication donation submitted and saved to your history!", Toast.LENGTH_LONG).show()
                        clearFields()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to submit. Try again.", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Navigation buttons
        fundsButton.setOnClickListener {
            val intent = Intent(this, FundsDonationsActivity::class.java)
            startActivity(intent)
        }

        dogFoodButton.setOnClickListener {
            val intent = Intent(this, DogFoodActivity::class.java)
            startActivity(intent)
        }

        medicationButton.setOnClickListener {
            Toast.makeText(this, "You are already on the Medication page", Toast.LENGTH_SHORT).show()
        }

        // Handle nav item clicks
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
                    startActivity(Intent(this, NewsletterActivity::class.java))
                }
                R.id.nav_medsdonation -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.nav_volunteer -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, VolunteerActivity::class.java))
                    finish()
                }
                R.id.nav_adoption -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, AdoptionActivity::class.java))
                    finish()
                }
                R.id.nav_donation_history -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

    }

    // clearFields function
    private fun clearFields() {
        donorNameEditText.text.clear()
        medicationNameEditText.text.clear()
        quantityEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
    }
}