// DogAdapterPublic.kt

package vcmsa.projects.thedoghouse_prototype

import DogDataRecord
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
// Removed unused import: import com.cloudinary.android.uploadwidget.UploadWidget.startActivity

class DogAdapterPublic(private val dogs: MutableList<DogDataRecord>, private val context: Context) :
    RecyclerView.Adapter<DogAdapterPublic.DogPublicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogPublicViewHolder {
        // NOTE: Ensure 'viewadoptionrecyclerview' is the correct name of your XML layout
        // for the single list item (which you also provided in the prompt).
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewadoptionrecyclerview, parent, false) // Using a more specific name for clarity
        return DogPublicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogPublicViewHolder, position: Int) {
        val dog = dogs[position]

        // 1. Load Image
        if (dog.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(dog.imageUrl)
                .placeholder(R.drawable.bonebutton) // Use your placeholder here
                .into(holder.dogImage)
        }

        // 2. Set Text Fields
        holder.name.text = "Name: ${dog.name}"
        holder.sex.text = "Sex: ${dog.sex}"
        holder.breed.text = "Breed: ${dog.breed}"
        holder.age.text = "Age: ${dog.age}" // Correctly linked and bound
        holder.vacStatus.text = "Vaccination Status: ${if (dog.isVaccinated) "Complete" else "Incomplete"}"
        holder.sterStatus.text = "Sterilization Status: ${if (dog.isSterilized) "Yes" else "No"}"

        // Setting the Bio text to include the actual content.
        // Assuming the XML R.id.textBio is intended for the content label + content.
        holder.bio.text = "Bio: ${dog.bio}"

        // 3. Handle Button Clicks
        holder.adoptButton.setOnClickListener {
            Toast.makeText(context, "Clicked Adopt for ${dog.name}", Toast.LENGTH_SHORT).show()
            // FIX: Use the 'context' passed to the adapter to start the activity
            context.startActivity(Intent(context, AdoptionActivity::class.java))
        }

        holder.sponsorButton.setOnClickListener {
            Toast.makeText(context, "Clicked Sponsor for ${dog.name}", Toast.LENGTH_SHORT).show()
            // FIX: Use the 'context' passed to the adapter to start the activity
            context.startActivity(Intent(context, FundsDonationsActivity::class.java))
        }
    }

    override fun getItemCount(): Int = dogs.size

    fun updateData(newDogs: List<DogDataRecord>) {
        dogs.clear()
        dogs.addAll(newDogs)
        notifyDataSetChanged()
    }

    class DogPublicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Find all views from your recycler XML
        val dogImage: ImageView = itemView.findViewById(R.id.imageDog)
        val name: TextView = itemView.findViewById(R.id.textDogName)
        val sex: TextView = itemView.findViewById(R.id.textSex)
        val breed: TextView = itemView.findViewById(R.id.textBreed)
        val age: TextView = itemView.findViewById(R.id.textAge) // <-- Added Age link
        val vacStatus: TextView = itemView.findViewById(R.id.textVac)
        val sterStatus: TextView = itemView.findViewById(R.id.textSter)
        val bio: TextView = itemView.findViewById(R.id.textBio) // <-- Added Bio link

        // Assuming adoptButton1 is Adopt and adoptButton is Sponsor
        val adoptButton: ImageButton = itemView.findViewById(R.id.adoptButton1)
        val sponsorButton: ImageButton = itemView.findViewById(R.id.adoptButton)
    }
}