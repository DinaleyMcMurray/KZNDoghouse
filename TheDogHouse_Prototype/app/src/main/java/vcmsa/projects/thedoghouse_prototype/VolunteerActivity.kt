package vcmsa.projects.thedoghouse_prototype

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VolunteerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VolunteerAdapter
    private lateinit var volunteerList: MutableList<VolunteerRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteer)

        // Apply system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerVolunteers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Dummy volunteer data (all fields filled properly)
        volunteerList = mutableListOf(
            VolunteerRecord(
                name = "Alice Brown",
                sex = "Female",
                age = "28",
                hours = "10 hrs/week",
                contactNumber = "123-456-7890",
                email = "alice@example.com",
                role = "Dog Walker"
            ),
            VolunteerRecord(
                name = "David Green",
                sex = "Male",
                age = "35",
                hours = "8 hrs/week",
                contactNumber = "987-654-3210",
                email = "david@example.com",
                role = "Food Donor"
            ),
            VolunteerRecord(
                name = "Sophia White",
                sex = "Female",
                age = "30",
                hours = "12 hrs/week",
                contactNumber = "555-444-3333",
                email = "sophia@example.com",
                role = "Medical Support"
            )
        )

        // Bind adapter
        adapter = VolunteerAdapter(volunteerList)
        recyclerView.adapter = adapter
    }
}
