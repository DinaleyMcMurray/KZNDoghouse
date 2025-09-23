package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recyclerVolunteers(private val volunteers: List<VolunteerRecord>) :
    RecyclerView.Adapter<recyclerVolunteers.VolunteerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_volunteer, parent, false)
        return VolunteerViewHolder(view)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val volunteer = volunteers[position]

        holder.name.text = volunteer.name
        holder.role.text = volunteer.role
        holder.date.text = volunteer.joinedDate
    }

    override fun getItemCount(): Int = volunteers.size

    class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textVolunteerName)
        val role: TextView = itemView.findViewById(R.id.textVolunteerRole)
        val date: TextView = itemView.findViewById(R.id.textVolunteerDate)
    }
}
