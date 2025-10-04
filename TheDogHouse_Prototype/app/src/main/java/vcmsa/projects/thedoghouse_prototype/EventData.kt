// EventData.kt
package vcmsa.projects.thedoghouse_prototype

import com.google.firebase.firestore.DocumentId
import java.util.Date
import android.os.Parcelable // <-- NEW IMPORT
import kotlinx.parcelize.Parcelize // <-- NEW IMPORT (Requires plugin)

@Parcelize // <-- NEW ANNOTATION
data class EventData(
    @DocumentId
    val documentId: String = "", // Crucial for identifying which document to UPDATE
    val name: String = "",
    val location: String = "",
    val dateAndTime: String = "",
    val cost: String = "",
    val about: String = "",
    val needsRsvp: Boolean = false,
    val dateCreated: Date? = null,
    val imageUrl: String = ""
) : Parcelable // <-- NEW INHERITANCE