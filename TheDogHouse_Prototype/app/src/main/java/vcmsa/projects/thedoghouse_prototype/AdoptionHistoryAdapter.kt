package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

        holder.textUploadDate.text = "Upload Date: ${adoption.uploadDate?.let { dateFormat.format(it) } ?: "N/A"}"

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

    /**
     * Initiates a view request for the file URL using a browser or PDF viewer Intent.
     * This avoids using the complex and unreliable Android DownloadManager.
     */
    // AdoptionHistoryAdapter.kt (inside openFile function)

    private fun openFile(context: Context, url: String, clientName: String) {
        try {
            // 1. Force URL to HTTPS
            val secureUrl = url.replace("http://", "https://")
            val uri = Uri.parse(secureUrl)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                setDataAndType(uri, "application/pdf")
            }

            // 3. Check if there's an app that can handle this Intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Toast.makeText(context, "Opening document for: $clientName...", Toast.LENGTH_LONG).show()
            } else {
                // If setDataAndType fails (e.g., if the URL is not perfectly formed), try again with just setData
                val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                    setData(uri)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                if (fallbackIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(fallbackIntent)
                    Toast.makeText(context, "Opening document in browser/viewer...", Toast.LENGTH_LONG).show()
                } else {
                    // Final failure: show the user the URL to copy/paste
                    Toast.makeText(context, "No app found. URL: $secureUrl", Toast.LENGTH_LONG).show()
                    Log.w("FileOpener", "No activity found to handle ACTION_VIEW for PDF or generic URL.")
                }
            }

        } catch (e: Exception) {
            Log.e("FileOpener", "Failed to open file: ${e.message}", e)
            Toast.makeText(context, "Failed to open document. Check URL and try again.", Toast.LENGTH_LONG).show()
        }
    }
}