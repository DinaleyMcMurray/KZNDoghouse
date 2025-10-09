package vcmsa.projects.thedoghouse_prototype

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class HistoryMedsRecord (
    @DocumentId
    val documentId: String = "",
    val userId: String? = null,
    val donorName: String? = null,
    val dropOffDate: String? = null,
    val dropOffTime: String? = null,
    val medicationName: String? = null,
    val quantity: String? = null,
    val timestamp: Timestamp? = null
)