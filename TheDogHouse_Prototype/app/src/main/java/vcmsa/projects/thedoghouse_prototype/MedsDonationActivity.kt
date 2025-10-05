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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meds_donation)

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

        // RecyclerView or other setup can go here if needed

        // Submit logic (unchanged)
        submitButton.setOnClickListener {
            // Your Firebase submission logic...
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
                R.id.nav_donation_history -> startActivity(Intent(this, DonationHistoryActivity::class.java))
                R.id.nav_fundsdonation -> drawerLayout.closeDrawers() // Already on this page
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

    // clearFields function
    private fun clearFields() {
        donorNameEditText.text.clear()
        medicationNameEditText.text.clear()
        quantityEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
    }
}