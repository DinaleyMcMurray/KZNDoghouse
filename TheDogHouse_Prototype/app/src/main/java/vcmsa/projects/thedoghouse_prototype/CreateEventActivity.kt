package vcmsa.projects.thedoghouse_prototype

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager // Cloudinary Import
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.util.Date
import android.util.Log

class CreateEventActivity : AppCompatActivity() {

    // --- Cloudinary Configuration (REPLACE with your actual credentials) ---
    // NOTE: These must match the values used in AddDogActivity.kt
    private val CLOUDINARY_CLOUD_NAME = "dyuieeirb"
    private val CLOUDINARY_UPLOAD_PRESET = "KZNDoghouse"

    // --- Image Upload Variables ---
    private var imageUri: Uri? = null
    private var imageUrl: String? = null

    // Input Fields
    private lateinit var nameEditText: EditText // R.id.textName
    private lateinit var whereEditText: EditText // R.id.textwhere
    private lateinit var whenEditText: EditText // R.id.textWhen
    private lateinit var costEditText: EditText // R.id.Cost
    private lateinit var aboutEditText: EditText // R.id.editTextTextMultiLine
    private lateinit var rsvpSwitch: Switch // R.id.textRsvpbool

    // Buttons
    private lateinit var uploadImageButton: Button // R.id.button4
    private lateinit var cancelButton: Button // R.id.button1
    private lateinit var uploadButton: Button // R.id.button2 (The main submit button)

    // Firestore Setup
    private val firestore = FirebaseFirestore.getInstance()
    private val ADMIN_DOC_ID = "AdminUserDocument" // Fixed Admin Document ID

    private val imageChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            if (imageUri != null) {
                uploadImageButton.text = "Image Selected!"
                Toast.makeText(this, "Image selected, ready to upload.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event) // Assuming layout is named activity_create_event.xml

        // Initialize Cloudinary (Use applicationContext)
        try {
            val config = mapOf(
                "cloud_name" to CLOUDINARY_CLOUD_NAME
            )
            MediaManager.init(applicationContext, config)
        } catch (e: Exception) {
            Log.e("Cloudinary", "Initialization failed: ${e.message}")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Link Views to Kotlin Variables
        nameEditText = findViewById(R.id.textName)
        whereEditText = findViewById(R.id.textwhere)
        whenEditText = findViewById(R.id.textWhen)
        costEditText = findViewById(R.id.Cost)
        aboutEditText = findViewById(R.id.editTextTextMultiLine)
        rsvpSwitch = findViewById(R.id.textRsvpbool)

        uploadImageButton = findViewById(R.id.button4)
        cancelButton = findViewById(R.id.button1)
        uploadButton = findViewById(R.id.button2)

        // 2. Set Button Click Listeners
        uploadButton.setOnClickListener {
            validateAndStartUpload()
        }

        cancelButton.setOnClickListener {
            // Navigate back to Admin landing page
            startActivity(Intent(this, EventsManagementActivity::class.java)) // Assuming this is your event management screen
            finish()
        }

        uploadImageButton.setOnClickListener {
            chooseImage()
        }
    }

    // --- Step 1: Image Selection ---
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageChooserLauncher.launch(intent)
    }

    // --- Step 2: Validation and Image Upload Start ---
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

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show()
            return
        }

        uploadButton.isEnabled = false
        uploadImageButton.isEnabled = false
        Toast.makeText(this, "Uploading image to Cloudinary...", Toast.LENGTH_LONG).show()

        // Call the Cloudinary upload function
        uploadImageToCloudinary()
    }

    // --- Step 3: Upload Image to Cloudinary ---
    private fun uploadImageToCloudinary() {
        MediaManager.get().upload(imageUri)
            .option("resource_type", "image")
            .option("folder", "doghouse_app/events") // Organizes files specifically for events
            .option("upload_preset", CLOUDINARY_UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("Cloudinary", "Event Upload started: $requestId")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // Progress is handled by Cloudinary internally
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    imageUrl = resultData["url"] as String
                    Log.d("Cloudinary", "Event Upload successful. URL: $imageUrl")
                    // Proceed to save the event details to Firestore
                    saveEventToFirestore()
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    uploadButton.isEnabled = true
                    uploadImageButton.isEnabled = true
                    Log.e("Cloudinary", "Event Upload failed: ${error.description}")
                    Toast.makeText(this@CreateEventActivity, "Image upload failed: ${error.description}", Toast.LENGTH_LONG).show()
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    // Handle reschedule if necessary
                }
            })
            .dispatch() // Start the upload process
    }

    // --- Step 4: Save Data to Firestore ---
    private fun saveEventToFirestore() {
        val eventName = nameEditText.text.toString().trim()
        val location = whereEditText.text.toString().trim()
        val dateAndTime = whenEditText.text.toString().trim()
        val cost = costEditText.text.toString().trim()
        val about = aboutEditText.text.toString().trim()
        val needsRsvp = rsvpSwitch.isChecked

        val finalImageUrl = imageUrl ?: ""

        val eventData = hashMapOf(
            "name" to eventName,
            "location" to location,
            "dateAndTime" to dateAndTime,
            "cost" to cost,
            "about" to about,
            "needsRsvp" to needsRsvp,
            "dateCreated" to Date(),
            "imageUrl" to finalImageUrl
        )

        // Save to the fixed Admin structure: Admin/AdminUserDocument/CreateEvents
        firestore.collection("Admin")
            .document(ADMIN_DOC_ID)
            .collection("CreateEvents")
            .add(eventData)
            .addOnSuccessListener {
                Toast.makeText(this, "Event '$eventName' created successfully!", Toast.LENGTH_LONG).show()
                clearFields()
                uploadButton.isEnabled = true
                uploadImageButton.isEnabled = true
                uploadImageButton.text = "Upload Image"
            }
            .addOnFailureListener { e ->
                Log.e("CreateEventActivity", "Firestore save failed: ${e.message}", e)
                Toast.makeText(this, "Failed to create event details.", Toast.LENGTH_LONG).show()
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