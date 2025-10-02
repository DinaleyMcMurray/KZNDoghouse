package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    // Define admin credentials constants here for reference (Optional, but good practice)
    private val ADMIN_EMAIL = "admin@gmail.com"

    // Global references for the drawer components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // ---------------------------------------------------------------------
        // 1. GET USER ROLE
        // Retrieve the flag passed from the login activity (default to false)
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        // ---------------------------------------------------------------------

        // Edge-to-edge padding (Keep as is)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ===== Drawer + Toolbar setup =====
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        navView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)

        // Open drawer on nav icon or swipe
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ---------------------------------------------------------------------
        // 2. CONDITIONALLY SET NAVIGATION MENU
        // Swap the menu XML based on the user's role
        if (isAdmin) {
            // R.menu.admin_nav_menu must be your admin menu XML file
            navView.menu.clear()
            navView.inflateMenu(R.menu.navigation_menu_admin)
        } else {
            // R.menu.user_nav_menu must be your standard user menu XML file
            navView.menu.clear()
            navView.inflateMenu(R.menu.navigation_menu_user)
        }
        // ---------------------------------------------------------------------

        // 3. Handle navigation drawer clicks (UPDATED to cover both menus)
        navView.setNavigationItemSelectedListener { item ->
            // Use a 'when' statement to handle all possible menu item IDs from BOTH menus
            when (item.itemId) {
                // Shared/User Menu Items
                R.id.nav_home -> navigateTo(HomeActivity::class.java, clearStack = true)
                R.id.nav_adoption -> navigateTo(AdoptionActivity::class.java)
                R.id.nav_adoption_history -> navigateTo(AdoptionHistoryActivity::class.java)
                R.id.nav_donation_history -> navigateTo(DonationHistoryActivity::class.java)
                R.id.nav_volunteer -> navigateTo(VolunteerActivity::class.java)
                R.id.nav_newsletter -> navigateTo(NewsletterActivity::class.java)

                // Admin-Specific Menu Items
                R.id.nav_volunteer_management -> navigateTo(VolunteerManagementActivity::class.java)
                // Add any other admin-specific menu items here

                else -> false // Handle unhandled item IDs
            }

            drawerLayout.closeDrawers()
            true
        }

        // Close drawer on back button press (Keep as is)
        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }

        // ===== Direct Button Navigation (Keep as is) =====
        // Navigate to DogFoodActivity when button7 is clicked
        findViewById<Button>(R.id.button7).setOnClickListener {
            startActivity(Intent(this, DogFoodActivity::class.java))
        }

        // Navigate to VolunteerActivity when button8 is clicked
        findViewById<Button>(R.id.button8).setOnClickListener {
            startActivity(Intent(this, VolunteerActivity::class.java))
        }
    }

    // Helper function for cleaner navigation code
    private fun navigateTo(activityClass: Class<*>, clearStack: Boolean = false) {
        val intent = Intent(this, activityClass).apply {
            if (clearStack) {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        startActivity(intent)
        if (clearStack) finish()
    }
}