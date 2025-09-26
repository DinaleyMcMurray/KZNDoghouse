package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DonationHistoryAdapter(private val donations: List<DonationRecord>) :
    RecyclerView.Adapter<DonationHistoryAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]
        holder.donorName.text = donation.donorName
        holder.donationType.text = donation.donationType
        holder.amount.text = "Amount: ${donation.amount}"
        holder.date.text = donation.date
    }

    override fun getItemCount(): Int = donations.size

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorName: TextView = itemView.findViewById(R.id.tvDonorName)
        val donationType: TextView = itemView.findViewById(R.id.tvDonationType)
        val amount: TextView = itemView.findViewById(R.id.tvAmount)
        val date: TextView = itemView.findViewById(R.id.tvDate)
    }
}
