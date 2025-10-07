package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class DonationHistoryAdapter(private var donations: MutableList<Any>) :
    RecyclerView.Adapter<DonationHistoryAdapter.DonationViewHolder>() {

    // FIX: Move constants to companion object for scope access by the ViewHolder
    companion object {
        private const val TYPE_FUNDS = 1
        private const val TYPE_DOGFOOD = 2
        private const val TYPE_MEDS = 3
    }

    // Helper function to format date from Any (Timestamp or String)
    private fun formatDate(date: Any?): String {
        return when (date) {
            is Timestamp -> SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault()).format(date.toDate())
            is String -> date
            else -> "Unknown Date"
        }
    }

    fun updateData(newDonations: List<Any>) {
        this.donations.clear()
        this.donations.addAll(newDonations)
        notifyDataSetChanged()
    }

    // 2. Returns the correct view type integer based on the data object
    override fun getItemViewType(position: Int): Int {
        return when (donations[position]) {
            is HistoryFundsRecord -> TYPE_FUNDS
            is HistoryDogFoodRecord -> TYPE_DOGFOOD
            is HistoryMedsRecord -> TYPE_MEDS
            else -> throw IllegalArgumentException("Invalid item type at position $position")
        }
    }

    // 3. Inflates the correct layout based on the view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val layoutResId = when (viewType) {
            // ✅ CRITICAL FIX: These MUST be the file names of the individual list item layouts.
            TYPE_FUNDS -> R.layout.historyfunds
            TYPE_DOGFOOD -> R.layout.dogfoodfunds
            TYPE_MEDS -> R.layout.medsfunds // Assuming 'recyclerdonation' is your Meds item layout
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return DonationViewHolder(view, viewType)
    }

    override fun getItemCount(): Int = donations.size

    // 4. Binds the data to the TextViews
    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val item = donations[position]

        when (item) {
            is HistoryFundsRecord -> {
                // Funds (5 lines of data mapped to specific IDs)
                holder.donorName.text = "Donor: ${item.donorName ?: "N/A"}"
                holder.line2.text = "Amount: R${item.amount ?: "0"}"
                holder.line3.text = "Date Submitted: ${formatDate(item.dateSubmitted)}"
                holder.line4.text = "Status: ${item.status ?: "N/A"}"
                holder.line5.text = "Type: ${item.type ?: "Funds"}"
                holder.line6?.visibility = View.GONE
            }

            is HistoryDogFoodRecord -> {
                // Dog Food (5 lines of data mapped to specific IDs)
                holder.donorName.text = "Donor: ${item.donorName ?: "Anonymous"}"
                holder.line2.text = "Food: ${item.dogFoodName ?: "N/A"}"
                holder.line3.text = "Date: ${item.dropOffDate ?: "Unknown Date"}"
                holder.line4.text = "Time: ${item.dropOffTime ?: "N/A"}"

                // Line 5 uses the Timestamp ID for the record's timestamp
                holder.line5.text = "Record Timestamp: ${formatDate(item.timestamp)}"
                holder.line6?.visibility = View.GONE
            }

            is HistoryMedsRecord -> {
                // Meds (6 lines of data mapped to specific IDs)
                holder.donorName.text = "Donor: ${item.donorName ?: "Anonymous"}"
                holder.line2.text = "Medication: ${item.medicationName ?: "N/A"}"
                holder.line3.text = "Date: ${item.dropOffDate ?: "Unknown Date"}"
                holder.line4.text = "Time: ${item.dropOffTime ?: "N/A"}"
                holder.line5.text = "Quantity: ${item.quantity ?: 0}"

                // Line 6 uses the Timestamp ID for the record's timestamp
                holder.line6?.visibility = View.VISIBLE
                holder.line6?.text = "Record Timestamp: ${formatDate(item.timestamp)}"
            }

            else -> {
                holder.donorName.text = "Error"
                holder.line2.text = "Unknown Item"
                holder.line3.text = ""
                holder.line4.text = ""
                holder.line5.text = ""
                holder.line6?.text = ""
            }
        }
    }

    // 5. DonationViewHolder uses the viewType to map the correct layout IDs
    class DonationViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        // Common element
        val donorName: TextView = itemView.findViewById(R.id.DonorName)

        // Lines 2-5 map specific layout IDs to generic line variables
        val line2: TextView = when(viewType) {
            TYPE_FUNDS -> itemView.findViewById(R.id.amount)
            TYPE_DOGFOOD -> itemView.findViewById(R.id.dogFoodName)
            TYPE_MEDS -> itemView.findViewById(R.id.medicationName)
            else -> throw IllegalStateException("Invalid view holder initialization")
        }

        val line3: TextView = when(viewType) {
            TYPE_FUNDS -> itemView.findViewById(R.id.dateSubmitted)
            TYPE_DOGFOOD -> itemView.findViewById(R.id.dropOffDate)
            TYPE_MEDS -> itemView.findViewById(R.id.dropOffDate)
            else -> throw IllegalStateException("Invalid view holder initialization")
        }

        val line4: TextView = when(viewType) {
            TYPE_FUNDS -> itemView.findViewById(R.id.dropOffDate)
            TYPE_DOGFOOD -> itemView.findViewById(R.id.dropOffTime)
            TYPE_MEDS -> itemView.findViewById(R.id.dropOffTime)
            else -> throw IllegalStateException("Invalid view holder initialization")
        }

        val line5: TextView = when(viewType) {
            TYPE_FUNDS -> itemView.findViewById(R.id.dropOffTime)
            TYPE_DOGFOOD -> itemView.findViewById(R.id.Timestamp)
            TYPE_MEDS -> itemView.findViewById(R.id.Quantity)
            else -> throw IllegalStateException("Invalid view holder initialization")
        }

        // Line 6 handles the optional Timestamp field in Meds/DogFood layouts
        val line6: TextView? = try {
            if (viewType == TYPE_MEDS || viewType == TYPE_DOGFOOD) {
                itemView.findViewById(R.id.Timestamp)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}