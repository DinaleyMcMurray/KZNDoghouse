package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MedsDonationActivity : AppCompatActivity() {

    private lateinit var donorNameEditText: EditText
    private lateinit var medicationNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var dropOffDateEditText: EditText
    private lateinit var dropOffTimeEditText: EditText
    private lateinit var submitButton: Button

    private lateinit var fundsButton: Button
    private lateinit var dogFoodButton: Button
    private lateinit var medicationButton: Button

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meds_donation)

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
                    "dropOffTime" to time
                )

                firestore.collection("MedicationDonations")
                    .add(donationData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Medication donation submitted!", Toast.LENGTH_LONG).show()
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
            Toast.makeText(this, "You are already on Medication page", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        donorNameEditText.text.clear()
        medicationNameEditText.text.clear()
        quantityEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
    }
}
