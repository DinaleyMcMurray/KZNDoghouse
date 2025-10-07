package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView // Import TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    // UI components
    private lateinit var editName: EditText
    private lateinit var viewEmail: TextView // CHANGED: Now a TextView
    private lateinit var editPhone: EditText
    private lateinit var editAge: EditText
    // REMOVED: editCurrentPassword
    private lateinit var btnSave: Button
    private lateinit var toolbar: MaterialToolbar

    // Firebase instances
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val USERS_COLLECTION = "Users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize Views
        editName = findViewById(R.id.editTextText)
        viewEmail = findViewById(R.id.profile_email_readonly) // CHANGED ID
        editPhone = findViewById(R.id.editTextPhone)
        editAge = findViewById(R.id.editTextNumber)
        btnSave = findViewById(R.id.btn_save_profile)
        toolbar = findViewById(R.id.toolbar)

        setupToolbar()
        loadUserData()

        // Set up save button listener (no password needed now)
        btnSave.setOnClickListener { saveUserData() }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Edit Profile"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadUserData() {
        val user = mAuth.currentUser
        val uid = user?.uid

        if (uid == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show()
            return
        }

        db.collection(USERS_COLLECTION).document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Load editable fields
                    editName.setText(documentSnapshot.getString("name"))
                    editPhone.setText(documentSnapshot.getString("contactNumber"))
                    editAge.setText(documentSnapshot.getString("age"))

                    // Load read-only email
                    viewEmail.text = documentSnapshot.getString("email")
                } else {
                    Toast.makeText(this, "Profile data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("EditProfile", "Load failed: ${e.message}")
            }
    }

    private fun saveUserData() {
        val user = mAuth.currentUser
        val uid = user?.uid

        if (uid == null) {
            Toast.makeText(this, "Error: Cannot save data, user ID missing.", Toast.LENGTH_LONG).show()
            return
        }

        val newName = editName.text.toString().trim()
        val newPhone = editPhone.text.toString().trim()
        val newAge = editAge.text.toString().trim()

        // Since email is read-only, we pull the original value from the TextView
        val currentEmail = viewEmail.text.toString().trim()

        val userMap = hashMapOf<String, Any>(
            "name" to newName,
            "email" to currentEmail, // Saves the original email value back
            "contactNumber" to newPhone,
            "age" to newAge
        )

        // Directly call Firestore update (safe now, no Auth changes)
        db.collection(USERS_COLLECTION).document(uid).update(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving profile details: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditProfile", "Firestore update failed: ${e.message}")
            }
    }
}