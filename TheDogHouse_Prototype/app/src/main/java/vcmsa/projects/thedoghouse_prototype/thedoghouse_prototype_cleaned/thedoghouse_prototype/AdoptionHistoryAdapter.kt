package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AdoptionHistory(
    val dogName: String = "",
    val sex: String = "",
    val age: String = "",
    val ownerName: String = "",
    val address: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val idNumber: String = ""
)

class AdoptionHistoryAdapter(
    private val context: Context,
    private val adoptionList: List<AdoptionHistory>
) : RecyclerView.Adapter<AdoptionHistoryAdapter.AdoptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdoptionViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_adoption_history, parent, false)
        return AdoptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdoptionViewHolder, position: Int) {
        val adoption = adoptionList[position]
        holder.dogName.text = "Dog's Name: ${adoption.dogName}"
        holder.sex.text = "Sex: ${adoption.sex}"
        holder.age.text = "Age: ${adoption.age}"
        holder.ownerName.text = "Owner's Name: ${adoption.ownerName}"
        holder.address.text = "Address: ${adoption.address}"
        holder.contact.text = "Contact Number: ${adoption.contactNumber}"
        holder.email.text = "Email: ${adoption.email}"
        holder.idNumber.text = "ID Number: ${adoption.idNumber}"

        holder.downloadBtn.setOnClickListener {
            // TODO: implement real document download
            android.widget.Toast.makeText(
                context,
                "Downloading documents for ${adoption.dogName}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = adoptionList.size

    class AdoptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dogName: TextView = itemView.findViewById(R.id.tvDogName)
        val sex: TextView = itemView.findViewById(R.id.tvSex)
        val age: TextView = itemView.findViewById(R.id.tvAge)
        val ownerName: TextView = itemView.findViewById(R.id.tvOwnerName)
        val address: TextView = itemView.findViewById(R.id.tvAddress)
        val contact: TextView = itemView.findViewById(R.id.tvContact)
        val email: TextView = itemView.findViewById(R.id.tvEmail)
        val idNumber: TextView = itemView.findViewById(R.id.tvIdNumber)
        val downloadBtn: Button = itemView.findViewById(R.id.btnDownload)
    }
}
