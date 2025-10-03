package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class EventsManagementActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_events_management)

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Correct assignment to class vars
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)

        // ✅ Open drawer with nav icon
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ✅ Handle nav clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {
                    startActivity(Intent(this, DogManagementActivity::class.java))
                }
                R.id.nav_volunteer_management -> {
                    startActivity(Intent(this, VolunteerManagementActivity::class.java))
                }
                R.id.nav_events_management -> {
                    // already here, just close
                }
                R.id.nav_adoption_history -> {
                    startActivity(Intent(this, AdoptionHistoryActivity::class.java))
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}


//        // Intent to open the app when user taps the notification
//        val intent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )

        // Channel

//    }
//        // Notification design
//        val builder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.calendar) // replace with your own icon
//            .setContentTitle("We have a new event!")
//            .setContentText("Dont miss $evetname on the $eventdate")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        // Show notification
//        val notificationManager = NotificationManagerCompat.from(this)
//        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
//}
