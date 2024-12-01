import iset.dsi.myapplication.Category
import iset.dsi.myapplication.FileMetaData
import iset.dsi.myapplication.Notification
import iset.dsi.myapplication.Resource
import iset.dsi.myapplication.ResourceDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ResourceApi {

    // Endpoint pour récupérer le nombre total de catégories
    @GET("/categories/total-categories")
    fun getTotalCategoriesCount(): Call<Long>

    // Endpoint pour récupérer le nombre total de ressources
    @GET("ressources/total-resources")
    fun getTotalResourcesCount(): Call<Long>


    @GET("ressources/category/{categoryId}")
    fun getResourcesByCategory(@Path("categoryId") categoryId: Int): Call<List<Resource>>

    @GET("/categories")
    fun getCategories(): Call<List<Category>>

    @DELETE("categories/delete/{id}")
    fun deleteCategory(@Path("id") id: Int): Call<Void>


    @PUT("categories/update/{id}")
    fun updateCategory(@Path("id") id: Int, @Body resource: Category): Call<Category>

    @POST("categories")
    fun addCategory(@Body category: Category): Call<Category>

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


    @GET("ressources/user/{userId}")
    fun getResourcesByUser(@Path("userId") userId: Long): Call<List<Resource>>



    @GET("ressources/download/{fileId}")
    fun downloadFile(@Path("fileId") fileId: Int): Call<ResponseBody>



    @GET("ressources/{id}/metadata")
    fun getResourceMetaData(@Path("id") resourceId: Int): Call<FileMetaData>


}



