package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// NOTE: This adapter is dedicated to Medication data
class MedsDonationHistoryAdapter(private var donations: MutableList<HistoryMedsRecord>) :
    RecyclerView.Adapter<MedsDonationHistoryAdapter.MedsViewHolder>() {

    // Helper function to format Timestamp (Remains the same)
    private fun formatDate(timestamp: Timestamp?): String {
        return timestamp?.let {
            SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault()).format(it.toDate())
        } ?: "Unknown Timestamp"
    }

    fun updateData(newDonations: List<HistoryMedsRecord>) {
        this.donations.clear()
        this.donations.addAll(newDonations)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedsViewHolder {
        // Inflate using standard View Binding (not Data Binding)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_meds, parent, false)
        return MedsViewHolder(view)
    }

    override fun getItemCount(): Int = donations.size

    override fun onBindViewHolder(holder: MedsViewHolder, position: Int) {
        val item = donations[position]

        holder.donorName.text = "Donor: ${item.donorName ?: "Anonymous"}"
        // line2 matches medicationName from the XML
        holder.medicationName.text = "Medication: ${item.medicationName ?: "N/A"}"
        // line3 matches dropOffDate from the XML
        holder.dropOffDate.text = "Date: ${item.dropOffDate ?: "Unknown Date"}"
        // line4 matches dropOffTime from the XML
        holder.dropOffTime.text = "Time: ${item.dropOffTime ?: "N/A"}"
        // line5 matches Quantity from the XML
        holder.quantity.text = "Quantity: ${item.quantity ?: "N/A"}"
        // line6 matches Timestamp from the XML
        holder.recordTimestamp.text = "Record Timestamp: ${formatDate(item.timestamp)}"
    }

    // This ViewHolder uses the IDs from your list item layout (recycler_history_meds.xml)
    class MedsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorName: TextView = itemView.findViewById(R.id.DonorName)
        val medicationName: TextView = itemView.findViewById(R.id.medicationName) // Changed from line2
        val dropOffDate: TextView = itemView.findViewById(R.id.dropOffDate)     // Changed from line3
        val dropOffTime: TextView = itemView.findViewById(R.id.dropOffTime)     // Changed from line4
        val quantity: TextView = itemView.findViewById(R.id.Quantity)           // Changed from line5
        val recordTimestamp: TextView = itemView.findViewById(R.id.Timestamp)   // Changed from line6
    }
}