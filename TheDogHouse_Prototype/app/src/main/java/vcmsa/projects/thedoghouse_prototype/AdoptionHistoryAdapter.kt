package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AdoptionHistory(
    val dogName: String = "",
    val sex: String = "",
    val age: String = "",
    val ownerName: String = "",
    val address: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val idNumber: String = ""
)

class AdoptionHistoryAdapter(
    private val context: Context,
    private val adoptionList: List<AdoptionHistory>
) : RecyclerView.Adapter<AdoptionHistoryAdapter.DynamicViewHolder>() {

    class DynamicViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)
        return DynamicViewHolder(layout)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val adoption = adoptionList[position]
        val layout = holder.layout
        layout.removeAllViews()

        fun addTextView(label: String, value: String, bold: Boolean = false) {
            val tv = TextView(context)
            tv.text = "$label $value"
            tv.textSize = 18f
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
        val btn = Button(context)
        btn.text = "Download Documents"
        layout.addView(btn)
    }

    override fun getItemCount(): Int = adoptionList.size
}
