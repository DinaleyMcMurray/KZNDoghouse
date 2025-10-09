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

class MedsDonationHistoryAdapter(
    private val donations: MutableList<HistoryMedsRecord>,
    private val deleteClickListener: (String) -> Unit // Lambda to handle delete click
) : RecyclerView.Adapter<MedsDonationHistoryAdapter.MedsViewHolder>() {

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

    fun removeItem(documentId: String) {
        val index = donations.indexOfFirst { it.documentId == documentId }
        if (index != -1) {
            donations.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_meds, parent, false)
        return MedsViewHolder(view)
    }

    override fun getItemCount(): Int = donations.size

    override fun onBindViewHolder(holder: MedsViewHolder, position: Int) {
        val item = donations[position]

        // 1. Bind Text Data (Existing logic)
        holder.donorName.text = "Donor: ${item.donorName ?: "Anonymous"}"
        holder.medicationName.text = "Medication: ${item.medicationName ?: "N/A"}"
        holder.dropOffDate.text = "Date: ${item.dropOffDate ?: "Unknown Date"}"
        holder.dropOffTime.text = "Time: ${item.dropOffTime ?: "N/A"}"
        holder.quantity.text = "Quantity: ${item.quantity ?: "N/A"}"
        holder.recordTimestamp.text = "Record Timestamp: ${formatDate(item.timestamp)}"

        holder.deleteButton.setOnClickListener {
            // Pass the unique documentId back to the Activity for deletion
            deleteClickListener(item.documentId)
        }
    }

    class MedsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorName: TextView = itemView.findViewById(R.id.DonorName)
        val medicationName: TextView = itemView.findViewById(R.id.medicationName)
        val dropOffDate: TextView = itemView.findViewById(R.id.dropOffDate)
        val dropOffTime: TextView = itemView.findViewById(R.id.dropOffTime)
        val quantity: TextView = itemView.findViewById(R.id.Quantity)
        val recordTimestamp: TextView = itemView.findViewById(R.id.Timestamp)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDelete)
    }
}