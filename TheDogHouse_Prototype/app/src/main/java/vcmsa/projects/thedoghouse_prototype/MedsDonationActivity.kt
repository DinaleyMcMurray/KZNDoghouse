package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

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

    private lateinit var auth: FirebaseAuth
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

        // Initialize drawer and toolbar
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Correct drawer toggle
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Initialize form fields (Linking XML views)
        donorNameEditText = findViewById(R.id.DonorName)
        medicationNameEditText = findViewById(R.id.MedicationName)
        quantityEditText = findViewById(R.id.Quantity)
        dropOffDateEditText = findViewById(R.id.editTextDate)
        dropOffTimeEditText = findViewById(R.id.editTextTime2)
        submitButton = findViewById(R.id.button4)

        fundsButton = findViewById(R.id.button1)
        dogFoodButton = findViewById(R.id.button2)
        medicationButton = findViewById(R.id.button3)


        // SUBMIT LOGIC (Full, restored logic)
        submitButton.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Please log in to submit a donation.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Get form data
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
                    "timestamp" to Date()
                )

                firestore.collection("Users")
                    .document(currentUser.uid)
                    .collection("MedsDonations")
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
        // END SUBMIT LOGIC


        // NAVIGATION BUTTONS (Corrected in previous steps)
        fundsButton.setOnClickListener {
            startActivity(Intent(this, FundsDonationsActivity::class.java))
            finish()
        }
        dogFoodButton.setOnClickListener {
            startActivity(Intent(this, DogFoodActivity::class.java))
            finish()
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
                R.id.nav_adoption -> startActivity(Intent(this, AdoptionActivity::class.java))
                R.id.nav_donation_history -> startActivity(Intent(this, DonationHistoryActivity::class.java))
                R.id.nav_fundsdonation ->  startActivity(Intent(this, FundsDonationsActivity::class.java))
                R.id.nav_account -> startActivity(Intent(this, EditProfileActivity::class.java))
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
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