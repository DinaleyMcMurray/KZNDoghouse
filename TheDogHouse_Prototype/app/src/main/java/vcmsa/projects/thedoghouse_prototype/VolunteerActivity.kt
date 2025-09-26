package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.UnstableApi // Note: This import is specific to androidx.media3 and may be unneeded
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// Removed unused imports from Ntobeko2: RecyclerView, LinearLayoutManager, VolunteerAdapter, VolunteerRecord

class VolunteerActivity : AppCompatActivity() {

    private lateinit var volName: EditText
    private lateinit var volGender: EditText
    private lateinit var volAge: EditText
    private lateinit var volNumber: EditText
    private lateinit var volEmail: EditText
    private lateinit var submitButton: Button

    // Use firestore consistently for the database instance
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    // @OptIn is related to androidx.media3.common.util.Log, which is likely a misplaced import.
    // Keeping it here only if the specific Log is absolutely necessary, otherwise it should be removed.
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteer)

        // Apply system insets (from Ntobeko2 and HEAD)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

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
            } else {

                val volunteerData = hashMapOf(
                    "Volunteer Name" to volunteerName,
                    "Volunteer Gender" to volunteerGender,
                    "Volunteer Age" to volunteerAge,
                    "Volunteer Phone" to volunteerPhone,
                    "Volunteer Email" to volunteerEmail,
                    "userId" to userId // Include the user ID for reference
                )

                // Save to /users/{userId}/volunteer
                firestore.collection("users").document(userId).collection("volunteer")
                    .add(volunteerData)
                    .addOnSuccessListener { documentReference ->
                        // Changed Log.d source to match Android standard (Assuming androidx.media3.common.util.Log is being used as android.util.Log)
                        Log.d("FIRESTORE", "Volunteer details saved: ${documentReference.id}")
                        Toast.makeText(this, "Volunteer Details saved!", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                    .addOnFailureListener { e ->
                        // Changed Log.w source to match Android standard
                        Log.w("FIRESTORE", "Error saving volunteer details", e)
                        Toast.makeText(this, "Error saving volunteer details", Toast.LENGTH_SHORT).show()
                    }
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