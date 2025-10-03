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

class AddDogActivity : AppCompatActivity() {

    // --- Cloudinary Configuration (REPLACE with your actual credentials) ---
    private val CLOUDINARY_CLOUD_NAME = "dyuieeirb"
    private val CLOUDINARY_UPLOAD_PRESET = "KZNDoghouse"
    private val CLOUDINARY_API_KEY = "959111626652188"
    private val CLOUDINARY_API_SECRET = "MPC45jC70zK656BiiADN-0ULohs"

    // --- Image Upload Variables ---
    private var imageUri: Uri? = null
    private var imageUrl: String? = null

    // Input Fields
    private lateinit var dogNameEditText: EditText
    private lateinit var breedEditText: EditText
    private lateinit var sexEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var vaccinatedSwitch: Switch
    private lateinit var sterilizedSwitch: Switch

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
                uploadImageButton.text = "Image Selected!"
                Toast.makeText(this, "Image selected, ready to upload.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dog)

        // --- CORRECTED/SIMPLIFIED CLOUDINARY INITIALIZATION ---
        // We initialize it directly. The SDK handles the internal check
        // to ensure it is only set up once per application run.
        try {
            val config = mapOf(
                "cloud_name" to CLOUDINARY_CLOUD_NAME,
                "api_key" to CLOUDINARY_API_KEY,
                "api_secret" to CLOUDINARY_API_SECRET
            )
            // Use applicationContext as previously established
            MediaManager.init(applicationContext, config)
        } catch (e: Exception) {
            // Log if initialization fails (e.g., if called twice, though the SDK usually prevents this)
            Log.e("Cloudinary", "Initialization failed: ${e.message}")
        }
        // --------------------------------------------------------

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Link Views
        dogNameEditText = findViewById(R.id.textDogName)
        breedEditText = findViewById(R.id.textBreed)
        sexEditText = findViewById(R.id.Gender)
        ageEditText = findViewById(R.id.Age)
        bioEditText = findViewById(R.id.editTextTextMultiLine)
        vaccinatedSwitch = findViewById(R.id.textVac)
        sterilizedSwitch = findViewById(R.id.textSterilization)

        uploadImageButton = findViewById(R.id.button4)
        cancelButton = findViewById(R.id.button1)
        uploadButton = findViewById(R.id.button2)

        // 2. Set Button Click Listeners
        uploadButton.setOnClickListener {
            validateAndStartUpload()
        }

        cancelButton.setOnClickListener {
            startActivity(Intent(this, DogManagementActivity::class.java))
            finish()
        }

        uploadImageButton.setOnClickListener {
            chooseImage()
        }
    }

    // --- Step 1: Image Selection (Unchanged) ---
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageChooserLauncher.launch(intent)
    }

    // --- Step 2: Validation and Image Upload Start (Modified) ---
    private fun validateAndStartUpload() {
        // Basic Validation
        if (dogNameEditText.text.toString().trim().isEmpty() ||
            breedEditText.text.toString().trim().isEmpty() ||
            sexEditText.text.toString().trim().isEmpty() ||
            ageEditText.text.toString().trim().isEmpty() ||
            bioEditText.text.toString().trim().isEmpty()) {
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
            .option("folder", "doghouse_app/dogs") // Optional: Organizes files
            .option("upload_preset", CLOUDINARY_UPLOAD_PRESET) // Recommended for security
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("Cloudinary", "Upload started: $requestId")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    val progress = (bytes * 100 / totalBytes).toInt()
                    Log.d("Cloudinary", "Progress: $progress%")
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    imageUrl = resultData["secure_url"] as String
                    Log.d("Cloudinary", "Upload successful. URL: $imageUrl")
                    // Proceed to save the dog details to Firestore
                    saveDogToFirestore()
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    uploadButton.isEnabled = true
                    uploadImageButton.isEnabled = true
                    Log.e("Cloudinary", "Upload failed: ${error.description}")
                    Toast.makeText(this@AddDogActivity, "Image upload failed: ${error.description}", Toast.LENGTH_LONG).show()
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    // Handle reschedule if necessary
                }
            })
            .dispatch() // Start the upload process
    }

    // --- Step 4: Save Data (Unchanged Firestore logic) ---
    private fun saveDogToFirestore() {
        val dogName = dogNameEditText.text.toString().trim()
        val breed = breedEditText.text.toString().trim()
        val sex = sexEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()
        val bio = bioEditText.text.toString().trim()

        val finalImageUrl = imageUrl ?: ""

        val dogData = hashMapOf(
            "name" to dogName,
            "breed" to breed,
            "sex" to sex,
            "bio" to bio,
            "age" to age,
            "isVaccinated" to vaccinatedSwitch.isChecked,
            "isSterilized" to sterilizedSwitch.isChecked,
            "status" to "Available for Adoption",
            "dateAdded" to Date(),
            "imageUrl" to finalImageUrl // Store the Cloudinary URL
        )

        // Save to the fixed Admin structure: Admin/AdminUserDocument/AddDog
        firestore.collection("Admin")
            .document(ADMIN_DOC_ID)
            .collection("AddDog")
            .add(dogData)
            .addOnSuccessListener {
                Toast.makeText(this, "Dog '$dogName' added successfully!", Toast.LENGTH_LONG).show()
                clearFields()
                uploadButton.isEnabled = true
                uploadImageButton.isEnabled = true
                uploadImageButton.text = "Upload Image"
            }
            .addOnFailureListener { e ->
                Log.e("AddDogActivity", "Firestore save failed: ${e.message}", e)
                Toast.makeText(this, "Failed to add dog details. Image uploaded to Cloudinary.", Toast.LENGTH_LONG).show()
                uploadButton.isEnabled = true
                uploadImageButton.isEnabled = true
            }
    }

    private fun clearFields() {
        dogNameEditText.text.clear()
        breedEditText.text.clear()
        sexEditText.text.clear()
        bioEditText.text.clear()
        ageEditText.text.clear()
        vaccinatedSwitch.isChecked = false
        sterilizedSwitch.isChecked = false
        imageUri = null
        imageUrl = null
    }
}