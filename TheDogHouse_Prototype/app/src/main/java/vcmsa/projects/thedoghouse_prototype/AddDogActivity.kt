package vcmsa.projects.thedoghouse_prototype

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

// Assuming DogDataRecord is imported and is Parcelable
// import DogDataRecord

class AddDogActivity : AppCompatActivity() {

    // --- Configuration & Setup ---
    private val CLOUDINARY_CLOUD_NAME = "dyuieeirb"
    private val CLOUDINARY_UPLOAD_PRESET = "KZNDoghouse"
    private val CLOUDINARY_API_KEY = "959111626652188"
    private val CLOUDINARY_API_SECRET = "MPC45jC70zK656BiiADN-0ULohs"

    private val firestore = FirebaseFirestore.getInstance()
    private val ADMIN_DOC_ID = "AdminUserDocument"
    private val DOGS_COLLECTION_PATH = "Admin/$ADMIN_DOC_ID/AddDog"
    private val TAG = "AddDogActivity"

    // --- Data Variables ---
    private var currentDog: DogDataRecord? = null
    private var imageUri: Uri? = null
    private var imageUrl: String? = null

    // --- UI/Input Fields ---
    private lateinit var dogNameEditText: EditText
    private lateinit var breedEditText: EditText
    private lateinit var sexEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var vaccinatedSwitch: Switch
    private lateinit var sterilizedSwitch: Switch
    private lateinit var titleTextView: TextView

    // Buttons
    private lateinit var uploadImageButton: Button
    private lateinit var cancelButton: Button
    private lateinit var uploadButton: Button

    // --- Image Chooser Launcher ---
    private val imageChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            if (imageUri != null) {
                uploadImageButton.text = "Image Selected, Ready to Save!"
                Toast.makeText(this, "Image selected, ready to upload on Save.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dog)

        // --- Cloudinary Initialization ---
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

        // --- View Initialization ---
        dogNameEditText = findViewById(R.id.textDogName)
        breedEditText = findViewById(R.id.textBreed)
        sexEditText = findViewById(R.id.Gender)
        ageEditText = findViewById(R.id.Age)
        bioEditText = findViewById(R.id.editTextTextMultiLine)
        vaccinatedSwitch = findViewById(R.id.textVac)
        sterilizedSwitch = findViewById(R.id.textSterilization)
        titleTextView = findViewById(R.id.heading) // Using your specified ID

        uploadImageButton = findViewById(R.id.button4)
        cancelButton = findViewById(R.id.button1)
        uploadButton = findViewById(R.id.button2)

        // 1. Handle Edit Mode Logic
        currentDog = intent.getParcelableExtra("DOG_TO_EDIT")

        if (currentDog != null) {
            // EDIT MODE: Populate fields
            titleTextView.text = "Edit Dog: ${currentDog!!.name}"
            uploadButton.text = "Save Changes"

            dogNameEditText.setText(currentDog!!.name)
            breedEditText.setText(currentDog!!.breed)
            sexEditText.setText(currentDog!!.sex)
            ageEditText.setText(currentDog!!.age.toString())
            bioEditText.setText(currentDog!!.bio)
            vaccinatedSwitch.isChecked = currentDog!!.isVaccinated // Populate switch state
            sterilizedSwitch.isChecked = currentDog!!.isSterilized // Populate switch state

            uploadImageButton.text = "Change Image (Current image exists)"
        } else {
            // CREATE MODE
            titleTextView.text = "Add New Dog"
            uploadButton.text = "Upload"
        }

        // 2. Set Button Click Listeners
        uploadButton.setOnClickListener { validateAndStartUpload() }
        cancelButton.setOnClickListener {
            startActivity(Intent(this, DogManagementActivity::class.java))
            finish()
        }
        uploadImageButton.setOnClickListener { chooseImage() }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageChooserLauncher.launch(intent)
    }

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

        // Age validation
        if (ageEditText.text.toString().trim().toIntOrNull() == null) {
            Toast.makeText(this, "Please enter a valid number for age.", Toast.LENGTH_SHORT).show()
            return
        }

        val needsNewUpload = imageUri != null
        val hasExistingImage = !currentDog?.imageUrl.isNullOrEmpty()

        if (!needsNewUpload && !hasExistingImage && currentDog == null) {
            Toast.makeText(this, "Please select an image for this new dog.", Toast.LENGTH_SHORT).show()
            return
        }

        uploadButton.isEnabled = false
        uploadImageButton.isEnabled = false

        if (needsNewUpload) {
            Toast.makeText(this, "Uploading image to Cloudinary...", Toast.LENGTH_LONG).show()
            uploadImageToCloudinary() // Calls saveDogToFirestore on success
        } else {
            saveDogToFirestore() // Saves directly (Edit mode with no new image)
        }
    }

    private fun uploadImageToCloudinary() {
        MediaManager.get().upload(imageUri)
            .option("resource_type", "image")
            .option("folder", "doghouse_app/dogs")
            .option("upload_preset", CLOUDINARY_UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    imageUrl = resultData["secure_url"] as String
                    saveDogToFirestore()
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    uploadButton.isEnabled = true
                    uploadImageButton.isEnabled = true
                    Log.e(TAG, "Upload failed: ${error.description}")
                    Toast.makeText(this@AddDogActivity, "Image upload failed: ${error.description}", Toast.LENGTH_LONG).show()
                }
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
    }

    /**
     * Handles both ADDING new dogs and UPDATING existing dogs.
     */
    private fun saveDogToFirestore() {
        // 1. Collect updated data
        val dogName = dogNameEditText.text.toString().trim()
        val breed = breedEditText.text.toString().trim()
        val sex = sexEditText.text.toString().trim()
        val ageInt = ageEditText.text.toString().trim().toIntOrNull() ?: 0
        val bio = bioEditText.text.toString().trim()

        val finalImageUrl = imageUrl ?: currentDog?.imageUrl ?: ""

        // 2. Create data map
        val dogData = mutableMapOf<String, Any>(
            "name" to dogName,
            "breed" to breed,
            "sex" to sex,
            "bio" to bio,
            "age" to ageInt,
            "isVaccinated" to vaccinatedSwitch.isChecked, // Boolean value from switch
            "isSterilized" to sterilizedSwitch.isChecked,   // Boolean value from switch
            "status" to (currentDog?.status ?: "Available for Adoption"),
            "imageUrl" to finalImageUrl
        )

        // Preserve dateAdded if editing
        dogData["dateAdded"] = currentDog?.dateAdded ?: Date()

        // 3. Determine the Firestore operation
        val firestoreRef = firestore.collection(DOGS_COLLECTION_PATH)

        val task = if (currentDog != null && currentDog!!.documentId.isNotEmpty()) {
            // EDIT MODE: Update existing document
            firestoreRef.document(currentDog!!.documentId).update(dogData)
        } else {
            // CREATE MODE: Add new document
            firestoreRef.add(dogData)
        }

        // 4. Handle result
        task.addOnSuccessListener {
            val isEditing = currentDog != null
            val dogAction = if (isEditing) "updated" else "added"
            Toast.makeText(this, "Dog '$dogName' $dogAction successfully!", Toast.LENGTH_LONG).show()

            clearFields()
            startActivity(Intent(this, DogManagementActivity::class.java))
            finish()
        }
            .addOnFailureListener { e ->
                Log.e(TAG, "Firestore operation failed: ${e.message}", e)
                Toast.makeText(this, "Failed to save dog details: ${e.message}", Toast.LENGTH_LONG).show()
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
        currentDog = null
    }
}