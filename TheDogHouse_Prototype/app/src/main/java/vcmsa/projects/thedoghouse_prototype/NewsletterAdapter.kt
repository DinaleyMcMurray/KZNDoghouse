package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log // Log import is good for debugging
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast // Added Toast for user feedback on failure
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// NOTE: You need to define the 'NewsletterItem' data class elsewhere in your project
// for this adapter to compile successfully.

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
        holder.name.text = "${item.title}"
        holder.location.text = "Where: ${item.location}"
        holder.date.text = "When: ${item.date}"
        holder.cost.text = "Cost: ${item.cost}"
        holder.description.text = "${item.description}"

        // --- 2. Image Loading (GLIDE) ---
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.rehome)
                .error(R.drawable.donatingmeds)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.rehome)
        }

        // --- 3. Visibility Logic ---
        if (item.needsRsvp) {
            // Show the button if RSVP is required by the admin.
            holder.rsvpButton.visibility = View.VISIBLE
        } else {
            // Hide the button if RSVP is NOT required.
            holder.rsvpButton.visibility = View.GONE
        }
        // ------------------------------------------------------------------

        // === WhatsApp Button Functionality FIX ===
        // FIX 1: Use 'holder.rsvpButton' to reference the button view.
        // FIX 2: Call 'context.startActivity(intent)' to launch the Intent.

        holder.rsvpButton.setOnClickListener {
            val phoneNumber = "27716215128" // South Africa +27

            // Customize the message to include the event details
            val message = "Hello, I would like to RSVP for the event: ${item.title} on ${item.date}."

            try {
                val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.whatsapp")
                    data = Uri.parse(url)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // If WhatsApp is not installed or package name is wrong
                Log.e("NewsletterAdapter", "WhatsApp launch failed.", e)
                Toast.makeText(context, "WhatsApp not found. Opening Play Store.", Toast.LENGTH_SHORT).show()

                // Open Play Store
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=com.whatsapp")
                }
                // Use a try-catch in case opening the store also fails
                try {
                    context.startActivity(intent)
                } catch (storeError: Exception) {
                    // Fallback to web browser for Play Store
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")))
                }
            }
        }
    }

    override fun getItemCount(): Int = newsletters.size

    class NewsletterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views initialized using the IDs from newsletterrecyclerview.xml
        val image: ImageView = itemView.findViewById(R.id.imageEvent)
        val name: TextView = itemView.findViewById(R.id.textEventName)
        val location: TextView = itemView.findViewById(R.id.textWhere)
        val date: TextView = itemView.findViewById(R.id.textWhen)
        val cost: TextView = itemView.findViewById(R.id.textSter)
        val description: TextView = itemView.findViewById(R.id.textAbout)
        val rsvpButton: Button = itemView.findViewById(R.id.btnRsvp) // The RSVP button
    }
}