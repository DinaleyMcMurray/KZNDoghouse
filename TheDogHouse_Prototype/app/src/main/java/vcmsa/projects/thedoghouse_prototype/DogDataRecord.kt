package vcmsa.projects.thedoghouse_prototype

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.PropertyName // <-- NEW IMPORT
import kotlinx.parcelize.Parcelize
import java.util.Date

// IMPORTANT: Ensure you have 'id("kotlin-parcelize")' in your app-level build.gradle.
@Parcelize
data class DogDataRecord(
    @DocumentId
    val documentId: String = "",
    val name: String = "",
    val breed: String = "",
    val sex: String = "",
    val bio: String = "",
    val age: Int = 0,

    // FIX: Add annotation for Firestore to correctly map Boolean fields
    @get:PropertyName("isVaccinated")
    val isVaccinated: Boolean = false,

    // FIX: Add annotation for Firestore to correctly map Boolean fields
    @get:PropertyName("isSterilized")
    val isSterilized: Boolean = false,

    val status: String = "Available for Adoption",
    val imageUrl: String = "",

    @ServerTimestamp
    val dateAdded: Date? = null
) : Parcelable