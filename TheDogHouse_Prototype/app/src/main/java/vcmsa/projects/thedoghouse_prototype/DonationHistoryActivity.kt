package vcmsa.projects.thedoghouse_prototype

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DonationHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonationHistoryAdapter
    private lateinit var donationList: MutableList<DonationRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_history)

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerDonationHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy data (replace with database or Firebase later)
        donationList = mutableListOf(
            DonationRecord("John Doe", "Funds", 500, "2025-09-15"),
            DonationRecord("Jane Smith", "Dog Food", 2, "2025-09-16"),
            DonationRecord("Mike Johnson", "Medication", 3, "2025-09-17")
        )

        adapter = DonationHistoryAdapter(donationList)
        recyclerView.adapter = adapter
    }
}
