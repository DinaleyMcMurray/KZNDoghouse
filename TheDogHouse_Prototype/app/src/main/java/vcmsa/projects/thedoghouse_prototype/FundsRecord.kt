package vcmsa.projects.thedoghouse_prototype

data class FundsRecord(
    val amount: String? = null,
    val dateSubmitted: Any? = null, // Use Any? to handle Timestamp or String
    val status: String? = null,
    val type: String? = null,
    val userId: String? = null
)
