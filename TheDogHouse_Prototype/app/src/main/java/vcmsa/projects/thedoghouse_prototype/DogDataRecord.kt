package vcmsa.projects.thedoghouse_prototype

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName
import java.util.Date // or String, see note below

// IMPORTANT: Ensure you have 'id("kotlin-parcelize")' in your app-level build.gradle.
@Parcelize
data class DogDataRecord(
    // Since your API returns 'documentId', match the name exactly
    val documentId: String = "",
    val name: String = "",
    val breed: String = "",
    val sex: String = "",
    val bio: String = "",
    val age: Int = 0,
    val isVaccinated: Boolean = false,
    val isSterilized: Boolean = false,
    val status: String = "Available for Adoption",
    val imageUrl: String = "",
    val dateAdded: String = ""
) : Parcelable