package vcmsa.projects.thedoghouse_prototype

data class HistoryMedsRecord (
    val donorName: String? = null,
    val dropOffDate: String? = null,
    val dropOffTime: String? = null,
    val medicationName: String? = null,
    val quantity: String? = null,
    val timestamp: com.google.firebase.Timestamp? = null
)