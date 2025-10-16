package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class HelpActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_help)

            // 1. Initialize Nav Drawer Views
            // These IDs must match the ones in your activity_view_adoption.xml layout
            drawerLayout = findViewById(R.id.drawer_layout)
            navigationView = findViewById(R.id.navigation_view)
            toolbar = findViewById(R.id.toolbar)

            // Set the toolbar as the action bar
            setSupportActionBar(toolbar)

            // Set click listener for the navigation icon (hamburger icon) to open the drawer
            toolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

        // === WhatsApp Button Functionality ===
        val btnContactManager: Button = findViewById(R.id.btnContactManager)
        btnContactManager.setOnClickListener {
            val phoneNumber = "27716215128" // South Africa +27
            val message = "Hello, I need assistance with the app."
            try {
                val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.whatsapp")
                    data = Uri.parse(url)
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                // If WhatsApp not installed, open Play Store
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                }
                startActivity(intent)
            }
        }

        // === Navigation Drawer Menu Handling ===
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> startActivity(Intent(this, UserProfileActivity::class.java))
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_newsletter -> startActivity(Intent(this, NewsletterActivity::class.java))
                R.id.nav_fundsdonation -> startActivity(Intent(this, MedsDonationActivity::class.java))
                R.id.nav_volunteer -> startActivity(Intent(this, VolunteerActivity::class.java))
                R.id.nav_adoption -> startActivity(Intent(this, ViewAdoptionActivity::class.java))
                R.id.nav_help -> { startActivity(Intent(this, HelpActivity::class.java))
                    finish()}
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}


//package vcmsa.projects.thedoghouse_prototype
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.widget.Button
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.DataBindingUtil.setContentView
//import androidx.drawerlayout.widget.DrawerLayout
//import com.google.android.material.appbar.MaterialToolbar
//import com.google.android.material.navigation.NavigationView
//
//class HelpActivity : AppCompatActivity() {
//
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var navigationView: NavigationView
//    private lateinit var toolbar: MaterialToolbar
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_help)
//
//        val btnContactManager: Button = findViewById(R.id.btnContactManager)
//
//        btnContactManager.setOnClickListener {
//            // Replace with manager's phone number
//            val phoneNumber = "27716215128" // South Africa +27
//            val message = "Hello, I need assistance with the app."
//
//            try {
//                val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
//                val intent = Intent(Intent.ACTION_VIEW).apply {
//                    setPackage("com.whatsapp")
//                    data = Uri.parse(url)
//                }
//                startActivity(intent)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                // If WhatsApp is not installed, open Play Store
//                val intent = Intent(Intent.ACTION_VIEW).apply {
//                    data = Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
//                }
//                startActivity(intent)
//            }
//        }
//
//        // Handle nav item clicks
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_account -> {
//                    startActivity(Intent(this, EditProfileActivity::class.java))
//                }
//                R.id.nav_logout -> {
//                    startActivity(Intent(this, LoginActivity::class.java))
//                }
//                R.id.nav_home -> {
//                    startActivity(Intent(this, HomeActivity::class.java))
//                }
//                R.id.nav_newsletter -> {
//                    startActivity(Intent(this, NewsletterActivity::class.java))
//                }
//                R.id.nav_medsdonation -> {
//                    // Optional: Handle logout
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
//                }
//                R.id.nav_volunteer -> {
//                    // Optional: Handle logout
//                    startActivity(Intent(this, VolunteerActivity::class.java))
//                    finish()
//                }
//                R.id.nav_adoption -> {
//                    // Optional: Handle logout
//                    startActivity(Intent(this, AdoptionActivity::class.java))
//                    finish()
//                }
//                R.id.nav_donation_history -> {
//                    // Optional: Handle logout
//                    startActivity(Intent(this, DonationHistoryActivity::class.java))
//                    finish()
//                }
//                R.id.nav_help -> {
//                    // Optional: Handle logout
//                    startActivity(Intent(this, HelpActivity::class.java))
//                    finish()
//                }
//            }
//            drawerLayout.closeDrawers()
//            true
//        }
//    }
//}
