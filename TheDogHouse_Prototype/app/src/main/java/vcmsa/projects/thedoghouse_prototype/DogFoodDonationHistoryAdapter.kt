package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button // Import Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class DogFoodDonationHistoryAdapter(
    private var donations: MutableList<HistoryDogFoodRecord>,
    private val deleteClickListener: (String) -> Unit // Lambda to handle delete click
) : RecyclerView.Adapter<DogFoodDonationHistoryAdapter.DogFoodViewHolder>() {

    // Helper function to format Timestamp (Remains the same)
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

    fun removeItem(documentId: String) {
        val index = donations.indexOfFirst { it.documentId == documentId }
        if (index != -1) {
            donations.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_dogfood, parent, false)
        return DogFoodViewHolder(view)
    }

    override fun getItemCount(): Int = donations.size

    override fun onBindViewHolder(holder: DogFoodViewHolder, position: Int) {
        val item = donations[position]

        // 1. Bind Text Data
        holder.donorName.text = "Donor: ${item.donorName ?: "Anonymous"}"
        holder.line2.text = "Food: ${item.dogFoodName ?: "N/A"}"
        holder.line3.text = "Date: ${item.dropOffDate ?: "Unknown Date"}"
        holder.line4.text = "Time: ${item.dropOffTime ?: "N/A"}"
        holder.line5.text = "Record Timestamp: ${formatDate(item.timestamp)}"

        // 2. ⚡️ Bind Delete Button Listener ⚡️
        holder.deleteButton.setOnClickListener {
            // Pass the unique documentId back to the Activity for deletion
            deleteClickListener(item.documentId)
        }
    }

    class DogFoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorName: TextView = itemView.findViewById(R.id.DonorName)
        val line2: TextView = itemView.findViewById(R.id.dogFoodName)
        val line3: TextView = itemView.findViewById(R.id.dropOffDate)
        val line4: TextView = itemView.findViewById(R.id.dropOffTime)
        val line5: TextView = itemView.findViewById(R.id.Timestamp)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDelete)
    }
}