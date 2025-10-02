package vcmsa.projects.thedoghouse_prototype

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_events_management)

        // Apply edge-to-edge insets to your root view
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

//        // Intent to open the app when user taps the notification
//        val intent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )

        // Channel

    }
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
}
