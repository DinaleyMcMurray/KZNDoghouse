package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button // NOTE: This import may no longer be necessary if you don't use 'Button' anywhere else.
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var dogManagementCard: androidx.cardview.widget.CardView
    // ⚡️ Removed: private lateinit var logoutButton: Button ⚡️

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        supportActionBar?.hide()
        setContentView(R.layout.activity_admin_home)

        // 1. Initialize Drawer/Toolbar components
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        dogManagementCard = findViewById(R.id.card_dog_management)
        // ⚡️ Removed: logoutButton initialization ⚡️

        // Set Toolbar as the Action Bar
        setSupportActionBar(toolbar)

        // 2. Hook up toolbar icon to open drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // 3. Handle Dashboard Card Click
        dogManagementCard.setOnClickListener {
            startActivity(Intent(this, DogManagementActivity::class.java))
        }

        // ⚡️ Removed: Dashboard Logout Button listener ⚡️

        // 4. Handle navigation clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> startActivity(Intent(this, DogManagementActivity::class.java))
                R.id.nav_volunteer_management -> startActivity(Intent(this, VolunteerManagementActivity::class.java))
                R.id.nav_events_management -> startActivity(Intent(this, EventsManagementActivity::class.java))
                R.id.nav_adoption_history -> startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                R.id.nav_dogfood -> {
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                }
                R.id.nav_sponsor -> startActivity(Intent(this, SponsorManagementActivity::class.java))
                R.id.nav_logout -> performLogout() // ⚡️ FIX: Call the clean function for logout ⚡️
                R.id.nav_home -> { /* Stay on current screen */ }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Apply insets
//        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

    // Extracted Logout Function
    private fun performLogout() {
        Toast.makeText(this, "Admin Logged Out", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        // Ensure proper closure of the admin session/back stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}