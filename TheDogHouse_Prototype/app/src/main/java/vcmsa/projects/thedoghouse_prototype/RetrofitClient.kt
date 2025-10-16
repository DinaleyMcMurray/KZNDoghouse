import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit // Import the necessary class

object RetrofitClient {

    // Use the base URL of your Render service
    private const val BASE_URL = "https://kzndoghouseapi.onrender.com/"

    // 1. Create a custom OkHttpClient with extended timeouts
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        // Set connection timeout (how long to wait to establish a connection)
        .connectTimeout(30, TimeUnit.SECONDS)
        // Set read timeout (how long to wait for data after connection is established)
        .readTimeout(30, TimeUnit.SECONDS)
        // Set write timeout (how long to wait for a request to be sent)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // 2. Attach the custom OkHttpClient to Retrofit
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val dogApiService: DogApiService by lazy {
        retrofit.create(DogApiService::class.java)
    }
}