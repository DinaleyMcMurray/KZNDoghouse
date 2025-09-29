package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ===== Drawer + Toolbar setup (From Ntobeko2) =====
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val navView: NavigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)

        // Open drawer on nav icon or swipe
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle navigation drawer clicks
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                // === Home ===
                R.id.nav_home -> {
                    // Navigate to HomeActivity and clear back stack
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }

                // === Adoptions ===
                R.id.nav_adoption -> {
                    startActivity(Intent(this, AdoptionActivity::class.java))
                }
                R.id.nav_view_adoption -> {
                    startActivity(Intent(this, ViewAdoptionActivity::class.java))
                }
                R.id.nav_adoption_history -> {
                    startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                }

                // === Donations ===
                R.id.nav_funds -> {
                    startActivity(Intent(this, FundsDonationsActivity::class.java))
                }
                R.id.nav_dog_food -> {
                    startActivity(Intent(this, DogFoodActivity::class.java))
                }
                R.id.nav_meds -> {
                    startActivity(Intent(this, MedsDonationActivity::class.java))
                }
                R.id.nav_donation_history -> {
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                }

                // === Volunteers ===
                R.id.nav_volunteer -> {
                    startActivity(Intent(this, VolunteerActivity::class.java))
                }
                R.id.nav_volunteer_management -> {
                    startActivity(Intent(this, VolunteerManagementActivity::class.java))
                }

                // === Newsletter ===
                R.id.nav_newsletter -> {
                    startActivity(Intent(this, NewsletterActivity::class.java))
                }

                // === Register / Login ===
                R.id.nav_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
                R.id.nav_login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.nav_admin_login -> {
                    startActivity(Intent(this, AdminLoginActivity::class.java))
                }
            }

            drawerLayout.closeDrawers()
            true
        }

        // Close drawer on back button press
        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                isEnabled = false // Disable this callback
                onBackPressedDispatcher.onBackPressed() // Perform default back action
            }
        }

        // ===== Direct Button Navigation (from HEAD/combined) =====

        // Navigate to DogFoodActivity when button7 is clicked (from HEAD/Ntobeko2)
        val goToDogFoodButton = findViewById<Button>(R.id.button7)
        goToDogFoodButton.setOnClickListener {
            startActivity(Intent(this, DogFoodActivity::class.java))
        }

        // Navigate to VolunteerActivity when button8 is clicked (from HEAD)
        val volButton = findViewById<Button>(R.id.button8)
        volButton.setOnClickListener {
            val intent = Intent(this, VolunteerActivity::class.java)
            startActivity(intent)
        }
    }
}