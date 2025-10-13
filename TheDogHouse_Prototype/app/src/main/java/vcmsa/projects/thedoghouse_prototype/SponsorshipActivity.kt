package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SponsorshipActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    // Global properties for UI elements
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    // UI Elements for Sponsorship Form
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etMobile: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnSubmit: Button
    private lateinit var linkPayPal: TextView
    private lateinit var btnPayPalIcon: ImageView

    // PayPal configuration
    private val PAYPAL_RECEIVER_EMAIL = "sb-8d13646818350@business.example.com"

    // Variable to hold the ID of the dog being sponsored (if passed via Intent)
    private var dogIdForSponsorship: String? = null
    // Variable to hold the name of the dog being sponsored (for the record)
    private var dogNameForSponsorship: String = "N/A"


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sponsorship)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // ⚡️ GET DOG ID AND NAME FROM INTENT ⚡️
        // 1. Try the expected key:
        dogIdForSponsorship = intent.getStringExtra("DOG_ID_FOR_ADOPTION")

        // Retrieve the name using the unique key
        dogNameForSponsorship = intent.getStringExtra("DOG_NAME_FOR_SPONSORSHIP") ?: "Unknown Dog"

        // Log and provide feedback if the dog ID is missing
        if (dogIdForSponsorship.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Please select a dog to sponsor first.", Toast.LENGTH_LONG).show()
            Log.e("SponsorshipActivity", "Dog ID is missing from the intent after checking DOG_ID_FOR_ADOPTION.")
        }

        // Get dog name using the expected key
        dogNameForSponsorship = intent.getStringExtra("DOG_NAME_FOR_SPONSORSHIP") ?: "Unknown Dog"

        // Log and provide feedback if the dog ID is missing, regardless of the attempted key
        if (dogIdForSponsorship.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Please select a dog to sponsor first.", Toast.LENGTH_LONG).show()
            // This will show if neither the sponsorship nor the adoption key worked.
            Log.e("SponsorshipActivity", "Dog ID is missing from the intent (Received: $dogIdForSponsorship)")
        }


        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // === INITIALIZE DRAWER AND TOOLBAR ===
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        setupNavigationDrawer()
        // === END INITIALIZATION ===


        // === INITIALIZE FORM UI COMPONENTS ===
        etName = findViewById(R.id.volNameEditText)
        etAge = findViewById(R.id.editTextAge)
        etMobile = findViewById(R.id.editTextVolunteerNumber)
        etAmount = findViewById(R.id.SponsorAmount)
        btnSubmit = findViewById(R.id.button4)

        linkPayPal = findViewById(R.id.linkPayPal)
        btnPayPalIcon = findViewById(R.id.btnPayPal)

        // Ensure payment options are hidden when activity starts
        linkPayPal.visibility = View.GONE
        btnPayPalIcon.visibility = View.GONE


        // === SUBMIT BUTTON LOGIC ===
        btnSubmit.setOnClickListener {
            val name = etName.text.toString().trim()
            val age = etAge.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val amount = etAmount.text.toString().trim()

            if (name.isEmpty() || age.isEmpty() || mobile.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "Please fill in all details.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (amount.toDoubleOrNull() ?: 0.0 <= 0.0) {
                Toast.makeText(this, "Please enter a valid sponsorship amount.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Final check that we have the dog ID before proceeding to Firestore
            if (dogIdForSponsorship.isNullOrEmpty()) {
                Toast.makeText(this, "Cannot save: Dog data is missing. Please select a dog again.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            // 1. Save all details to Firestore
            saveSponsorshipRecordToFirestore(name, age, mobile, amount)

            // 2. Show payment links for the next step
            linkPayPal.visibility = View.VISIBLE
            btnPayPalIcon.visibility = View.VISIBLE

        }

        // === PAYMENT BUTTON LOGIC ===
        linkPayPal.setOnClickListener {
            openPayment(etAmount.text.toString().trim())
        }
        btnPayPalIcon.setOnClickListener {
            openPayment(etAmount.text.toString().trim())
        }
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> startActivity(Intent(this, UserProfileActivity::class.java))
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_newsletter -> startActivity(Intent(this, NewsletterActivity::class.java))
                R.id.nav_fundsdonation -> startActivity(Intent(this, FundsDonationsActivity::class.java))
                R.id.nav_volunteer -> startActivity(Intent(this, VolunteerActivity::class.java))
                R.id.nav_adoption -> startActivity(Intent(this, ViewAdoptionActivity::class.java))
                R.id.nav_help -> startActivity(Intent(this, HelpActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun openPayment(amountText: String) {
        val finalAmount = amountText.toDoubleOrNull() ?: 0.0

        if (finalAmount <= 0.0) {
            Toast.makeText(this, "Amount is required for payment.", Toast.LENGTH_LONG).show()
            return
        }

        val url = Uri.parse("https://www.sandbox.paypal.com/cgi-bin/webscr")
            .buildUpon()
            .appendQueryParameter("cmd", "_donations")
            .appendQueryParameter("business", PAYPAL_RECEIVER_EMAIL)
            .appendQueryParameter("amount", String.format("%.2f", finalAmount))
            .appendQueryParameter("currency_code", "USD")
            .appendQueryParameter("lc", "ZA")
            .appendQueryParameter("bn", "PP-DonationsBF:btn_donate_SM.gif:NonHosted")
            .appendQueryParameter("item_name", "Sponsorship for Dog: $dogNameForSponsorship")
            .build()
            .toString()

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun saveSponsorshipRecordToFirestore(name: String, age: String, mobile: String, amount: String) {
        val currentUser = auth.currentUser
        // This check is already done by the button listener, but remains good practice
        if (currentUser == null || dogIdForSponsorship.isNullOrEmpty()) {
            Toast.makeText(this, "Login or Dog ID missing. Cannot save.", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 1. Fetch the User's Email and explicitly convert age
                val userDoc = firestore.collection("Users").document(currentUser.uid).get().await()
                val userEmail = userDoc.getString("email") ?: currentUser.email ?: "Email N/A"
                val sponsorAgeInt = age.toIntOrNull() ?: 0

                // 2. Prepare the Sponsorship Data
                val sponsorshipData = hashMapOf(
                    // User-submitted details
                    "sponsorName" to name,
                    "sponsorAge" to sponsorAgeInt,
                    "sponsorMobile" to mobile,
                    "amount" to amount,
                    // Dog ID is guaranteed to be non-null/non-empty here due to validation
                    "dogId" to dogIdForSponsorship,
                    "dogName" to dogNameForSponsorship,
                    "userId" to currentUser.uid,
                    "userEmail" to userEmail,
                    "type" to "Sponsorship",
                    "timestamp" to Timestamp.now(),
                    "dateSubmitted" to Timestamp.now()
                )

                // 3. Save the record to the Sponsors subcollection under the User
                firestore.collection("Users")
                    .document(currentUser.uid)
                    .collection("Sponsors")
                    .add(sponsorshipData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@SponsorshipActivity,
                            "Sponsorship details for $dogNameForSponsorship recorded. Please proceed to payment!",
                            Toast.LENGTH_LONG
                        ).show()

                        btnSubmit.text = "DETAILS CONFIRMED"
                        btnSubmit.isEnabled = false

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this@SponsorshipActivity,
                            "Failed to record sponsorship. ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Sponsorship", "Firestore save failed", e)
                    }

            } catch (e: Exception) {
                Toast.makeText(
                    this@SponsorshipActivity,
                    "Error fetching user details: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("Sponsorship", "User lookup failed", e)
            }
        }
    }
}