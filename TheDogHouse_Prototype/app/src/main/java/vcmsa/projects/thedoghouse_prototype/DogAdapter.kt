package vcmsa.projects.thedoghouse_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DogAdapter(
    private val dogs: MutableList<DogDataRecord>,
    private val onEditClick: (DogDataRecord) -> Unit,        // <-- EDIT HANDLER
    private val onAdoptedClick: (DogDataRecord) -> Unit      // <-- STATUS TOGGLE HANDLER
) : RecyclerView.Adapter<DogAdapter.DogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerdogmanagement, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dog = dogs[position]
        val context = holder.itemView.context

        holder.name.text = "Dog's Name: ${dog.name}"
        holder.sex.text = "Dog's Sex: ${dog.sex}"
        holder.age.text = "Age: ${dog.age}"
        holder.bio.text = "Bio: ${dog.bio}"
        // Displaying the Boolean fields correctly
        holder.vacStatus.text = "Vac Status: ${if (dog.isVaccinated) "Yes" else "No"}"
        holder.sterilStatus.text = "Sterilization Status: ${if (dog.isSterilized) "Yes" else "No"}"

        // Update button text based on status
        holder.buttonAdopted.text = if (dog.status == "Available for Adoption") "Mark Adopted" else "Mark Available"

        // 1. Load the Image using the Cloudinary URL
        if (dog.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(dog.imageUrl)
                .placeholder(R.drawable.fundsdonation)
                .into(holder.dogImage)
        } else {
            holder.dogImage.setImageResource(R.drawable.fundsdonation)
        }

        // Set click listeners
        holder.buttonEdit.setOnClickListener {
            onEditClick(dog)
        }

        holder.buttonAdopted.setOnClickListener {
            onAdoptedClick(dog)
        }
    }

    override fun getItemCount(): Int = dogs.size

    class DogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dogImage: ImageView = itemView.findViewById(R.id.imageDogPhoto)
        val name: TextView = itemView.findViewById(R.id.textDogName)
        val sex: TextView = itemView.findViewById(R.id.textSex)
        val age: TextView = itemView.findViewById(R.id.textAge)
        val bio: TextView = itemView.findViewById(R.id.textBio)
        val vacStatus: TextView = itemView.findViewById(R.id.textVac)
        val sterilStatus: TextView = itemView.findViewById(R.id.textSterlStatus)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonAdopted: Button = itemView.findViewById(R.id.buttonAdopted)
    }

    fun updateData(newDogs: List<DogDataRecord>) {
        dogs.clear()
        dogs.addAll(newDogs)
        notifyDataSetChanged()
    }
}