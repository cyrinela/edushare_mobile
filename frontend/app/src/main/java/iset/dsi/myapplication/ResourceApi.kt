import iset.dsi.myapplication.Category
import iset.dsi.myapplication.Resource
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ResourceApi {

    @GET("/categories")  // Remplacez par l'URL de votre endpoint
    fun getCategories(): Call<List<Category>>  // Liste des objets Category

    @GET("ressources")
    fun getResources(): Call<List<Resource>>

    @DELETE("ressources/{id}")
    fun deleteResource(@Path("id") id: Int): Call<Void>

    @POST("ressources/add")
    suspend fun addResource(@Body resource: Resource): Response<String>

    @PUT("ressources/{id}")
    fun updateResource(@Path("id") id: Int, @Body resource: Resource): Call<Resource>
}
