package vcmsa.projects.thedoghouse_prototype

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// NOTE: You must ensure the 'NewsletterItem' class has the necessary fields
// like title, description, and date.

class NewsletterAdapter(private val newsletters: List<NewsletterItem>) :
    RecyclerView.Adapter<NewsletterAdapter.NewsletterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsletterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.newsletterrecyclerview, parent, false)
        return NewsletterViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsletterViewHolder, position: Int) {
        val item = newsletters[position]

        // --- 1. Text Fields ---
        holder.name.text = "Name: ${item.title}"
        holder.location.text = "Where: ${item.description}"
        holder.date.text = "When: ${item.date}"

        // --- 2. Cost Field ---
        // Assuming 'item' has a 'cost' field and 'holder.cost' maps to R.id.textSter
        //holder.cost.text = "Cost: ${item.cost}"

        // --- 3. Image Loading ---
        // NOTE: This requires adding the Coil or Glide library dependency to your build.gradle.kts.
        // I'll use Coil's syntax as an example.

        // Assuming 'item' has an 'imageUrl' field (String)
        // Coils's extension function for ImageView makes loading simple.
       // holder.image.load(item.imageUrl) {
            // Optional: Add a placeholder while loading
         //   placeholder(R.drawable.image_placeholder) // Replace with your actual placeholder drawable
           // error(R.drawable.image_error) // Replace with your actual error drawable
            //crossfade(true)
        //}

        // --- 4. Button Click Listener ---
        // Assuming 'holder.rsvpButton' maps to R.id.button
        holder.rsvpButton.setOnClickListener {
            // Implement the action for the RSVP button here
            // Example: Log the event name that was clicked
            Log.d("NewsletterAdapter", "RSVP button clicked for event: ${item.title}")

            // Example: Trigger a callback function defined in your adapter/fragment/activity
            // onRsvpClicked(item)
        }
    }

    override fun getItemCount(): Int = newsletters.size

    class NewsletterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views initialized using the IDs from the XML
        val image: ImageView = itemView.findViewById(R.id.imageEvent)
        val name: TextView = itemView.findViewById(R.id.textEventName)
        val location: TextView = itemView.findViewById(R.id.textWhere)
        val date: TextView = itemView.findViewById(R.id.textWhen)
        val cost: TextView = itemView.findViewById(R.id.textSter) // Mapped to Cost
        val rsvpButton: Button = itemView.findViewById(R.id.button) // Mapped to RSVP button

        // NOTE: The TextView with text "About: " does not have an ID in your XML.
        // If you need to access it, you must add an ID (e.g., android:id="@+id/textAbout").
    }
}