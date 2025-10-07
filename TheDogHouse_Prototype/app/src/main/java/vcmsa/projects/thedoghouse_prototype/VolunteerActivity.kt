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
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.media3.common.util.UnstableApi // Note: This import is specific to androidx.media3 and may be unneeded
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VolunteerActivity : AppCompatActivity() {

    private lateinit var volName: EditText
    private lateinit var volGender: EditText
    private lateinit var volAge: EditText
    private lateinit var volNumber: EditText
    private lateinit var volEmail: EditText
    private lateinit var submitButton: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
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

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        volName = findViewById(R.id.volNameEditText)
        volGender = findViewById(R.id.editTextGender)
        volAge = findViewById(R.id.editTextAge)
        volNumber = findViewById(R.id.editTextVolunteerNumber)
        volEmail = findViewById(R.id.editTextEmail)
        submitButton = findViewById(R.id.button4)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Not authenticated. Please log in.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        submitButton.setOnClickListener {
            val name = volName.text.toString().trim()
            val gender = volGender.text.toString().trim()
            val age = volAge.text.toString().trim()
            val phone = volNumber.text.toString().trim()
            val email = volEmail.text.toString().trim()

            if (name.isEmpty() || gender.isEmpty() || age.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val volunteerData = hashMapOf(
                    "Name" to name,
                    "Gender" to gender,
                    "Age" to age,
                    "Phone" to phone,
                    "Email" to email,
                    "userId" to currentUser.uid
                )

                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(currentUser.uid)
                    .collection("Volunteer")
                    .add(volunteerData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Volunteer details saved!", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error saving details", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_newsletter -> startActivity(Intent(this, NewsletterActivity::class.java))
                R.id.nav_fundsdonation -> startActivity(Intent(this,MedsDonationActivity::class.java))
                R.id.nav_adoption -> startActivity(Intent(this, ViewAdoptionActivity::class.java))
                R.id.nav_volunteer -> drawerLayout.closeDrawers() // Already here
                R.id.nav_account -> startActivity(Intent(this, UserProfileActivity::class.java))
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_help -> { startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
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