package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil.setContentView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class AdoptionActivity : AppCompatActivity() {

    // Variables from both sections
    private val PICK_PDF_REQUEST = 1001
    private var selectedFileUri: Uri? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adoption)

        // Edge-to-edge padding logic (standard practice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // NAVIGATION DRAWER SETUP (From Ntobeko2)
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)

        // Toolbar + hamburger toggle
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_open, // Ensure R.string.nav_open and R.string.nav_close exist
            R.string.nav_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Drawer item clicks (Placeholder logic from snippet)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.nav_events_management -> Toast.makeText(this, "My Uploads", Toast.LENGTH_SHORT).show()
//                R.id.nav_settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(GravityCompat.START) // Fixed: use GravityCompat.START
            true
        }


        // ADOPTION LOGIC (From main/HEAD)
        // 1. Download/Open the PDF from assets
        val missionText = findViewById<TextView>(R.id.mission)
        missionText.setOnClickListener {
            openPdfFromAssets("Adoption Application FINAL pdf.pdf")
        }

        // 2. Select a document to upload
        val selectText = findViewById<TextView>(R.id.select)
        selectText.setOnClickListener {
            pickDocument()
        }

        // 3. Cancel button
        findViewById<Button>(R.id.button1).setOnClickListener {
            finish() // close activity
        }

        // 4. Upload button
        findViewById<Button>(R.id.button2).setOnClickListener {
            if (selectedFileUri != null) {
                // In a real app, this is where you'd start the upload service.
                Toast.makeText(this, "File selected for upload: ${getFileName(selectedFileUri!!)}", Toast.LENGTH_LONG).show()
                // TODO: upload file to Firebase Storage or your backend
            } else {
                Toast.makeText(this, "Please select a document first", Toast.LENGTH_SHORT).show()
            }
        }

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
                R.id.nav_medsdonation -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, LoginActivity::class.java))
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
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Must call the super implementation first
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedFileUri = data.data
            // Update the UI (e.g., the 'select' TextView) to show the file name
            val selectTextView = findViewById<TextView>(R.id.select)
            selectTextView.text = getFileName(selectedFileUri!!)
            Toast.makeText(this, "Document selected: ${selectTextView.text}", Toast.LENGTH_SHORT).show()
        }
    }

    // =========================================================================
    // IMPLEMENTATION OF ADOPTION LOGIC METHODS
    // =========================================================================

    private fun openPdfFromAssets(fileName: String) {
        try {
            val file = File(cacheDir, fileName)
            if (!file.exists()) {
                // Copy from assets to internal storage (cache)
                assets.open(fileName).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }

            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Open PDF with..."))

        } catch (e: Exception) {
            Toast.makeText(this, "Could not open document: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

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

    // Helper function to get file name from Uri
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