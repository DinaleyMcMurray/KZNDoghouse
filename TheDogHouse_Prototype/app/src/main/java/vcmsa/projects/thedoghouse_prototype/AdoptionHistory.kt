package vcmsa.projects.thedoghouse_prototype

import java.util.Date

// This data class combines information from the Adoption record and the linked Dog record
data class AdoptionHistory(
    // Dog Details (from Dogs collection via dogId)
    val dogName: String = "",
    val sex: String = "",
    val age: String = "",

    // Adoption/Client Details (from Users/{userId}/Adoption/{id} collection)
    // NOTE: This assumes you save owner/client details in the Adoption document OR a separate Users collection.
    // For this implementation, we are only getting the necessary metadata to complete the card view.
    val ownerName: String = "",
    val documentUrl: String = "",
    val uploadDate: Date? = null,

    // For filtering/logic, keep the primary keys
    val documentId: String = "" // The ID of the Adoption document
)