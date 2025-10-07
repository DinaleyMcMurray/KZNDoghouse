package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val signInButton = findViewById<Button>(R.id.signinBtn)
        signInButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Initialize ALL EditText fields
        val nameEditText = findViewById<EditText>(R.id.etRegName)
        val emailEditText = findViewById<EditText>(R.id.etRegEmail)
        val ageEditText = findViewById<EditText>(R.id.etAge) // ⚡️ NEW: Initialize Age field
        val contactEditText = findViewById<EditText>(R.id.etRegContactNumber)
        val passwordEditText = findViewById<EditText>(R.id.etRegPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.etRegConfirmPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim() // ⚡️ NEW: Retrieve age
            val contact = contactEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Update validation to include the new age field
            if (name.isEmpty() || email.isEmpty() || age.isEmpty() || contact.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // 2. Auth successful, now save to Firestore
                        val user = auth.currentUser
                        if (user != null) {
                            // ⚡️ UPDATED: Pass age to the save function ⚡️
                            saveUserToFirestore(user.uid, name, email, age, contact)
                        } else {
                            Toast.makeText(this, "Registration Successful, but user data save failed: UID not found.", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Error: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                        Log.e("RegisterActivity", "Auth Failed: ${authTask.exception?.message}")
                    }
                }
        }
    }

    /**
     * Saves the user's name, email, age, and contact number to the Firestore database
     * in a collection named "Users".
     */
    private fun saveUserToFirestore(uid: String, name: String, email: String, age: String, contact: String) {
        // Create a HashMap to store the user details
        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "contactNumber" to contact,
            "age" to age, // ⚡️ NEW: Save the age field ⚡️
            "isAdmin" to false // Set default role
        )

        // Save the data to the "Users" collection, using the Firebase Auth UID as the document ID
        db.collection("Users").document(uid).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Registration and Data Save Successful!", Toast.LENGTH_SHORT).show()
                // Navigate after both Auth and Firestore operations are complete
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Registration Successful, but User Data Save Failed: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("RegisterActivity", "Firestore Save Failed: ${e.message}")
                // Still navigate, but log the error
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }
}