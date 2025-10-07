
data class VolunteerRecord(
    val documentId: String = "", // <-- 🔥 ADD THIS FIELD 🔥
    val userId:String = "",
    val name: String,
    val gender: String,
    val age: String,
    val contactNumber: String,
    val email: String
)