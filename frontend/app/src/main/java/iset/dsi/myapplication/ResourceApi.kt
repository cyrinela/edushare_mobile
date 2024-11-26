import iset.dsi.myapplication.Category
import iset.dsi.myapplication.Notification
import iset.dsi.myapplication.Resource
import iset.dsi.myapplication.ResourceDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ResourceApi {

    @GET("/categories")  // Remplacez par l'URL de votre endpoint
    fun getCategories(): Call<List<Category>>  // Liste des objets Category

    @DELETE("categories/delete/{id}")
    fun deleteCategory(@Path("id") id: Int): Call<Void>

    @PUT("categories/update/{id}")
    fun updateCategory(@Path("id") id: Int, @Body resource: Category): Call<Void>

    @GET("ressources")
    fun getResources(): Call<List<Resource>>

    @DELETE("ressources/{id}")
    fun deleteResource(@Path("id") id: Int): Call<Void>

    @Multipart
    @POST("ressources/add")
    suspend fun addResource(
        @Part("ressource") resource: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Void>


    @PUT("ressources/{id}")
    fun updateResource(@Path("id") id: Int, @Body resource: Resource): Call<Resource>


    @GET("/ressources/all")
    fun getResources2(): Call<List<ResourceDto>>

    // Mettre à jour une ressource
    @PUT("resources/{id}")
    fun updateResource2(
        @Path("id") id: Int,
        @Body resource: Resource
    ): Call<Resource>


    @GET("/notifications")
    fun getNotifications(): Call<List<Notification>>


}

