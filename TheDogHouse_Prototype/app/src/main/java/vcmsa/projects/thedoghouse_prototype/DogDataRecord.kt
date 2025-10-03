import java.util.Date
import com.google.firebase.firestore.ServerTimestamp // Recommended for Date

data class DogDataRecord(
    // Fields must match the keys used in your HashMap EXACTLY (case sensitive!)
    val name: String = "",
    val breed: String = "",
    val sex: String = "",
    val bio: String = "",
    val age: Int = 0,
    val isVaccinated: Boolean = false,
    val isSterilized: Boolean = false,
    val status: String = "Available for Adoption",
    val imageUrl: String = "", // Cloudinary URL
    // Use this annotation if you want Firestore to set the timestamp automatically
    @ServerTimestamp
    val dateAdded: Date? = null
)