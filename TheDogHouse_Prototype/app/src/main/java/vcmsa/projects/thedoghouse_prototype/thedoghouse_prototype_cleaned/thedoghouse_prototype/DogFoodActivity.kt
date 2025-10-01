package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class DogFoodActivity : AppCompatActivity() {

    private lateinit var donorNameEditText: EditText
    private lateinit var dogFoodNameEditText: EditText
    private lateinit var dropOffDateEditText: EditText
    private lateinit var dropOffTimeEditText: EditText
    private lateinit var submitButton: Button

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dog_food)

        // Apply safe area padding (status bar, nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_register -> startActivity(Intent(this, RegisterActivity::class.java))
                R.id.nav_newsletter -> startActivity(Intent(this, NewsletterActivity::class.java))
                R.id.nav_meds -> startActivity(Intent(this, MedsDonationActivity::class.java))
                R.id.nav_funds -> startActivity(Intent(this, FundsDonationsActivity::class.java))
                R.id.nav_volunteer -> startActivity(Intent(this, VolunteerActivity::class.java))
                R.id.nav_uploads -> Toast.makeText(this, "Uploads page", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Settings page", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // 🔹 Form Views
        donorNameEditText = findViewById(R.id.DonorName)
        dogFoodNameEditText = findViewById(R.id.DogFoodName)
        dropOffDateEditText = findViewById(R.id.editTextDate)
        dropOffTimeEditText = findViewById(R.id.editTextTime2)
        submitButton = findViewById(R.id.button4)

        submitButton.setOnClickListener { handleSubmit() }
    }

    /**
     * Validates input fields and uploads donation to Firestore
     */
    private fun handleSubmit() {
        val donorName = donorNameEditText.text.toString().trim()
        val dogFoodName = dogFoodNameEditText.text.toString().trim()
        val date = dropOffDateEditText.text.toString().trim()
        val time = dropOffTimeEditText.text.toString().trim()

        if (donorName.isEmpty() || dogFoodName.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "⚠ Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val donationData = hashMapOf(
            "donorName" to donorName,
            "dogFoodName" to dogFoodName,
            "dropOffDate" to date,
            "dropOffTime" to time
        )

        firestore.collection("DogFoodDonations")
            .add(donationData)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Donation submitted!", Toast.LENGTH_LONG).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "❌ Failed to submit. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    /** Clears all input fields */
    private fun clearFields() {
        donorNameEditText.text.clear()
        dogFoodNameEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
    }
}
