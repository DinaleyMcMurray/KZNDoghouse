package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsletterAdapter(private val newsletters: List<NewsletterItem>, private val context: Context) :
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
        holder.location.text = "Where: ${item.location}"
        holder.date.text = "When: ${item.date}"
        holder.cost.text = "Cost: ${item.cost}"
        holder.description.text = "About: ${item.description}"

        // --- 2. Image Loading (GLIDE IMPLEMENTATION - CORRECTED) ---
        // ⚡️ Using 'item' and 'holder.image' with placeholder/error logic ⚡️
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.rehome) // Your chosen placeholder
                .error(R.drawable.donatingmeds)  // Your chosen error image
                .into(holder.image) // Correct ImageView ID is 'image'
        }
        else {
            // Optional: If no image URL, set the placeholder directly
            holder.image.setImageResource(R.drawable.rehome)
        }

        // --- 3. Button Click Listener ---
        holder.rsvpButton.setOnClickListener {
            Log.d("NewsletterAdapter", "RSVP button clicked for event: ${item.title}")
            // Implement RSVP/Navigation logic here
        }
    }

    override fun getItemCount(): Int = newsletters.size

    class NewsletterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views initialized using the IDs from newsletterrecyclerview.xml
        val image: ImageView = itemView.findViewById(R.id.imageEvent) // ⚡️ Corrected ID: imageEvent ⚡️
        val name: TextView = itemView.findViewById(R.id.textEventName)
        val location: TextView = itemView.findViewById(R.id.textWhere)
        val date: TextView = itemView.findViewById(R.id.textWhen)
        val cost: TextView = itemView.findViewById(R.id.textSter)
        val description: TextView = itemView.findViewById(R.id.textAbout)
        val rsvpButton: Button = itemView.findViewById(R.id.button)
    }
}