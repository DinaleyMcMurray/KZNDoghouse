package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class DogManagementActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_management) // your XML with DrawerLayout

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        // Hook up toolbar icon to open drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        findViewById<Button>(R.id.AddDogBtn).setOnClickListener {
            startActivity(Intent(this, AddDogActivity::class.java))
        }
        // Handle nav item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {
                    startActivity(Intent(this, AddDogActivity::class.java))
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
                    // Optional: Handle logout
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}

