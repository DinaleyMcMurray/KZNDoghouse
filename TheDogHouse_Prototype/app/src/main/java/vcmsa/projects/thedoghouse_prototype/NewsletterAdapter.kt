package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsletterAdapter(private val newsletters: List<NewsletterItem>) :
    RecyclerView.Adapter<NewsletterAdapter.NewsletterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsletterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_newsletter, parent, false)
        return NewsletterViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsletterViewHolder, position: Int) {
        val item = newsletters[position]
        holder.title.text = item.title
        holder.description.text = item.description
        holder.date.text = item.date
    }

    override fun getItemCount(): Int = newsletters.size

    class NewsletterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvNewsletterTitle)
        val description: TextView = itemView.findViewById(R.id.tvNewsletterDescription)
        val date: TextView = itemView.findViewById(R.id.tvNewsletterDate)
    }
}
