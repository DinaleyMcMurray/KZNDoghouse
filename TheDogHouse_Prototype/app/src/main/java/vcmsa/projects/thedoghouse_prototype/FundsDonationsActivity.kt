package vcmsa.projects.thedoghouse_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FundsDonationsActivity : AppCompatActivity() {

    private lateinit var donorNameEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var paymentMethodEditText: EditText
    private lateinit var referenceEditText: EditText
    private lateinit var submitButton: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funds_donations)

        // Link XML views
        donorNameEditText = findViewById(R.id.DonorNameFunds)
        amountEditText = findViewById(R.id.DonationAmount)
        paymentMethodEditText = findViewById(R.id.PaymentMethod)
        referenceEditText = findViewById(R.id.ReferenceNumber)
        submitButton = findViewById(R.id.SubmitFundsBtn)

        // Handle submit
        submitButton.setOnClickListener {
            saveFundsDonation()
        }
    }

    private fun saveFundsDonation() {
        val donorName = donorNameEditText.text.toString().trim()
        val amount = amountEditText.text.toString().trim()
        val paymentMethod = paymentMethodEditText.text.toString().trim()
        val reference = referenceEditText.text.toString().trim()

        if (donorName.isEmpty() || amount.isEmpty() || paymentMethod.isEmpty() || reference.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: "anonymous"

        // Data structure for Firestore
        val donationData = hashMapOf(
            "donorName" to donorName,
            "amount" to amount,
            "paymentMethod" to paymentMethod,
            "reference" to reference,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(userId)
            .collection("FundsDonations")
            .add(donationData)
            .addOnSuccessListener {
                Toast.makeText(this, "Funds donation submitted!", Toast.LENGTH_LONG).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        donorNameEditText.text.clear()
        amountEditText.text.clear()
        paymentMethodEditText.text.clear()
        referenceEditText.text.clear()
    }
}
