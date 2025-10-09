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
import android.content.Intent

class AdoptionHistoryAdapter(
    private val context: Context,
    private var adoptionList: List<AdoptionHistory>,
    private val deleteClickListener: (AdoptionHistory) -> Unit
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
        val btnDELETE: Button = itemView.findViewById(R.id.btnDELETE)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdoptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleradoptionhistory, parent, false)
        return AdoptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdoptionViewHolder, position: Int) {
        val adoption = adoptionList[position]

        holder.textDogName.text = "Dog's Name: ${adoption.dogName}"
        holder.textSex.text = "Dog's Sex: ${adoption.sex}"
        holder.textAge.text = "Dog's Age: ${adoption.age}"
        holder.textUsername.text = "Client's Name: ${adoption.ownerName}"
        val fileName = adoption.documentUrl.substringAfterLast('/')
        holder.textDocUrl.text = "Document: $fileName"

        holder.textUploadDate.text =
            "Upload Date: ${adoption.uploadDate?.let { dateFormat.format(it) } ?: "N/A"}"

        holder.btnDocuments.setOnClickListener {
            if (adoption.documentUrl.isNotEmpty()) {
                openFile(context, adoption.documentUrl, adoption.ownerName)
            } else {
                Toast.makeText(context, "No document URL available.", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnDELETE.setOnClickListener {
            deleteClickListener(adoption)
        }
    }

    override fun getItemCount(): Int = adoptionList.size

    fun setData(newAdoptionList: List<AdoptionHistory>) {
        adoptionList = newAdoptionList
        notifyDataSetChanged()
    }

    // ⚡️ NEW: Remove item utility for live UI update after deletion ⚡️
    fun removeItem(documentId: String) {
        val mutableList = adoptionList.toMutableList()
        val index = mutableList.indexOfFirst { it.documentId == documentId }
        if (index != -1) {
            mutableList.removeAt(index)
            adoptionList = mutableList.toList()
            notifyItemRemoved(index)
        }
    }

    private fun openFile(context: Context, url: String, clientName: String) {
        try {
            val cleanUrl = url.replace("http://", "https://").trim()
            val viewerUrl = "https://docs.google.com/viewer?url=$cleanUrl"
            val uri = Uri.parse(viewerUrl)

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