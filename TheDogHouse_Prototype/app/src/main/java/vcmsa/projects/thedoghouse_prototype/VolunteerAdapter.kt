package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VolunteerAdapter(private val volunteers: List<VolunteerRecord>) :
    RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {

    class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textVolunteerName)
        val sex: TextView = itemView.findViewById(R.id.textSex)
        val age: TextView = itemView.findViewById(R.id.textAge)
        val hours: TextView = itemView.findViewById(R.id.textHours)
        val contact: TextView = itemView.findViewById(R.id.textVolunteerNo)
        val email: TextView = itemView.findViewById(R.id.textVolunteerEmail)
        val btnDocs: Button = itemView.findViewById(R.id.btnDocuments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_volunteer, parent, false)
        return VolunteerViewHolder(view)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val volunteer = volunteers[position]
        holder.name.text = volunteer.name
        holder.sex.text = volunteer.sex
        holder.age.text = volunteer.age
        holder.hours.text = volunteer.hours
        holder.contact.text = volunteer.contactNumber
        holder.email.text = volunteer.email

        holder.btnDocs.setOnClickListener {
            // TODO: implement document download or open action
        }
    }

    override fun getItemCount(): Int = volunteers.size
}
