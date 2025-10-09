// HistoryFundsRecord.kt (UPDATED)

package vcmsa.projects.thedoghouse_prototype

import com.google.firebase.firestore.DocumentId

data class HistoryFundsRecord (
    // ⚡️ ADDED: This field stores the unique Firestore Document ID
    @DocumentId
    val documentId: String = "",
    val userId: String? = null,
    val donorName: String? = null,
    val amount: String? = null,
    val dateSubmitted: Any? = null,
    val status: String? = null,
    val type: String? = null
)