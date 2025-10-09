package vcmsa.projects.thedoghouse_prototype

import java.util.Date

data class NewsletterItem(
    val title: String = "",
    val location: String = "",
    val date: String = "",
    val cost: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val needsRsvp: Boolean = false,
    val timestamp: Date? = null
)