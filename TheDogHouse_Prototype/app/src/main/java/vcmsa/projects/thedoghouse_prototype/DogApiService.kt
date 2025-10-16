import retrofit2.http.GET
import retrofit2.http.Query
import vcmsa.projects.thedoghouse_prototype.DogDataRecord

interface DogApiService
{
    @GET("dogs/filter")
    suspend fun getFilteredDogs(
        @Query("age") age: Int? = null,
        @Query("breed") breed: String? = null,
        @Query("isVaccinated") isVaccinated: Boolean? = null,
        @Query("isSterilized") isSterilized: Boolean? = null
    ): List<DogDataRecord>
}