package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Intent // ADDED


class AdoptionHistoryAdapter(
    private val context: Context,
    private var adoptionList: List<AdoptionHistory>
) : RecyclerView.Adapter<AdoptionHistoryAdapter.AdoptionViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    class AdoptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDogName: TextView = itemView.findViewById(R.id.textDogName)
        val textSex: TextView = itemView.findViewById(R.id.textSex)
        val textAge: TextView = itemView.findViewById(R.id.textAge)
        val textUsername: TextView = itemView.findViewById(R.id.textUserame)
        val textDocUrl: TextView = itemView.findViewById(R.id.DocUrl)
        val textUploadDate: TextView = itemView.findViewById(R.id.UploadDate)
        val btnDocuments: Button = itemView.findViewById(R.id.btnDocuments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdoptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleradoptionhistory, parent, false)
        return AdoptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdoptionViewHolder, position: Int) {
        val adoption = adoptionList[position]

        // 2. Bind Data to Views
        holder.textDogName.text = "Dog's Name: ${adoption.dogName}"
        holder.textSex.text = "Dog's Sex: ${adoption.sex}"
        holder.textAge.text = "Dog's Age: ${adoption.age}"
        holder.textUsername.text = "Client's Name: ${adoption.ownerName}"

        // Assuming documentUrl contains the full Cloudinary path (e.g., .../n7odm5ji3gwjpz1xic6b.pdf)
        val fileName = adoption.documentUrl.substringAfterLast('/')
        holder.textDocUrl.text = "Document: $fileName"

        holder.textUploadDate.text =
            "Upload Date: ${adoption.uploadDate?.let { dateFormat.format(it) } ?: "N/A"}"

        // 3. Handle Download Button Click
        holder.btnDocuments.setOnClickListener {
            if (adoption.documentUrl.isNotEmpty()) {
                // ⚡️ CALL THE NEW OPEN FILE FUNCTION ⚡️
                openFile(context, adoption.documentUrl, adoption.ownerName)
            } else {
                Toast.makeText(context, "No document URL available.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = adoptionList.size

    fun setData(newAdoptionList: List<AdoptionHistory>) {
        adoptionList = newAdoptionList
        notifyDataSetChanged()
    }

    // AdoptionHistoryAdapter.kt
    private fun openFile(context: Context, url: String, clientName: String) {
        try {
            val cleanUrl = url.replace("http://", "https://").trim()
            val viewerUrl = "https://docs.google.com/viewer?url=$cleanUrl"
            val uri = Uri.parse(viewerUrl)

            // ✅ Force the link to open in a web browser instead of checking for a specific viewer app
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
            Toast.makeText(context, "Opening document for $clientName...", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Log.e("FileOpener", "Error opening document: ${e.message}", e)
            Toast.makeText(context, "Error opening document. Try again.", Toast.LENGTH_LONG).show()
        }
    }
}