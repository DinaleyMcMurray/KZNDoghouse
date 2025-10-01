package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class FundsDonationsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_funds_donations)

        // Apply safe insets (status + nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // === Drawer + Toolbar setup ===
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)

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

        // === Handle nav clicks ===
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                R.id.nav_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
                R.id.nav_newsletter -> {
                    startActivity(Intent(this, NewsletterActivity::class.java))
                }
                R.id.nav_meds -> {
                    startActivity(Intent(this, MedsDonationActivity::class.java))
                }
                R.id.nav_funds -> {
                    Toast.makeText(this, "Already on Funds Donations", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_volunteer -> {
                    startActivity(Intent(this, VolunteerActivity::class.java))
                }
                R.id.nav_uploads -> {
                    Toast.makeText(this, "Uploads page", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings page", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
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
}
