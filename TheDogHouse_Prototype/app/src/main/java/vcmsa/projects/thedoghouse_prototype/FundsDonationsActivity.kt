package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

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

        // ⚡️ INITIALIZE DRAWER COMPONENTS ⚡️
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        // Set Toolbar to open drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // ⚡️ END INITIALIZATION ⚡️


        // NAVIGATION BUTTONS (Using the CORRECT IDs from your XML)
        val DogFoodButton = findViewById<Button>(R.id.DogFoodBtn) // Correct ID
        val MedsButton = findViewById<Button>(R.id.MedsBtn)       // Correct ID
        // Note: The FundsBtn is not retrieved here as it is the current page, but it exists in the XML.

        DogFoodButton.setOnClickListener {
            val intent = Intent(this, DogFoodActivity::class.java)
            startActivity(intent)
            finish()
        }

        MedsButton.setOnClickListener {
            val intent = Intent(this, MedsDonationActivity::class.java)
            startActivity(intent)
            finish()
        }

        // FUND DONATION LOGIC
        val etAmount = findViewById<EditText>(R.id.editAmount)
        val btn100 = findViewById<Button>(R.id.buttonR100)
        val btn500 = findViewById<Button>(R.id.buttonR500)
        val btn1000 = findViewById<Button>(R.id.buttonR1000)

        // NEW BUTTON
        val enterAmountButton = findViewById<Button>(R.id.enterAmountBtn)

        val btnPayPal = findViewById<ImageView>(R.id.btnPayPal)
        val btnVisa = findViewById<ImageView>(R.id.btnVisa)
        val btnEft = findViewById<ImageView>(R.id.btnEft)

        val linkPayPal = findViewById<TextView>(R.id.linkPayPal)
        val linkVisa = findViewById<TextView>(R.id.linkVisa)
        val linkEft = findViewById<TextView>(R.id.linkEft)

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
        btnPayPal.setOnClickListener {
            openPayment("https://www.paypal.com/pay?amount=$selectedAmount")
        }
        linkPayPal.setOnClickListener {
            openPayment("https://www.paypal.com/pay?amount=$selectedAmount")
        }

        btnVisa.setOnClickListener {
            openPayment("https://www.visa.com/pay?amount=$selectedAmount")
        }
        linkVisa.setOnClickListener {
            openPayment("https://www.visa.com/pay?amount=$selectedAmount")
        }

        btnEft.setOnClickListener {
            openPayment("https://www.bank.com/eft?amount=$selectedAmount")
        }
        linkEft.setOnClickListener {
            openPayment("https://www.bank.com/eft?amount=$selectedAmount")
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
                R.id.nav_fundsdonation -> {
                    // Current activity, just close the drawer
                }
                R.id.nav_volunteer -> {
                    startActivity(Intent(this, VolunteerActivity::class.java))
                    finish()
                }
                R.id.nav_adoption -> {
                    startActivity(Intent(this, ViewAdoptionActivity::class.java))
                    finish()
                }
                R.id.nav_donation_history -> {
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

    // Open browser with payment link
    private fun openPayment(url: String) {
        val finalAmount = if (selectedAmount.isEmpty()) {
            findViewById<EditText>(R.id.editAmount)?.text?.toString()?.trim() ?: "0"
        } else {
            selectedAmount
        }

        if (finalAmount.toDoubleOrNull() ?: 0.0 <= 0.0) {
            Toast.makeText(this, "Please enter a valid amount before proceeding to payment.", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("amount=$selectedAmount", "amount=$finalAmount")))
        startActivity(intent)
    }

    // Function to save donation to Firestore
    private fun saveFundsDonationToFirestore(amount: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to record your donation.", Toast.LENGTH_LONG).show()
            return
        }

        if (amount.toDoubleOrNull() ?: 0.0 <= 0.0) {
            Toast.makeText(this, "Invalid donation amount.", Toast.LENGTH_SHORT).show()
            return
        }

        val donationData = hashMapOf(
            "amount" to amount,
            "type" to "Funds",
            "dateSubmitted" to Date(),
            "status" to "Pending Payment"
        )

        firestore.collection("Users")
            .document(currentUser.uid)
            .collection("FundsDonations")
            .add(donationData)
            .addOnSuccessListener {
                Toast.makeText(this, "Donation of R$amount recorded. Proceed to payment!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to record donation. Try again.", Toast.LENGTH_SHORT).show()
            }
    }
}