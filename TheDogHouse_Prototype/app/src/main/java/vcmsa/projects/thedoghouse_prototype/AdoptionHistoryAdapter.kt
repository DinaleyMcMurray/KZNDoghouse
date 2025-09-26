package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdoptionHistoryAdapter(
    private val context: Context,
    private val adoptionList: List<AdoptionHistory>
) : RecyclerView.Adapter<AdoptionHistoryAdapter.DynamicViewHolder>() {

    class DynamicViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        return DynamicViewHolder(layout)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val adoption = adoptionList[position]
        val layout = holder.layout
        layout.removeAllViews()

        fun addTextView(label: String, value: String, bold: Boolean = false) {
            val tv = TextView(context)
            tv.text = "$label $value"
            if (bold) tv.setTypeface(null, Typeface.BOLD)
            layout.addView(tv)
        }

        addTextView("Dog's Name:", adoption.dogName, true)
        addTextView("Dog's Sex:", adoption.sex)
        addTextView("Dog's Age:", adoption.age)
        addTextView("Owner's Name:", adoption.ownerName, true)
        addTextView("Address:", adoption.address)
        addTextView("Contact Number:", adoption.contactNumber)
        addTextView("Email:", adoption.email)
        addTextView("ID Number:", adoption.idNumber)

        // Add a download button
        layout.addView(btn)
    }

    override fun getItemCount(): Int = adoptionList.size
}
