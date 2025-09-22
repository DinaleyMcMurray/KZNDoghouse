package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.net.Uri
import android.os.Bundle
<<<<<<< HEAD
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class AdoptionActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adoption) // make sure this is the DrawerLayout layout

        // Find views from activity_adoption.xml
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

        // Drawer item clicks
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.nav_uploads -> Toast.makeText(this, "My Uploads", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawers()
            true
        }
    }
=======
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class AdoptionActivity : AppCompatActivity() {

    private val PICK_PDF_REQUEST = 1001
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption) // make sure XML filename matches!

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
                // TODO: upload file to Firebase Storage or your backend
                Toast.makeText(this, "Uploading: $selectedFileUri", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a document first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Copies the asset PDF to cache and opens it with an external PDF viewer.
     */
    private fun openPdfFromAssets(fileName: String) {
        try {
            val inputStream = assets.open(fileName)
            val outFile = File(cacheDir, fileName)
            val outputStream = FileOutputStream(outFile)

            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()

            val uri: Uri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                outFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Launches a file picker to select a document.
     */
    private fun pickDocument() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri
                Toast.makeText(this, "Selected: $uri", Toast.LENGTH_SHORT).show()
            }
        }
    }
>>>>>>> d50d9fb5fe108f7b409db4de23e2609b01338cb7
}
