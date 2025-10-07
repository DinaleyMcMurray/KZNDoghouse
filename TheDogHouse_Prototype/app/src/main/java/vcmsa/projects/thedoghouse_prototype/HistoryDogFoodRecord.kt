package vcmsa.projects.thedoghouse_prototype

data class HistoryDogFoodRecord (
    val dogFoodName: String? = null,
    val donorName: String? = null,
    val dropOffDate: String? = null,
    val dropOffTime: String? = null,
    val timestamp: com.google.firebase.Timestamp? = null
)