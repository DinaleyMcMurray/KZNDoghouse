// DogAdapter.kt

package vcmsa.projects.thedoghouse_prototype

import DogDataRecord // Import your data class
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView // We need an ImageView for the photo!
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Used for loading image URLs

class DogAdapter(private val dogs: MutableList<DogDataRecord>) :
    RecyclerView.Adapter<DogAdapter.DogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerdogmanagement, parent, false) // Ensure your XML is named recycler_dog_item.xml
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dog = dogs[position]
        val context = holder.itemView.context

        holder.name.text = "Dog's Name: ${dog.name}"
        holder.sex.text = "Dog's Sex: ${dog.sex}"
        holder.age.text = "Age: ${dog.age}"
        holder.bio.text = "Bio: ${dog.bio}"
        holder.vacStatus.text = "Vac Status: ${if (dog.isVaccinated) "Yes" else "No"}"
        holder.sterilStatus.text = "Sterilization Status: ${if (dog.isSterilized) "Yes" else "No"}"
        // NOTE: You are saving dog.breed, but it is missing in your current RecyclerView XML.

        // 1. Load the Image using the Cloudinary URL
        if (dog.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(dog.imageUrl)
                .placeholder(R.drawable.fundsdonation) // Use a default placeholder drawable
                .into(holder.dogImage)
        } else {
            holder.dogImage.setImageResource(R.drawable.fundsdonation)
        }

        // Handle button clicks (Edit/Adopted)
        holder.buttonEdit.setOnClickListener { /* Implement Edit Logic */ }
        holder.buttonAdopted.setOnClickListener { /* Implement Adopted Logic */ }
    }

    override fun getItemCount(): Int = dogs.size

    class DogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dogImage: ImageView = itemView.findViewById(R.id.imageDogPhoto) // <--- CRITICAL NEW VIEW
        val name: TextView = itemView.findViewById(R.id.textDogName)
        val sex: TextView = itemView.findViewById(R.id.textSex)
        val age: TextView = itemView.findViewById(R.id.textAge)
        val bio: TextView = itemView.findViewById(R.id.textBio)
        val vacStatus: TextView = itemView.findViewById(R.id.textVac)
        val sterilStatus: TextView = itemView.findViewById(R.id.textSterlStatus)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonAdopted: Button = itemView.findViewById(R.id.buttonAdopted)
    }

    // Helper function to update data and refresh the RecyclerView
    fun updateData(newDogs: List<DogDataRecord>) {
        dogs.clear()
        dogs.addAll(newDogs)
        notifyDataSetChanged()
    }
}