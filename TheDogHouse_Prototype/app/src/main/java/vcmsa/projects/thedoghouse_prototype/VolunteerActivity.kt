package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest

class VolunteerActivity : AppCompatActivity() {

    // --- Notification Constants ---
    private val CHANNEL_ID = "volunteer_application_channel"
    private val CHANNEL_NAME = "Volunteer Application Status"

    private lateinit var volName: EditText
    private lateinit var volGender: EditText
    private lateinit var volAge: EditText
    private lateinit var volNumber: EditText
    private lateinit var volEmail: EditText
    private lateinit var submitButton: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    // 🔥 NEW: Storage for data submitted before permission was granted
    private var pendingVolunteerData: VolunteerData? = null

    // Helper data class to temporarily hold the form data
    data class VolunteerData(
        val name: String,
        val gender: String,
        val age: String,
        val phone: String,
        val email: String,
        val userId: String
    )


    // 🔥 NEW: Activity Result Launcher modified to auto-submit the application
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted. Processing application...", Toast.LENGTH_SHORT).show()
                // Process the application automatically using the pending data
                pendingVolunteerData?.let { data ->
                    saveAndNotifyVolunteer(data.userId, data.name, data.gender, data.age, data.phone, data.email)
                }
                // Clear the pending data
                pendingVolunteerData = null
            } else {
                Toast.makeText(this, "Notification permission denied. Application saved but confirmation alert skipped.", Toast.LENGTH_LONG).show()
                // If permission is denied, still process the application but skip the notification
                pendingVolunteerData?.let { data ->
                    saveVolunteerOnly(data.userId, data.name, data.gender, data.age, data.phone, data.email)
                }
                pendingVolunteerData = null
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteer)

        // 1. Initialize Notification Channel
        createNotificationChannel()

        // No need to request on startup, we request when they click submit.

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
            val userId = currentUser.uid

            if (name.isEmpty() || gender.isEmpty() || age.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // 1. Package the current data
                val currentData = VolunteerData(name, gender, age, phone, email, userId)

                if (isNotificationPermissionGranted()) {
                    // 2. Permission is granted, save and notify immediately
                    saveAndNotifyVolunteer(userId, name, gender, age, phone, email)
                } else {
                    // 3. Permission is NOT granted, store data and request permission
                    pendingVolunteerData = currentData
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    // The result launcher will handle the rest.
                }
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_newsletter -> startActivity(Intent(this, NewsletterActivity::class.java))
                R.id.nav_fundsdonation -> startActivity(Intent(this, MedsDonationActivity::class.java))
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

    // --- PERMISSION AND SAVE HELPER FUNCTIONS ---

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Permission is not required for devices below Android 13
            true
        }
    }

    /**
     * Saves volunteer data to Firestore and shows the notification.
     */
    private fun saveAndNotifyVolunteer(userId: String, name: String, gender: String, age: String, phone: String, email: String) {
        val volunteerData = hashMapOf(
            "Name" to name,
            "Gender" to gender,
            "Age" to age,
            "Phone" to phone,
            "Email" to email,
            "userId" to userId
        )

        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(userId)
            .collection("Volunteer")
            .add(volunteerData)
            .addOnSuccessListener {
                Toast.makeText(this, "Volunteer details saved!", Toast.LENGTH_SHORT).show()

                // CALL THE NOTIFICATION FUNCTION HERE
                showApplicationNotification(name)

                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error saving details", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Saves volunteer data to Firestore but skips the notification (used if permission is denied).
     */
    private fun saveVolunteerOnly(userId: String, name: String, gender: String, age: String, phone: String, email: String) {
        val volunteerData = hashMapOf(
            "Name" to name,
            "Gender" to gender,
            "Age" to age,
            "Phone" to phone,
            "Email" to email,
            "userId" to userId
        )

        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(userId)
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


    // --- NOTIFICATION HELPER FUNCTIONS ---

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Alerts for the status of volunteer applications."
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showApplicationNotification(volunteerName: String) {
        // Since this function is only called after permission is verified, we can proceed.

        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Application Successful!"
        val message = "Thank you, $volunteerName! Your volunteer application has been submitted. The KZN Doghouse will contact you soon."

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.bonebutton)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // Re-check for Tiramisu+ safety, although the external check handles the user prompt
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return@with
                }
            }

            // Generate a unique ID for the notification
            notify(System.currentTimeMillis().toInt(), builder.build())
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