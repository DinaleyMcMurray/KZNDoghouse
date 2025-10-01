package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DonationHistoryAdapter(private val donations: List<DonationRecord>) :
    RecyclerView.Adapter<DonationHistoryAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerdonation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]
        holder.donorName.text = donation.donorName
        holder.donationType.text = donation.donationType
        holder.quantity.text = "Amount: ${donation.amount}"
        holder.date.text = donation.date
    }

    override fun getItemCount(): Int = donations.size

    // In your DonationHistoryAdapter.kt file:

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val donorName: TextView = itemView.findViewById(R.id.textDonorName)
        val donationType: TextView = itemView.findViewById(R.id.textDonationType)

        // Assuming 'amount' is mapped to 'textDonationQuan' in the new layout
        val quantity: TextView = itemView.findViewById(R.id.textDonationQuan)

        // Assuming 'date' is mapped to 'textDate' in the new layout
        val date: TextView = itemView.findViewById(R.id.textDate)

        // You also have buttons now, which you can add if needed:
         val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
         val buttonAdopted: Button = itemView.findViewById(R.id.buttonAdopted)
    }
}
