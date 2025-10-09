package vcmsa.projects.thedoghouse_prototype

import java.util.Date
data class AdoptionHistory(
    val dogName: String = "",
    val sex: String = "",
    val age: String = "",
    val ownerName: String = "",
    val documentUrl: String = "",
    val uploadDate: Date? = null,
    val documentId: String = "",
    val userId: String = ""
)