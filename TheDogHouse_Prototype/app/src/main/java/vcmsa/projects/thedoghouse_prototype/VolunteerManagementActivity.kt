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

        // ✅ Use the RecyclerView ID from your XML
        recyclerView = findViewById(R.id.recyclerVolunteers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Dummy data for testing (all 7 fields filled)
        volunteerList = mutableListOf(
            VolunteerRecord(
                name = "Alice Brown",
                sex = "Female",
                age = "28",
                hours = "10 hours",
                contactNumber = "123456789",
                email = "alice@email.com",
                role = "Dog Walker"
            ),
            VolunteerRecord(
                name = "David Green",
                sex = "Male",
                age = "35",
                hours = "5 hours",
                contactNumber = "987654321",
                email = "david@email.com",
                role = "Food Donor"
            )
        )

        // Attach adapter
        adapter = VolunteerAdapter(volunteerList)
        recyclerView.adapter = adapter
    }
}
