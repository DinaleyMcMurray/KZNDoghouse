package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class DogFoodActivity : AppCompatActivity() {

    private lateinit var donorNameEditText: EditText
    private lateinit var dogFoodNameEditText: EditText
    private lateinit var dropOffDateEditText: EditText
    private lateinit var dropOffTimeEditText: EditText
    private lateinit var submitButton: Button

    private lateinit var fundsButton: Button
    private lateinit var dogFoodButton: Button
    private lateinit var medicationButton: Button

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_food)

        donorNameEditText = findViewById(R.id.DonorName)
        dogFoodNameEditText = findViewById(R.id.DogFoodName)
        dropOffDateEditText = findViewById(R.id.editTextDate)
        dropOffTimeEditText = findViewById(R.id.editTextTime2)
        submitButton = findViewById(R.id.button4)

        fundsButton = findViewById(R.id.button1)
        dogFoodButton = findViewById(R.id.button2)
        medicationButton = findViewById(R.id.button3)

        submitButton.setOnClickListener {
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
                    "dropOffTime" to time
                )

                firestore.collection("DogFoodDonations")
                    .add(donationData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Donation submitted!", Toast.LENGTH_LONG).show()
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
            // Already on this screen, maybe show a Toast or do nothing
            Toast.makeText(this, "You are already on Dog Food page", Toast.LENGTH_SHORT).show()
        }

        medicationButton.setOnClickListener {
            val intent = Intent(this, MedsDonationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun clearFields() {
        donorNameEditText.text.clear()
        dogFoodNameEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
    }
}
