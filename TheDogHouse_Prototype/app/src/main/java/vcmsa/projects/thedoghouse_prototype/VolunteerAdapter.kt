package vcmsa.projects.thedoghouse_prototype

import VolunteerRecord
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VolunteerAdapter(
    private val volunteers: MutableList<VolunteerRecord>,
    private val deleteListener: OnItemDeleteListener
) : RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {

    interface OnItemDeleteListener {
        fun onDeleteClick(documentId: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclervolunteer, parent, false)
        return VolunteerViewHolder(view)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val volunteer = volunteers[position]

        // Bind volunteer data
        holder.name.text = "Name: ${volunteer.name}"
        holder.gender.text = "Sex: ${volunteer.gender}"
        holder.age.text = "Age: ${volunteer.age}"
        holder.contact.text = "Contact: ${volunteer.contactNumber}"
        holder.email.text = "Email: ${volunteer.email}"

        // Implement Delete Button Click Listener
        holder.deleteButton.setOnClickListener {
            deleteListener.onDeleteClick(volunteer.documentId, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = volunteers.size

    class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textVolunteerName)
        val gender: TextView = itemView.findViewById(R.id.textGender)
        val age: TextView = itemView.findViewById(R.id.textAge)
        val contact: TextView = itemView.findViewById(R.id.textVolunteerNo)
        val email: TextView = itemView.findViewById(R.id.textVolunteerEmail)

        // 🚀 CORRECTED: Reference the button by R.id.DeleteBtn 🚀
        val deleteButton: Button = itemView.findViewById(R.id.DeleteBtn)
    }
}