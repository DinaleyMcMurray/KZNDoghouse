package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.Timestamp // Import Firebase Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Import .await() extension

class FundsDonationsActivity : AppCompatActivity() {

    private var selectedAmount: String = ""
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    // Global properties for Navigation Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_funds_donations)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // === INITIALIZE DRAWER COMPONENTS ===
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        // Set Toolbar to open drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // === END INITIALIZATION ===


        // NAVIGATION BUTTONS
        val DogFoodButton = findViewById<Button>(R.id.DogFoodBtn)
        val MedsButton = findViewById<Button>(R.id.MedsBtn)

        DogFoodButton.setOnClickListener {
            startActivity(Intent(this, DogFoodActivity::class.java))
            finish()
        }

        MedsButton.setOnClickListener {
            startActivity(Intent(this, MedsDonationActivity::class.java))
            finish()
        }

        // FUND DONATION LOGIC
        val etAmount = findViewById<EditText>(R.id.editAmount)
        val btn100 = findViewById<Button>(R.id.buttonR100)
        val btn500 = findViewById<Button>(R.id.buttonR500)
        val btn1000 = findViewById<Button>(R.id.buttonR1000)
        val enterAmountButton = findViewById<Button>(R.id.enterAmountBtn)

        val btnPayPal = findViewById<ImageView>(R.id.btnPayPal)

        val linkPayPal = findViewById<TextView>(R.id.linkPayPal)

        // ==== Amount Selection ====
        btn100.setOnClickListener {
            etAmount.setText("100")
            selectedAmount = "100"
        }

        btn500.setOnClickListener {
            etAmount.setText("500")
            selectedAmount = "500"
        }

        btn1000.setOnClickListener {
            etAmount.setText("1000")
            selectedAmount = "1000"
        }

        // ==== Enter Amount Button Listener for Firestore Save ====
        enterAmountButton.setOnClickListener {
            val amountText = etAmount.text.toString().trim()
            if (amountText.isEmpty()) {
                Toast.makeText(this, "Please enter an amount or select a button.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Set the selected amount to whatever is in the EditText
            selectedAmount = amountText

            // Validate and Save to Firestore
            saveFundsDonationToFirestore(selectedAmount)
        }

        // ==== Payment Options ====
        // NOTE: These listeners should ensure the final amount is passed correctly
        btnPayPal.setOnClickListener {
            openPayment("https://www.paypal.com/pay")
        }
        linkPayPal.setOnClickListener {
            openPayment("https://www.paypal.com/pay")
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
                    // Current activity
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

    // Open browser with payment link
    private fun openPayment(baseUrl: String) {
        val finalAmount = findViewById<EditText>(R.id.editAmount)?.text?.toString()?.trim() ?: "0"

        if (finalAmount.toDoubleOrNull() ?: 0.0 <= 0.0) {
            Toast.makeText(this, "Please enter a valid amount before proceeding to payment.", Toast.LENGTH_LONG).show()
            return
        }

        // Correctly append amount to the URL
        val url = "$baseUrl?amount=$finalAmount"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    /**
     * Fetches the user's name first, then saves the full donation record to Firestore.
     */
    private fun saveFundsDonationToFirestore(amount: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to record your donation.", Toast.LENGTH_LONG).show()
            return
        }

        // Use CoroutineScope to handle the asynchronous Firestore lookups
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 1. Fetch the Donor's Name
                // .await() makes this asynchronous call look synchronous
                val userDoc = firestore.collection("Users").document(currentUser.uid).get().await()

                // Assuming the donor's name is stored in a field called "name" in the User document
                val donorName = userDoc.getString("name") ?: "Anonymous Donor"

                // 2. Prepare the Donation Data
                val donationData = hashMapOf(
                    "amount" to amount,
                    "type" to "Funds",
                    "status" to "Pending Payment",

                    // ⚡️ CRITICAL ADDITIONS for History View ⚡️
                    "donorName" to donorName, // Name is now included!
                    "userId" to currentUser.uid,
                    "timestamp" to Timestamp.now(),
                    "dateSubmitted" to Timestamp.now() // Use Timestamp for queryable dates
                )

                // 3. Save the record to the FundsDonations subcollection
                firestore.collection("Users")
                    .document(currentUser.uid)
                    .collection("FundsDonations")
                    .add(donationData)
                    .addOnSuccessListener {
                        Toast.makeText(this@FundsDonationsActivity, "Donation of R$amount recorded. Proceed to payment!", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@FundsDonationsActivity, "Failed to record donation. ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("FundsDonation", "Firestore save failed", e)
                    }

            } catch (e: Exception) {
                // Catches errors during userDoc.get().await() if network fails or user doc not found
                Toast.makeText(this@FundsDonationsActivity, "Error fetching user details: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("FundsDonation", "User lookup failed", e)
            }
        }
    }
}