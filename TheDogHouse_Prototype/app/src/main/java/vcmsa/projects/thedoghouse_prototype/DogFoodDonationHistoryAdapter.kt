package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// NOTE: This adapter is dedicated to Dog Food data
class DogFoodDonationHistoryAdapter(private var donations: MutableList<HistoryDogFoodRecord>) :
    RecyclerView.Adapter<DogFoodDonationHistoryAdapter.DogFoodViewHolder>() {

    // Helper function to format Timestamp
    private fun formatDate(timestamp: Timestamp?): String {
        return timestamp?.let {
            SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault()).format(it.toDate())
        } ?: "Unknown Timestamp"
    }

    fun updateData(newDonations: List<HistoryDogFoodRecord>) {
        this.donations.clear()
        this.donations.addAll(newDonations)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogFoodViewHolder {
        // You MUST ensure your item layout for Dog Food is named R.layout.dogfoodfunds
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_dogfood, parent, false)
        return DogFoodViewHolder(view)
    }

    override fun getItemCount(): Int = donations.size

    override fun onBindViewHolder(holder: DogFoodViewHolder, position: Int) {
        val item = donations[position]

        holder.donorName.text = "Donor: ${item.donorName ?: "Anonymous"}"
        holder.line2.text = "Food: ${item.dogFoodName ?: "N/A"}"
        holder.line3.text = "Date: ${item.dropOffDate ?: "Unknown Date"}"
        holder.line4.text = "Time: ${item.dropOffTime ?: "N/A"}"
        holder.line5.text = "Record Timestamp: ${formatDate(item.timestamp)}"
    }

    // This ViewHolder uses the IDs from your list item layout (dogfoodfunds.xml)
    class DogFoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorName: TextView = itemView.findViewById(R.id.DonorName) // Common ID
        val line2: TextView = itemView.findViewById(R.id.dogFoodName)
        val line3: TextView = itemView.findViewById(R.id.dropOffDate)
        val line4: TextView = itemView.findViewById(R.id.dropOffTime)
        val line5: TextView = itemView.findViewById(R.id.Timestamp)
    }
}