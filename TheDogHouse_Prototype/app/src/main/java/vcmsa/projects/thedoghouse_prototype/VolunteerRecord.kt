package vcmsa.projects.thedoghouse_prototype

data class VolunteerRecord(
    val name: String,
    val sex: String,
    val age: String,
    val hours: String,
    val contactNumber: String,
    val email: String,
    val role: String   // keep role, remove date
)
