package vcmsa.projects.thedoghouse_prototype

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DogAdapterPublic(private val dogs: MutableList<DogDataRecord>, private val context: Context) :
    RecyclerView.Adapter<DogAdapterPublic.DogPublicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogPublicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewadoptionrecyclerview, parent, false)
        return DogPublicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogPublicViewHolder, position: Int) {
        val dog = dogs[position]

        // 1. Load Image
        if (dog.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(dog.imageUrl)
                .placeholder(R.drawable.bonebutton)
                .into(holder.dogImage)
        }

        // 2. Set Text Fields
        holder.name.text = "Name: ${dog.name}"
        holder.sex.text = "Sex: ${dog.sex}"
        holder.breed.text = "Breed: ${dog.breed}"
        holder.age.text = "Age: ${dog.age}"
        holder.vacStatus.text = "Vaccination Status: ${if (dog.isVaccinated) "Complete" else "Incomplete"}"
        holder.sterStatus.text = "Sterilization Status: ${if (dog.isSterilized) "Yes" else "No"}"
        holder.bio.text = "Bio: ${dog.bio}"

        // 3. Handle Adopt Button Click - PASS THE DOG'S ID
        holder.adoptButton.setOnClickListener {
            // Assuming DogDataRecord has a documentId field populated by ViewAdoptionActivity
            val dogId = dog.documentId

            if (dogId.isNotEmpty()) {
                val intent = Intent(context, AdoptionActivity::class.java).apply {
                    // Pass the dog's unique ID to AdoptionActivity
                    putExtra("DOG_ID_FOR_ADOPTION", dogId)
                }
                context.startActivity(intent)
                Toast.makeText(context, "Applying for ${dog.name}", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("DogAdapterPublic", "Cannot start adoption: Dog documentId is missing.")
                Toast.makeText(context, "Error starting adoption process (Dog ID missing).", Toast.LENGTH_LONG).show()
            }
        }

        holder.sponsorButton.setOnClickListener {
            Toast.makeText(context, "Clicked Sponsor for ${dog.name}", Toast.LENGTH_SHORT).show()
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
        val age: TextView = itemView.findViewById(R.id.textAge)
        val vacStatus: TextView = itemView.findViewById(R.id.textVac)
        val sterStatus: TextView = itemView.findViewById(R.id.textSter)
        val bio: TextView = itemView.findViewById(R.id.textBio)

        val adoptButton: ImageButton = itemView.findViewById(R.id.adoptButton1)
        val sponsorButton: ImageButton = itemView.findViewById(R.id.adoptButton)
    }
}