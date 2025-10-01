package vcmsa.projects.thedoghouse_prototype

import VolunteerRecord
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VolunteerAdapter(private val volunteers: List<VolunteerRecord>) :
    RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_volunteers, parent, false) // ✅ correct file name
        return VolunteerViewHolder(view)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val volunteer = volunteers[position]

        // Bind volunteer data
        holder.name.text = "Name: ${volunteer.name}"
        holder.sex.text = "Sex: ${volunteer.sex}"
        holder.age.text = "Age: ${volunteer.age}"
        holder.hours.text = "Hours: ${volunteer.hours}"
        holder.contact.text = "Contact: ${volunteer.contactNumber}"
        holder.email.text = "Email: ${volunteer.email}"

        // Handle button
        holder.btnDocuments.setOnClickListener {
            // TODO: Add your document download logic here
        }
    }

    override fun getItemCount(): Int = volunteers.size

    class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textVolunteerName)
        val sex: TextView = itemView.findViewById(R.id.textSex)
        val age: TextView = itemView.findViewById(R.id.textAge)
        val hours: TextView = itemView.findViewById(R.id.textHours)
        val contact: TextView = itemView.findViewById(R.id.textVolunteerNo)
        val email: TextView = itemView.findViewById(R.id.textVolunteerEmail)
        val btnDocuments: Button = itemView.findViewById(R.id.btnDocuments)
    }
}
