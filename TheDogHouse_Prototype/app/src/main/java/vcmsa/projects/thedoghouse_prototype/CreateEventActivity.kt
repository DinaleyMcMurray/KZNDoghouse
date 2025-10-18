package vcmsa.projects.thedoghouse_prototype

import android.app.Activity
// Removed: import android.app.NotificationChannel
// Removed: import android.app.NotificationManager
// Removed: import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
// Removed: import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
// Removed: import androidx.core.app.NotificationCompat
// Removed: import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.util.Date

class CreateEventActivity : AppCompatActivity() {

    // --- Removed Notification Constants ---
    // private val CHANNEL_ID = "event_notifications_channel"
    // private val CHANNEL_NAME = "New Event Alerts"

    // --- Cloudinary Configuration ---
    private val CLOUDINARY_CLOUD_NAME = "dyuieeirb"
    private val CLOUDINARY_UPLOAD_PRESET = "KZNDoghouse"
    private val CLOUDINARY_API_KEY = "959111626652188"
    private val CLOUDINARY_API_SECRET = "MPC45jC70zK656BiiADN-0ULohs"

    // --- UI/Navigation Variables ---
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    // --- Data Variables ---
    private var imageUri: Uri? = null
    private var imageUrl: String? = null
    private var currentEvent: EventData? = null

    // Input Fields
    private lateinit var nameEditText: EditText
    private lateinit var whereEditText: EditText
    private lateinit var whenEditText: EditText
    private lateinit var costEditText: EditText
    private lateinit var aboutEditText: EditText
    private lateinit var rsvpSwitch: Switch

    // Buttons
    private lateinit var uploadImageButton: Button
    private lateinit var cancelButton: Button
    private lateinit var uploadButton: Button

    // Firestore Setup
    private val firestore = FirebaseFirestore.getInstance()
    private val ADMIN_DOC_ID = "AdminUserDocument"


    private val imageChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            if (imageUri != null) {
                // Update button text to reflect selection, indicating ready for upload/save
                uploadImageButton.text = "Image Selected, Ready to Upload"
                Toast.makeText(this, "Image selected, ready to upload on Save.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        // Removed: 1. Initialize Notification Channel (createNotificationChannel())

        // 2. Initialize Cloudinary (Ensure this only runs once globally if possible)
        try {
            val config = mapOf(
                "cloud_name" to CLOUDINARY_CLOUD_NAME,
                "api_key" to CLOUDINARY_API_KEY,
                "api_secret" to CLOUDINARY_API_SECRET
            )
            MediaManager.init(applicationContext, config)
        } catch (e: Exception) {
            Log.e("Cloudinary", "Initialization failed: ${e.message}")
        }

        // 3. Initialize UI/Navigation/Views
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)
        nameEditText = findViewById(R.id.textName)
        whereEditText = findViewById(R.id.textwhere)
        whenEditText = findViewById(R.id.textWhen)
        costEditText = findViewById(R.id.Cost)
        aboutEditText = findViewById(R.id.editTextTextMultiLine)
        rsvpSwitch = findViewById(R.id.textRsvpbool)
        uploadImageButton = findViewById(R.id.button4)
        cancelButton = findViewById(R.id.button1)
        uploadButton = findViewById(R.id.button2)

        setSupportActionBar(toolbar)


        // 4. Handle Edit Mode Logic
        currentEvent = intent.getParcelableExtra("EVENT_TO_EDIT")

        if (currentEvent != null) {
            // EDIT MODE: Populate fields and change text
            toolbar.title = "Edit Event: ${currentEvent!!.name}"
            uploadButton.text = "SAVE"

            nameEditText.setText(currentEvent!!.name)
            whereEditText.setText(currentEvent!!.location)
            whenEditText.setText(currentEvent!!.dateAndTime)
            costEditText.setText(currentEvent!!.cost)
            aboutEditText.setText(currentEvent!!.about)
            rsvpSwitch.isChecked = currentEvent!!.needsRsvp

            // Indicate that an image already exists
            uploadImageButton.text = "Change Image (Current image exists)"
        } else {
            // CREATE MODE: Default state
            toolbar.title = "Create New Event"
            uploadButton.text = "Upload"
        }

        // 5. Set Click Listeners
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> startActivity(Intent(this, DogManagementActivity::class.java))
                R.id.nav_volunteer_management -> startActivity(Intent(this, VolunteerManagementActivity::class.java))
                R.id.nav_events_management -> startActivity(Intent(this, EventsManagementActivity::class.java))
                R.id.nav_adoption_history -> startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                R.id.nav_dogfood -> startActivity(Intent(this, DonationHistoryActivity::class.java))
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.nav_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        uploadButton.setOnClickListener {
            validateAndStartUpload()
        }

        cancelButton.setOnClickListener {
            startActivity(Intent(this, EventsManagementActivity::class.java))
            finish()
        }

        uploadImageButton.setOnClickListener {
            chooseImage()
        }
    }

