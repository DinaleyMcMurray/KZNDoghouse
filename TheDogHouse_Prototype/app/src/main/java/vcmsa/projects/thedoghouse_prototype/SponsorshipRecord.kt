package vcmsa.projects.thedoghouse_prototype

import com.google.firebase.Timestamp

// Data class to match the fields saved in the "Sponsors" subcollection
data class SponsorshipRecord(
    // Details pulled from the form
    val sponsorName: String = "",
    val sponsorAge: Int = 0,
    val sponsorMobile: String = "",
    val amount: String = "",
    val dogId: String? = null,
    val dogName: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val type: String = "Sponsorship",
    val timestamp: Timestamp? = null,
    val dateSubmitted: Timestamp? = null,
    var recordId: String = ""
)