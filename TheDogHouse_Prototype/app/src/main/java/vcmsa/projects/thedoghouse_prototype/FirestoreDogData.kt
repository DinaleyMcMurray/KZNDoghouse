package vcmsa.projects.thedoghouse_prototype

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName // Import this!

@Parcelize
data class FirestoreDogData(
    @DocumentId
    val documentId: String = "",
    val name: String = "",
    val breed: String = "",
    val sex: String = "",
    val bio: String = "",
    val age: Int = 0,

    // 🔥 Ensure these PropertyNames exactly match your Firestore field names 🔥
    @get:PropertyName("isVaccinated")
    val isVaccinated: Boolean = false,

    @get:PropertyName("isSterilized")
    val isSterilized: Boolean = false,

    val status: String = "Available for Adoption",
    val imageUrl: String = "",
    val dateAdded: Date? = null
) : Parcelable