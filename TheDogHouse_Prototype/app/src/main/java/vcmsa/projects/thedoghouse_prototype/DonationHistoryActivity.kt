package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class DonationHistoryActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonationHistoryAdapter
    private lateinit var donationList: MutableList<DonationRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_history) // XML with DrawerLayout + Toolbar + RecyclerView

        // === Drawer + Toolbar setup ===
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        // Open drawer when toolbar nav icon is clicked
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
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
                R.id.nav_fundsdonation -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, FundsDonationsActivity::class.java))
                    finish()
                }
                R.id.nav_volunteer -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, VolunteerActivity::class.java))
                    finish()
                }
                R.id.nav_adoption -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, ViewAdoptionActivity::class.java))
                    finish()
                }
                R.id.nav_donation_history -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, DonationHistoryActivity::class.java))
                    finish()
                }
                R.id.nav_help -> {
                    // Optional: Handle logout
                    startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // === RecyclerView setup ===
        recyclerView = findViewById(R.id.recyclerdonation)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy data (replace with Firebase/DB later)
        donationList = mutableListOf(
            DonationRecord("John Doe", "Funds", 500, "2025-09-15"),
            DonationRecord("Jane Smith", "Dog Food", 2, "2025-09-16"),
            DonationRecord("Mike Johnson", "Medication", 3, "2025-09-17")
        )

        adapter = DonationHistoryAdapter(donationList)
        recyclerView.adapter = adapter
    }
}


//package vcmsa.projects.thedoghouse_prototype
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.drawerlayout.widget.DrawerLayout
//import com.google.android.material.appbar.MaterialToolbar
//import com.google.android.material.navigation.NavigationView
//
//class DonationHistoryActivity : AppCompatActivity() {
//
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var navigationView: NavigationView
//    private lateinit var toolbar: MaterialToolbar
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_donation_history) // your XML with DrawerLayout
//
//        drawerLayout = findViewById(R.id.drawer_layout)
//        navigationView = findViewById(R.id.navigation_view)
//        toolbar = findViewById(R.id.toolbar)
//
//        // Hook up toolbar icon to open drawer
//        toolbar.setNavigationOnClickListener {
//            drawerLayout.openDrawer(navigationView)
//        }
//
//        // Handle nav item clicks
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_dog_management -> {
//                    startActivity(Intent(this, DogManagementActivity::class.java))
//                }
//                R.id.nav_volunteer_management -> {
//                    startActivity(Intent(this, VolunteerManagementActivity::class.java))
//                }
//                R.id.nav_events_management -> {
//                    startActivity(Intent(this, EventsManagementActivity::class.java))
//                }
//                R.id.nav_adoption_history -> {
//                    startActivity(Intent(this, AdoptionHistoryActivity::class.java))
//                }
//                R.id.nav_logout -> {
//                    // Optional: Handle logout
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
//                }
//            }
//            drawerLayout.closeDrawers()
//            true
//        }
//    }
//}
//
//
////package vcmsa.projects.thedoghouse_prototype
////
////import android.os.Bundle
////import androidx.appcompat.app.AppCompatActivity
////import androidx.databinding.DataBindingUtil.setContentView
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////
////class DonationHistoryActivity : AppCompatActivity() {
////
////    private lateinit var recyclerView: RecyclerView
////    private lateinit var adapter: DonationHistoryAdapter
////    private lateinit var donationList: MutableList<DonationRecord>
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_donation_history)
//
////
////        // Setup RecyclerView
////        recyclerView = findViewById(R.id.recyclerdonation)
////        recyclerView.layoutManager = LinearLayoutManager(this)
////
////        // Dummy data (replace with database or Firebase later)
////        donationList = mutableListOf(
////            DonationRecord("John Doe", "Funds", 500, "2025-09-15"),
////            DonationRecord("Jane Smith", "Dog Food", 2, "2025-09-16"),
////            DonationRecord("Mike Johnson", "Medication", 3, "2025-09-17")
////        )
////
////        adapter = DonationHistoryAdapter(donationList)
////        recyclerView.adapter = adapter
////    }
////    }
////}
