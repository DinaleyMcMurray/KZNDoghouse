package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VolunteerActivity : AppCompatActivity() {

    private lateinit var volName: EditText
    private lateinit var volGender: EditText
    private lateinit var volAge: EditText
    private lateinit var volNumber: EditText
    private lateinit var volEmail: EditText
    private lateinit var submitButton: Button

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteer)

//        FirebaseApp.initializeApp(this)
//        db = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Not authenticated. Please log in again.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

     val userId = currentUser.uid



        // Initialize views
        volName = findViewById(R.id.volNameEditText)
        volGender = findViewById(R.id.editTextGender)
        volAge = findViewById(R.id.editTextAge)
        volNumber = findViewById(R.id.editTextVolunteerNumber)
        volEmail = findViewById(R.id.editTextEmail)
        submitButton = findViewById(R.id.button4)

        submitButton.setOnClickListener {
            val volunteerName = volName.text.toString().trim()
            val volunteerGender = volGender.text.toString().trim()
            val volunteerAge = volAge.text.toString().trim()
            val volunteerPhone = volNumber.text.toString().trim()
            val volunteerEmail = volEmail.text.toString().trim()

            if (volunteerName.isEmpty() || volunteerGender.isEmpty() || volunteerAge.isEmpty()
                || volunteerPhone.isEmpty() || volunteerEmail.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
//            else {
//                val volunteerData = hashMapOf(
//                    "Volunteer Name" to volunteerName,
//                    "Volunteer Gender" to volunteerGender,
//                    "Volunteer Age" to volunteerAge,
//                    "Volunteer Phone" to volunteerPhone,
//                    "Volunteer Email" to volunteerEmail
//                )
//
//                firestore.collection("Volunteers")
//                    .add(volunteerData)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Volunteer Details submitted!", Toast.LENGTH_LONG).show()
//                        clearFields()
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(this, "Failed to submit. Try again.", Toast.LENGTH_SHORT).show()
//                    }
//            }

            val volunteerData = hashMapOf(
//              "debtcategoryname" to debtCategoryName
                "Volunteer Name" to volunteerName,
                "Volunteer Gender" to volunteerGender,
                "Volunteer Age" to volunteerAge,
                "Volunteer Phone" to volunteerPhone,
                "Volunteer Email" to volunteerEmail
            )

            // Save to /users/{userId}/volunteer
            db.collection("users").document(userId).collection("volunteer")
                .add(volunteerData)
                .addOnSuccessListener { documentReference ->
                    Log.d("FIRESTORE", "Volunteer details saved: ${documentReference.id}")
                    Toast.makeText(this, "Volunteer Details saved!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
                .addOnFailureListener { e ->
                    Log.w("FIRESTORE", "Error saving volunteer details", e)
                    Toast.makeText(this, "Error saving volunteer details", Toast.LENGTH_SHORT).show()
                }
        }

    }

    // Function should be outside onCreate
    private fun clearFields() {
        volName.text.clear()
        volGender.text.clear()
        volAge.text.clear()
        volNumber.text.clear()
        volEmail.text.clear()
    }
}
