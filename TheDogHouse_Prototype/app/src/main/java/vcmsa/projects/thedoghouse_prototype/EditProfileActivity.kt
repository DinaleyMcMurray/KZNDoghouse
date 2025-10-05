package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com. google. firebase. firestore. DocumentSnapshot



class EditProfileActivity : AppCompatActivity() {
    private var editName: EditText? = null
    private var editEmail: EditText? = null
    private var editPhone: EditText? = null
    private var editAge: EditText? = null
    private var btnSave: Button? = null

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        // Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views
        editName = findViewById<EditText?>(R.id.editTextText)
        editEmail = findViewById<EditText?>(R.id.editTextTextEmailAddress)
        editPhone = findViewById<EditText?>(R.id.editTextPhone)
        editAge = findViewById<EditText?>(R.id.editTextNumber)
        btnSave = findViewById<Button?>(R.id.btn_save_profile)

        // Load current user data
        loadUserData()

        // Save changes
        btnSave!!.setOnClickListener(View.OnClickListener { v: View? -> saveUserData() })

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
                    // Optional: Handle logout
                    startActivity(Intent(this, FundsDonationsActivity::class.java))
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

    private fun loadUserData() {
        val uid = mAuth!!.getCurrentUser()!!.getUid()
        val userRef = db!!.collection("users").document(uid)

        userRef.get()
            .addOnSuccessListener(OnSuccessListener { documentSnapshot: DocumentSnapshot? ->
                if (documentSnapshot!!.exists()) {
                    editName!!.setText(documentSnapshot.getString("name"))
                    editEmail!!.setText(documentSnapshot.getString("email"))
                    editPhone!!.setText(documentSnapshot.getString("phone"))
                    editAge!!.setText(documentSnapshot.getString("age"))
                }
            }).addOnFailureListener(OnFailureListener { e: Exception? ->
                Toast.makeText(
                    this,
                    "Failed to load profile.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            )
    }

    private fun saveUserData() {
        val uid = mAuth!!.getCurrentUser()!!.getUid()

        val userMap: MutableMap<String?, Any?> = HashMap<String?, Any?>()
        userMap.put("name", editName!!.getText().toString())
        userMap.put("email", editEmail!!.getText().toString())
        userMap.put("phone", editPhone!!.getText().toString())
        userMap.put("age", editAge!!.getText().toString())

        db!!.collection("users").document(uid).set(userMap)
            .addOnSuccessListener(OnSuccessListener { aVoid: Void? ->
                Toast.makeText(
                    this@EditProfileActivity,
                    "Profile updated!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            )
            .addOnFailureListener(OnFailureListener { e: Exception? ->
                Toast.makeText(
                    this@EditProfileActivity,
                    "Error saving profile.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            )
    }
}
