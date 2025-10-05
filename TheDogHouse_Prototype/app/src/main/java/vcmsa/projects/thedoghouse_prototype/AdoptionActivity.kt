package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

// Firebase/Date Imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

// Cloudinary Imports
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class AdoptionActivity : AppCompatActivity() {

    private val TAG = "AdoptionActivity"
    private val STORAGE_PERMISSION_CODE = 101

    private val PICK_PDF_REQUEST = 1001
    private var selectedFileUri: Uri? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private val APPLICATION_FORM_FILENAME = "Application_Form.pdf"

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Cloudinary Configuration
    private val CLOUDINARY_CLOUD_NAME = "dyuieeirb"
    private val CLOUDINARY_UPLOAD_PRESET = "KZNDoghouse"
    private val CLOUDINARY_API_KEY = "959111626652188"
    private val CLOUDINARY_API_SECRET = "MPC45jC70zK656BiiADN-0ULohs"

    // Variable to hold the ID of the dog being adopted
    private var dogIdForAdoption: String? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adoption)

        // Initialize Firebase services
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize Cloudinary
        try {
            val config = mapOf(
                "cloud_name" to CLOUDINARY_CLOUD_NAME,
                "api_key" to CLOUDINARY_API_KEY,
                "api_secret" to CLOUDINARY_API_SECRET
            )
            MediaManager.init(applicationContext, config)
        } catch (e: Exception) {
            Log.e(TAG, "Cloudinary Initialization failed: ${e.message}")
        }

        // ⚡️ GET DOG ID FROM INTENT ⚡️
        dogIdForAdoption = intent.getStringExtra("DOG_ID_FOR_ADOPTION")
        if (dogIdForAdoption.isNullOrEmpty()) {
            Toast.makeText(this, "Please select a dog to adopt first.", Toast.LENGTH_LONG).show()
            // Depending on design, you might want to prevent further action or finish here.
        } else {
            Log.d(TAG, "Dog ID received: $dogIdForAdoption")
        }


        // Edge-to-edge padding logic (standard practice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. NAVIGATION DRAWER SETUP
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)

        // Toolbar + hamburger toggle
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 2. ADOPTION LOGIC SETUP

        // Download the PDF file logic
        val zipLinkText = findViewById<TextView>(R.id.application_zip_link)
        zipLinkText.setOnClickListener {
            checkStoragePermissionAndDownload()
        }

        // Select a document to upload (Original Logic)
        val selectText = findViewById<TextView>(R.id.select)
        selectText.setOnClickListener {
            pickDocument()
        }

        // Cancel button
        findViewById<Button>(R.id.CancelBtn).setOnClickListener {
            finish() // close activity
        }

        // ⚡️ UPLOAD BUTTON LOGIC ⚡️
        findViewById<Button>(R.id.UploadBtn).setOnClickListener {
            if (selectedFileUri != null) {
                // Check if dogId is present before starting upload
                if (dogIdForAdoption.isNullOrEmpty()) {
                    Toast.makeText(this, "Error: No dog selected for adoption.", Toast.LENGTH_SHORT).show()
                } else {
                    uploadFileToCloudinary(selectedFileUri!!)
                }
            } else {
                Toast.makeText(this, "Please select a document first", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Navigation Drawer Item Clicks (Your provided logic)
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
                    startActivity(Intent(this, FundsDonationsActivity::class.java))
                    finish()
                }
                R.id.nav_volunteer -> {
                    startActivity(Intent(this, VolunteerActivity::class.java))
                    finish()
                }
                R.id.nav_adoption -> {
                    startActivity(Intent(this, AdoptionActivity::class.java))
                    finish()
                }
                R.id.nav_donation_history -> {
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedFileUri = data.data
            val selectTextView = findViewById<TextView>(R.id.select)
            selectTextView.text = getFileName(selectedFileUri!!)
            Toast.makeText(this, "Document selected: ${selectTextView.text}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadFileToCloudinary(fileUri: Uri) {
        val user = auth.currentUser
        val dogId = dogIdForAdoption // Use the ID retrieved in onCreate

        if (user == null || dogId.isNullOrEmpty()) {
            Toast.makeText(this, "Prerequisites missing (User/Dog ID). Cannot upload.", Toast.LENGTH_SHORT).show()
            findViewById<Button>(R.id.UploadBtn).isEnabled = true
            return
        }

        // Disable button while uploading
        findViewById<Button>(R.id.UploadBtn).isEnabled = false
        Toast.makeText(this, "Uploading application to Cloudinary...", Toast.LENGTH_LONG).show()

        // 1. Upload file to Cloudinary
        MediaManager.get().upload(fileUri)
            .option("resource_type", "auto")
            .option("folder", "doghouse_app/adoption_forms")
            .option("upload_preset", CLOUDINARY_UPLOAD_PRESET)
            .callback(object : UploadCallback {

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val fileUrl = resultData["url"] as String
                    Log.d(TAG, "Cloudinary Upload successful. URL: $fileUrl")

                    // 2. PASS THE DOG ID TO FIRESTORE SAVE
                    saveAdoptionRecordToFirestore(user.uid, getFileName(fileUri), fileUrl, dogId)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    findViewById<Button>(R.id.UploadBtn).isEnabled = true
                    Log.e(TAG, "Cloudinary Upload failed: ${error.description}")
                    Toast.makeText(this@AdoptionActivity, "Upload failed: ${error.description}", Toast.LENGTH_LONG).show()
                }

                override fun onStart(requestId: String) { Log.d(TAG, "Upload started: $requestId") }
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) { /* Update progress bar if implemented */ }
                override fun onReschedule(requestId: String, error: ErrorInfo) { /* Log reschedule */ }
            })
            .dispatch()
    }

    private fun saveAdoptionRecordToFirestore(userId: String, fileName: String, fileUrl: String, dogId: String) {
        // Create the nested document structure: users/{userId}/Adoption/{uniqueId}
        val adoptionRecord = hashMapOf(
            "fileName" to fileName,
            "fileUrl" to fileUrl,
            "uploadDate" to Date(),
            "status" to "Pending Review",
            "dogId" to dogId // ⚡️ SAVING THE DOG ID ⚡️
        )

        db.collection("Users").document(userId)
            .collection("Adoption")
            .add(adoptionRecord)
            .addOnSuccessListener {
                Toast.makeText(this, "Application uploaded and submitted for review!", Toast.LENGTH_LONG).show()

                // Cleanup UI
                selectedFileUri = null
                findViewById<TextView>(R.id.select).text = "+ Select Doc to upload"
                findViewById<Button>(R.id.UploadBtn).isEnabled = true
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Firestore Save Failed", e)
                Toast.makeText(this, "Failed to record application metadata.", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.UploadBtn).isEnabled = true
            }
    }


    // =========================================================================
    // PERMISSIONS AND DOWNLOAD LOGIC (Unchanged)
    // =========================================================================

    private fun checkStoragePermissionAndDownload() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "Please log in to use this feature.", Toast.LENGTH_SHORT).show()
            return
        }

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            } else {
                downloadFileFromAssets(APPLICATION_FORM_FILENAME)
            }
        } else {
            downloadFileFromAssets(APPLICATION_FORM_FILENAME)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
                downloadFileFromAssets(APPLICATION_FORM_FILENAME)
            } else {
                Toast.makeText(this, "Storage Permission Denied. Cannot download file.", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun downloadFileFromAssets(fileName: String) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destFile = File(downloadsDir, fileName)

            if (destFile.exists()) {
                Toast.makeText(this, "$fileName already exists in Downloads.", Toast.LENGTH_LONG).show()
                return
            }

            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            assets.open(fileName).use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Toast.makeText(this, "$fileName downloaded to Downloads folder.", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Log.e(TAG, "Download failed for $fileName", e)
            Toast.makeText(this, "Failed to download file. Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // =========================================================================
    // UPLOAD LOGIC UTILITIES (Unchanged)
    // =========================================================================

    private fun pickDocument() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select PDF Document"),
                PICK_PDF_REQUEST
            )
        } catch (e: Exception) {
            Toast.makeText(this, "No file manager found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = it.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result ?: "Selected Document"
    }
}