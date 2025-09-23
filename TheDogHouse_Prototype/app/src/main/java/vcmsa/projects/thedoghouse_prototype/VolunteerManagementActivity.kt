package vcmsa.projects.thedoghouse_prototype

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VolunteerManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VolunteerAdapter
    private lateinit var volunteerList: MutableList<VolunteerRecord>

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
        recyclerView = findViewById(R.id.recyclerVolunteerManagement)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy volunteer data for management
        volunteerList = mutableListOf(
            VolunteerRecord("Alice Brown", "Weekly Dog Walking", "2025-09-15"),
            VolunteerRecord("David Green", "Food Donations", "2025-09-16"),
            VolunteerRecord("Sophia White", "Medical Support", "2025-09-17")
        )

        adapter = VolunteerAdapter(volunteerList)
        recyclerView.adapter = adapter
    }
}
