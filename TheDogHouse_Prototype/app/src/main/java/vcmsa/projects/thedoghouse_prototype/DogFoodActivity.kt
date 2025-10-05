package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
// Removed redundant DataBindingUtil.setContentView import
import com.google.firebase.auth.FirebaseAuth // ADDED: Import for Firebase Auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date // ADDED: Import for timestamp

class DogFoodActivity : AppCompatActivity() {

    private lateinit var donorNameEditText: EditText
    private lateinit var dogFoodNameEditText: EditText
    private lateinit var dropOffDateEditText: EditText
    private lateinit var dropOffTimeEditText: EditText
    private lateinit var submitButton: Button

    private lateinit var fundsButton: Button
    private lateinit var dogFoodButton: Button
    private lateinit var medicationButton: Button

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    private lateinit var auth: FirebaseAuth // ADDED: Authentication instance
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_food)

        // ADDED: Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        donorNameEditText = findViewById(R.id.DonorName)
        dogFoodNameEditText = findViewById(R.id.DogFoodName)
        dropOffDateEditText = findViewById(R.id.editTextDate)
        dropOffTimeEditText = findViewById(R.id.editTextTime2)
        submitButton = findViewById(R.id.button4)

        fundsButton = findViewById(R.id.button1)
        dogFoodButton = findViewById(R.id.button2)
        medicationButton = findViewById(R.id.button3)

        submitButton.setOnClickListener {
            // ADDED: Get current user and check authentication
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Please log in to submit a donation.", Toast.LENGTH_LONG).show()
                // You might want to navigate to LoginActivity here
                return@setOnClickListener
            }

            val donorName = donorNameEditText.text.toString().trim()
            val dogFoodName = dogFoodNameEditText.text.toString().trim()
            val date = dropOffDateEditText.text.toString().trim()
            val time = dropOffTimeEditText.text.toString().trim()

            if (donorName.isEmpty() || dogFoodName.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val donationData = hashMapOf(
                    "donorName" to donorName,
                    "dogFoodName" to dogFoodName,
                    "dropOffDate" to date,
                    "dropOffTime" to time,
                    "timestamp" to Date() // Added timestamp for historical tracking
                )

                // CRITICAL CHANGE: Saving to the 'DogFoodDonations' subcollection
                // under the current user's document in the 'Users' collection.
                firestore.collection("Users")
                    .document(currentUser.uid)
                    .collection("DogFoodDonations") // Subcollection under the user document
                    .add(donationData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Dog food donation submitted and saved to your history!", Toast.LENGTH_LONG).show()
                        clearFields()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to submit. Try again.", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        fundsButton.setOnClickListener {
            val intent = Intent(this, FundsDonationsActivity::class.java)
            startActivity(intent)
        }

        dogFoodButton.setOnClickListener {
            Toast.makeText(this, "You are already on Dog Food page", Toast.LENGTH_SHORT).show()
        }

        medicationButton.setOnClickListener {
            val intent = Intent(this, MedsDonationActivity::class.java)
            startActivity(intent)
        }

        // Hook up toolbar icon to open drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

//        findViewById<Button>(R.id.AddDogBtn).setOnClickListener {
//            startActivity(Intent(this, AddDogActivity::class.java))
//        }

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

    private fun clearFields() {
        donorNameEditText.text.clear()
        dogFoodNameEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
    }
}