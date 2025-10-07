package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// This adapter is now only for Funds data
class DonationHistoryAdapter(private var donations: MutableList<HistoryFundsRecord>) :
    RecyclerView.Adapter<DonationHistoryAdapter.FundsViewHolder>() {

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

    // 1. Inflates the one and only required layout, using your specified file name
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundsViewHolder {
        // ⚡️ ADAPTED: Using the file name reference R.layout.recycler_history_funds
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_funds, parent, false)
        return FundsViewHolder(view)
    }

    override fun getItemCount(): Int = donations.size

    // 2. Binds the data directly using the fixed ID mappings
    override fun onBindViewHolder(holder: FundsViewHolder, position: Int) {
        val item = donations[position]

        holder.donorName.text = "Donor: ${item.donorName ?: "N/A"}"
        holder.line2.text = "Amount: R${item.amount ?: "0"}"
        holder.line3.text = "Date Submitted: ${formatDate(item.dateSubmitted)}"
        holder.line4.text = "Status: ${item.status ?: "N/A"}"
        holder.line5.text = "Type: ${item.type ?: "Funds"}"
    }

    // 3. Simple ViewHolder maps the IDs from recycler_history_funds.xml
    class FundsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorName: TextView = itemView.findViewById(R.id.DonorName)
        val line2: TextView = itemView.findViewById(R.id.amount)
        val line3: TextView = itemView.findViewById(R.id.dateSubmitted)

        // These are the IDs you used to map Status and Type, respectively
        val line4: TextView = itemView.findViewById(R.id.dropOffDate)
        val line5: TextView = itemView.findViewById(R.id.dropOffTime)
    }
}