package vcmsa.projects.thedoghouse_prototype

data class DonationRecord(
    val donorName: String,
    val donationType: String,
    val amount: Int,
    val date: String
)
