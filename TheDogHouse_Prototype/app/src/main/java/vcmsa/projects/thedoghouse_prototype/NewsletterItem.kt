package vcmsa.projects.thedoghouse_prototype

import java.util.Date

data class NewsletterItem(
    val title: String = "",         // Maps to 'textEventName'
    val location: String = "",      // Maps to 'textWhere'
    val date: String = "",          // Maps to 'textWhen'
    val cost: String = "",          // Maps to 'textSter'
    val description: String = "",   // Maps to 'textAbout'
    val imageUrl: String = "",      // Maps to 'imageEvent'
    val timestamp: Date? = null     // For sorting events
)