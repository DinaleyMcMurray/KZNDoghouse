package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// import java.text.SimpleDateFormat // Not needed since dateAndTime is a String
// import java.util.Locale

class EventAdapter(
    private val eventsList: MutableList<EventData>,
    private val onEditClick: (EventData) -> Unit,
    private val onDeleteClick: (EventData) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Event Detail Fields (from list_item_event_admin.xml)
        val textEventName: TextView = itemView.findViewById(R.id.textEventName)
        val textDateAndTime: TextView = itemView.findViewById(R.id.textDateAndTime)
        val textLocation: TextView = itemView.findViewById(R.id.textLocation)
        val textCost: TextView = itemView.findViewById(R.id.textCost)
        val textAbout: TextView = itemView.findViewById(R.id.textAbout)
        val textRsvpStatus: TextView = itemView.findViewById(R.id.textRsvpStatus)

        // Action Buttons
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerevents, parent, false) // Assumed item layout name
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = eventsList[position]

        // Bind all data fields to the TextViews
        holder.textEventName.text = "Event Name: ${currentEvent.name}"
        holder.textDateAndTime.text = "When: ${currentEvent.dateAndTime}"
        holder.textLocation.text = "Where: ${currentEvent.location}"
        holder.textCost.text = "Cost: ${currentEvent.cost}"

        // Show a brief snippet for "About"
        holder.textAbout.text = "About: ${currentEvent.about}"

        // Display RSVP status
        val rsvpText = if (currentEvent.needsRsvp) "Yes" else "No"
        holder.textRsvpStatus.text = "RSVP Required: $rsvpText"

        // Set click listeners for the action buttons
        holder.buttonEdit.setOnClickListener {
            onEditClick(currentEvent)
        }

        holder.buttonDelete.setOnClickListener {
            onDeleteClick(currentEvent)
        }
    }

    override fun getItemCount(): Int = eventsList.size

    /**
     * Updates the list of events displayed by the adapter.
     */
    fun updateData(newEvents: List<EventData>) {
        eventsList.clear()
        eventsList.addAll(newEvents)
        notifyDataSetChanged()
    }
}