package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout // Import DrawerLayout
import com.google.android.material.appbar.MaterialToolbar // Import MaterialToolbar
import com.google.android.material.navigation.NavigationView // Import NavigationView

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var logoutButton: Button // For the dashboard button
    private lateinit var dogManagementCard: androidx.cardview.widget.CardView // For the CardView button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        // 1. Initialize Drawer/Toolbar components
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        logoutButton = findViewById(R.id.btn_admin_logout)
        dogManagementCard = findViewById(R.id.card_dog_management)

        // Set Toolbar as the Action Bar (optional, but good practice)
        setSupportActionBar(toolbar)

        // 2. Hook up toolbar icon to open drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // 3. Handle Dashboard Card Click (e.g., the Image Card)
        dogManagementCard.setOnClickListener {
            startActivity(Intent(this, DogManagementActivity::class.java))
        }

        // 4. Handle Dashboard Logout Button
        logoutButton.setOnClickListener {
            performLogout()
        }

        // 5. Handle Nav Item Clicks (Copied from DogManagementActivity)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {
                    startActivity(Intent(this, DogManagementActivity::class.java))
                }
                R.id.nav_volunteer_management -> {
                    startActivity(Intent(this, VolunteerManagementActivity::class.java))
                }
                R.id.nav_events_management -> {
                    startActivity(Intent(this, EventsManagementActivity::class.java))
                }
                R.id.nav_adoption_history -> {
                    startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                }
                R.id.nav_logout -> {
                    performLogout()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Apply insets (Keep this at the end if you want)
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Extracted Logout Function
    private fun performLogout() {
        Toast.makeText(this, "Admin Logged Out", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}