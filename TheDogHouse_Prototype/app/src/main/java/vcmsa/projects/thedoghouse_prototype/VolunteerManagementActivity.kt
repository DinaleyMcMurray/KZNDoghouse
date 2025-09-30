package vcmsa.projects.thedoghouse_prototype

import VolunteerRecord
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VolunteerManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VolunteerAdapter
    private lateinit var volunteerList: MutableList<VolunteerRecord>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteer_management)

        // Apply system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclervolunteers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy volunteer data for management
        volunteerList = mutableListOf(
            VolunteerRecord("Alice Brown", "Weekly Dog Walking", "2025-09-15"),
            VolunteerRecord("David Green", "Food Donations", "2025-09-16"),
            VolunteerRecord("Sophia White", "Medical Support", "2025-09-17")
        )

        adapter = VolunteerAdapter(volunteerList)
        recyclerView.adapter = adapter

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
//            .setSmallIcon(R.drawable.volunteer) // replace with your own icon
//            .setContentTitle("We have a new volunteer!")
//            .setContentText("Volunteer name: $volunteername, Volunteer age: $volunteerage")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        // Show notification
//        val notificationManager = NotificationManagerCompat.from(this)
//        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

}