    // --- Helper Functions (Image Upload, Firestore Save) ---

    // Removed: createNotificationChannel() function

    // Removed: showNotification() function

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageChooserLauncher.launch(intent)
    }

    private fun validateAndStartUpload() {
        // Basic Validation
        if (nameEditText.text.toString().trim().isEmpty() ||
            whereEditText.text.toString().trim().isEmpty() ||
            whenEditText.text.toString().trim().isEmpty() ||
            costEditText.text.toString().trim().isEmpty() ||
            aboutEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all text fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if a NEW image is selected OR if NO image existed before (required for new event)
        if (imageUri == null && currentEvent?.imageUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show()
            return
        }

        uploadButton.isEnabled = false
        uploadImageButton.isEnabled = false

        // Only upload to Cloudinary if a NEW image was selected
        if (imageUri != null) {
            Toast.makeText(this, "Uploading image to Cloudinary...", Toast.LENGTH_LONG).show()
            uploadImageToCloudinary()
        } else {
            // No new image selected, skip Cloudinary and go straight to Firestore save/update
            saveEventToFirestore()
        }
    }

    private fun uploadImageToCloudinary() {
        MediaManager.get().upload(imageUri)
            .option("resource_type", "image")
            .option("folder", "doghouse_app/events")
            .option("upload_preset", CLOUDINARY_UPLOAD_PRESET)

            // Add this option to request Cloudinary to provide the HTTPS link
            .option("secure", true)

            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val secureUrl = resultData["secure_url"] as? String
                    if (secureUrl != null) {
                        imageUrl = secureUrl
                        Log.d(TAG, "Upload successful. Secure URL: $imageUrl")
                    } else {
                        imageUrl = resultData["url"] as? String
                        Log.w(TAG, "Secure URL not found, falling back to non-secure.")
                    }
                    saveEventToFirestore()
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    uploadButton.isEnabled = true
                    uploadImageButton.isEnabled = true
                    Log.e(TAG, "Upload failed: ${error.description}")
                    Toast.makeText(this@CreateEventActivity, "Image upload failed: ${error.description}", Toast.LENGTH_LONG).show()
                }
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
    }

    private fun saveEventToFirestore() {
        val eventName = nameEditText.text.toString().trim()
        val location = whereEditText.text.toString().trim()
        val dateAndTime = whenEditText.text.toString().trim()
        val cost = costEditText.text.toString().trim()
        val about = aboutEditText.text.toString().trim()
        val needsRsvp = rsvpSwitch.isChecked

        // Determine final image URL: (New URL > Old URL > Empty)
        val finalImageUrl = imageUrl ?: currentEvent?.imageUrl ?: ""

        val eventData = mutableMapOf<String, Any>(
            "name" to eventName,
            "location" to location,
            "dateAndTime" to dateAndTime,
            "cost" to cost,
            "about" to about,
            "needsRsvp" to needsRsvp,
            "imageUrl" to finalImageUrl
        )

        // Maintain or set the dateCreated timestamp
        if (currentEvent != null) {
            eventData["dateCreated"] = currentEvent!!.dateCreated ?: Date()
        } else {
            eventData["dateCreated"] = Date()
        }

        // Determine the Firestore operation
        val firestoreRef = firestore.collection("Admin")
            .document(ADMIN_DOC_ID)
            .collection("CreateEvents")

        val task = if (currentEvent != null && currentEvent!!.documentId.isNotEmpty()) {
            // EDIT MODE: Use the existing documentId to update
            firestoreRef.document(currentEvent!!.documentId).update(eventData)
        } else {
            // CREATE MODE: Add a new document
            firestoreRef.add(eventData)
        }

        task.addOnSuccessListener {
            val isEditing = currentEvent != null
            val toastMessage = if (isEditing) "Event '$eventName' updated successfully!" else "Event '$eventName' created successfully!"
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()

            // Removed: Notification call

            clearFields()
            uploadButton.isEnabled = true
            uploadImageButton.isEnabled = true
            uploadImageButton.text = "Upload Image"

            // Navigate back to the management screen after success
            startActivity(Intent(this, EventsManagementActivity::class.java))
            finish()
        }
            .addOnFailureListener { e ->
                Log.e("CreateEventActivity", "Firestore operation failed: ${e.message}", e)
                Toast.makeText(this, "Failed to save event details: ${e.message}", Toast.LENGTH_LONG).show()
                uploadButton.isEnabled = true
                uploadImageButton.isEnabled = true
            }
    }

    private fun clearFields() {
        nameEditText.text.clear()
        whereEditText.text.clear()
        whenEditText.text.clear()
        costEditText.text.clear()
        aboutEditText.text.clear()
        rsvpSwitch.isChecked = false
        imageUri = null
        imageUrl = null
    }
}