// DonationHistoryAdapter.kt (UPDATED)

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

// ⚡️ ADAPTED: Adapter now takes a deleteClickListener lambda ⚡️
class DonationHistoryAdapter(
    private val donations: MutableList<HistoryFundsRecord>,
    private val deleteClickListener: (String) -> Unit // Lambda to handle delete click
) : RecyclerView.Adapter<DonationHistoryAdapter.FundsViewHolder>() {

    private fun formatDate(date: Any?): String {
        return when (date) {
            is Timestamp -> SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault()).format(date.toDate())
            is String -> date
            else -> "Unknown Date"
        }
    }

    fun updateData(newDonations: List<HistoryFundsRecord>) {
        this.donations.clear()
        this.donations.addAll(newDonations)
        notifyDataSetChanged()
    }

    // ⚡️ NEW: Function to remove item from list after successful delete ⚡️
    fun removeItem(documentId: String) {
        val index = donations.indexOfFirst { it.documentId == documentId }
        if (index != -1) {
            donations.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundsViewHolder {
        // Assuming your provided layout XML is named item_donation_history.xml or similar
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_funds, parent, false)
        return FundsViewHolder(view)
    }

    override fun getItemCount(): Int = donations.size

    override fun onBindViewHolder(holder: FundsViewHolder, position: Int) {
        val item = donations[position]

        // 1. Bind Text Data
        holder.donorName.text = "Donor: ${item.donorName ?: "N/A"}"
        holder.line2.text = "Amount: R${item.amount ?: "0"}"
        holder.line3.text = "Date Submitted: ${formatDate(item.dateSubmitted)}"
        holder.line4.text = "Status: ${item.status ?: "N/A"}"
        holder.line5.text = "Type: ${item.type ?: "Funds"}"

        // 2. ⚡️ Bind Delete Button Listener ⚡️
        holder.deleteButton.setOnClickListener {
            // Pass the unique documentId back to the Activity for deletion
            deleteClickListener(item.documentId)
        }
    }

    // 3. Simple ViewHolder maps the IDs
    class FundsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorName: TextView = itemView.findViewById(R.id.DonorName)
        val line2: TextView = itemView.findViewById(R.id.amount)
        val line3: TextView = itemView.findViewById(R.id.dateSubmitted)
        val line4: TextView = itemView.findViewById(R.id.dropOffDate) // Status
        val line5: TextView = itemView.findViewById(R.id.dropOffTime) // Type
        // ⚡️ ADDED: Map the Delete Button ID ⚡️
        val deleteButton: Button = itemView.findViewById(R.id.buttonDelete)
    }
}