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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth // ADDED: Import for Firebase Auth
import com.google.firebase.firestore.FirebaseFirestore // ADDED: Import for Firestore
import java.util.Date // ADDED: Import for timestamp

class FundsDonationsActivity : AppCompatActivity() {

    private var selectedAmount: String = ""
    private lateinit var auth: FirebaseAuth // ADDED: Auth instance
    private val firestore = FirebaseFirestore.getInstance() // ADDED: Firestore instance
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var toolbar: MaterialToolbar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_funds_donations)

        // ADDED: Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // NAVIGATION BUTTONS
        val DogFoodButton = findViewById<Button>(R.id.DogFoodBtn)
        val MedsButton = findViewById<Button>(R.id.MedsBtn)

        DogFoodButton.setOnClickListener {
            val intent = Intent(this, DogFoodActivity::class.java)
            startActivity(intent)
        }

        MedsButton.setOnClickListener {
            val intent = Intent(this, MedsDonationActivity::class.java)
            startActivity(intent)
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

        // ==== NEW: Enter Amount Button Listener for Firestore Save ====
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

        // ==== Payment Options (now use selectedAmount which is set either by buttons or enterAmountButton) ====
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
                R.id.nav_medsdonation -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, MedsDonationActivity::class.java))
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

    // Open browser with payment link
    private fun openPayment(url: String) {
        // If the user hasn't pressed the 'Funds' button (enterAmountBtn), use the currently entered text.
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

        // NOTE: We do not save to Firestore here, as the payment might fail.
        // The previous design saves the intent to pay, not the payment confirmation.
    }

    // ADDED: Function to save donation to Firestore
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
            "status" to "Pending Payment" // Important for fund tracking
        )

        // Save to the desired nested structure: Users/{uid}/FundsDonations/{auto_ID}
        firestore.collection("Users")
            .document(currentUser.uid)
            .collection("FundsDonations")
            .add(donationData)
            .addOnSuccessListener {
                Toast.makeText(this, "Donation of R$amount recorded. Proceed to payment!", Toast.LENGTH_LONG).show()
                // Do NOT clear the amount field yet, as the user needs it for the payment links
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to record donation. Try again.", Toast.LENGTH_SHORT).show()
            }
    }
}