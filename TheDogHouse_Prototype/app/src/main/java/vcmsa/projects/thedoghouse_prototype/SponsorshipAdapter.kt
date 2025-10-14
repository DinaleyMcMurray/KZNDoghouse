package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class SponsorshipAdapter(
    private val context: Context,
    private val records: MutableList<SponsorshipRecord>
) : RecyclerView.Adapter<SponsorshipAdapter.RecordViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy (HH:mm)", Locale.getDefault())
    private val firestore = FirebaseFirestore.getInstance()

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // IDs from your item_sponsor_record.xml
        val tvDogName: TextView = itemView.findViewById(R.id.textDogName)
        val tvDogId: TextView = itemView.findViewById(R.id.textSex) // Reused textSex ID for Dog ID
        val tvSponsorName: TextView = itemView.findViewById(R.id.textUserame) // Reused textUserame for Sponsor Name
        val tvAge: TextView = itemView.findViewById(R.id.Age)
        val tvMobile: TextView = itemView.findViewById(R.id.Number)
        val tvAmount: TextView = itemView.findViewById(R.id.textAmount)
        val tvDate: TextView = itemView.findViewById(R.id.UploadDate)
        val btnDelete: Button = itemView.findViewById(R.id.btnDELETE)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        // IMPORTANT: Ensure you have an XML file named 'item_sponsor_record.xml'
        // that contains the CardView layout you provided.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclersponsorhistory,parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]

        // 1. Populate Text Fields
        holder.tvDogName.text = "Dog's Name: ${record.dogName}"
        holder.tvDogId.text = "Dog's ID: ${record.dogId ?: "N/A"}"
        holder.tvSponsorName.text = "Client's Name: ${record.sponsorName}"
        holder.tvAge.text = "Age: ${record.sponsorAge}"
        holder.tvMobile.text = "Mobile Number: ${record.sponsorMobile}"
        // Note: Using the Euro symbol is just an example. Use your local currency.
        holder.tvAmount.text = "Amount: R${record.amount}"

        // Format Date (using dateSubmitted field from your Firestore structure)
        val dateValue = record.dateSubmitted?.let { dateFormat.format(it.toDate()) } ?: "N/A"
        holder.tvDate.text = "Upload Date: $dateValue"

        // 2. Handle Delete Button
        holder.btnDelete.setOnClickListener {
            deleteRecord(record, position)
        }
    }

    override fun getItemCount(): Int = records.size

    private fun deleteRecord(record: SponsorshipRecord, position: Int) {
        // Need both the User ID and the specific Sponsorship Record ID to find the document
        if (record.userId.isEmpty() || record.recordId.isEmpty()) {
            Toast.makeText(context, "Cannot delete: Missing user or record ID.", Toast.LENGTH_SHORT).show()
            return
        }

        // Path: Users/{userId}/Sponsors/{recordId}
        firestore.collection("Users")
            .document(record.userId)
            .collection("Sponsors")
            .document(record.recordId)
            .delete()
            .addOnSuccessListener {
                // Remove item from the list and notify adapter
                records.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(context, "Sponsorship record deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error deleting record: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("SponsorAdapter", "Error deleting record: ${e.message}", e)
            }
    }

    // Helper function for the Activity to refresh the data
    fun updateData(newRecords: List<SponsorshipRecord>) {
        records.clear()
        records.addAll(newRecords)
        notifyDataSetChanged()
    }
}