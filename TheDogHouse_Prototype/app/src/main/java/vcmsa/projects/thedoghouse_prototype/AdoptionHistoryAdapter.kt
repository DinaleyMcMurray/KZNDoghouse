package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class AdoptionHistoryAdapter(
    private val context: Context,
    private val adoptionList: List<AdoptionHistory>
) : RecyclerView.Adapter<AdoptionHistoryAdapter.DynamicViewHolder>() {

    // 1. View Holder Definition is fine for dynamic creation
    class DynamicViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        // FIX 1: Explicitly create the root LinearLayout using the parent's context
        val layout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            // Optional: Add padding here
        }
        return DynamicViewHolder(layout)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val adoption = adoptionList[position]
        val layout = holder.layout

        // FIX 4: Clear all views *before* adding the new set,
        // as the ViewHolder is being recycled. (Still not optimal, but fixes the immediate error).
        layout.removeAllViews()

        // Helper function for adding text views
        fun addTextView(label: String, value: String, bold: Boolean = false) {
            val tv = TextView(context)
            tv.text = "$label $value"
            if (bold) tv.setTypeface(null, Typeface.BOLD)
            layout.addView(tv)
        }

        addTextView("Dog's Name:", adoption.dogName, true)
        addTextView("Dog's Sex:", adoption.sex)
        addTextView("Dog's Age:", adoption.age)
        // ... (other addTextView calls)

        // FIX 2: Define and initialize the button before adding it
        val btnDownload = Button(context).apply {
            text = "Download Application"
            // Set a basic click listener (for demonstration)
            setOnClickListener {
                // Implement download logic here (e.g., toast message)
            }
        }
        layout.addView(btnDownload)
    }

    override fun getItemCount(): Int = adoptionList.size

    // RECOMMENDED FIX for AdoptionHistoryActivity filtering error:
    // Add a function to update the list without creating a new adapter instance
    fun setData(newAdoptionList: List<AdoptionHistory>) {
        // This is a simplified update that replaces the entire list
        (adoptionList as MutableList).clear()
        (adoptionList as MutableList).addAll(newAdoptionList)
        notifyDataSetChanged()
    }
}